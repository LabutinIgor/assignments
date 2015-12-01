package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class GameServerImpl implements GameServer {
    private final Game plugin;
    private int lastId = 0;
    private final Map<String, ConnectionTask> tasks = new HashMap<>();

    private class ConnectionTask implements Runnable {
        private final Connection connection;
        private final String id;
        private final Queue<String> messagesToSend = new ConcurrentLinkedQueue<>();
        public ConnectionTask(Connection connection, String id) {
            this.connection = connection;
            this.id = id;
        }

        @Override
        public void run() {
            plugin.onPlayerConnected(id);
            while (!connection.isClosed()) {
                try {
                    synchronized (connection) {
                        if (!connection.isClosed()) {
                            String message = connection.receive(100);
                            if (message != null) {
                                plugin.onPlayerSentMsg(id, message);
                            }
                        }
                        if (!connection.isClosed()) {
                            synchronized (messagesToSend) {
                                if (!messagesToSend.isEmpty()) {
                                    connection.send(messagesToSend.poll());
                                }
                            }
                        }
                    }
                } catch(InterruptedException e){
                    break;
                }
            }
            synchronized (tasks) {
                tasks.remove(id);
            }
        }
    }

    public GameServerImpl(String gameClassName, Properties properties) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        this.plugin = (Game) Class.forName(gameClassName).getConstructor(GameServer.class).newInstance(this);
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            String methodName = "set" + key.toUpperCase().charAt(0) + key.substring(1);
            if (value.matches("-?[0-9]+")) {
                this.plugin.getClass().getMethod(methodName, Integer.class).
                        invoke(this.plugin, Integer.valueOf(value));
            } else {
                this.plugin.getClass().getMethod(methodName, String.class).
                        invoke(this.plugin, value);
            }
        }
    }

    @Override
    public void accept(final Connection connection) {
        ConnectionTask task = new ConnectionTask(connection, String.valueOf(lastId++));
        synchronized (tasks) {
            tasks.put(String.valueOf(lastId - 1), task);
        }
        connection.send(String.valueOf(lastId - 1));
        new Thread(task).start();
    }

    @Override
    public void broadcast(String message) {
        synchronized (tasks) {
            for (ConnectionTask task : tasks.values()) {
                synchronized (task.messagesToSend) {
                    task.messagesToSend.add(message);
                }
            }
        }
    }

    @Override
    public void sendTo(String id, String message) {
        synchronized (tasks) {
            ConnectionTask task = tasks.get(id);
            synchronized (task.messagesToSend) {
                task.messagesToSend.add(message);
            }
        }
    }
}
