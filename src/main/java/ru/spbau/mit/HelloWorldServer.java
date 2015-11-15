package ru.spbau.mit;


public class HelloWorldServer implements Server {

    @Override
    public void accept(final Connection connection) {
        Runnable hello_world_task = new Runnable() {
            @Override
            public void run() {
                connection.send("Hello world");
                connection.close();
            }
        };
        new Thread(hello_world_task).start();
    }
}
