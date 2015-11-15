package ru.spbau.mit;

import java.util.*;


public class GameServerImpl implements GameServer {
    private Game plugin;
    private int lastId = 0;
    private Map<String, Connection> connections = new HashMap<>();
    public GameServerImpl(String gameClassName, Properties properties) {
        try {
            this.plugin = (Game) Class.forName(gameClassName).getConstructor(GameServer.class).newInstance(this);
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                if (value.matches("-?[0-9]+")) {
                    this.plugin.getClass().getMethod("set" + key.toUpperCase().charAt(0) + key.substring(1), Integer.class).
                            invoke(this.plugin, Integer.valueOf(value));
                } else {
                    this.plugin.getClass().getMethod("set" + key.toUpperCase().charAt(0) + key.substring(1), String.class).
                            invoke(this.plugin, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Wrong plugin");
            System.exit(1);
        }
    }

    @Override
    public void accept(final Connection connection) {
        synchronized (connections) {
            connections.put(String.valueOf(lastId++), connection);
        }
        Runnable task = new Runnable() {
            String id = String.valueOf(lastId - 1);

            @Override
            public void run() {
                plugin.onPlayerConnected(id);
                while (!connection.isClosed()) {
                    try {
                        if (!connection.isClosed()) {
                            String message = connection.receive(0);
                            if (message != null) {
                                plugin.onPlayerSentMsg(id, message);
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                synchronized (connections) {
                    connections.remove(id);
                }
            }
        };
        connection.send(String.valueOf(lastId - 1));
        new Thread(task).start();
    }

    @Override
    public void broadcast(String message) {
        synchronized (connections) {
            for (Connection connection : connections.values()) {
                connection.send(message);
            }
        }
    }

    @Override
    public void sendTo(String id, String message) {
        synchronized (connections) {
            connections.get(id).send(message);
        }
    }
}
