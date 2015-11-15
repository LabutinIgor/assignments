package ru.spbau.mit;

import java.util.Random;

public class SumTwoNumbersGame implements Game {
    private int first, second;
    private GameServer server;

    public SumTwoNumbersGame(GameServer server) {
        Random random = new Random();
        first = random.nextInt(1000);
        second = random.nextInt(1000);
        this.server = server;
    }

    @Override
    public synchronized void onPlayerConnected(String id) {
        server.sendTo(id, String.valueOf(first) + " " + String.valueOf(second));
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
        if (msg.matches("-?[0-9]+")) {
            if (Integer.valueOf(msg) == first + second) {
                server.sendTo(id, "Right");
                server.broadcast(id + " won");
                Random random = new Random();
                first = random.nextInt(1000);
                second = random.nextInt(1000);
                server.broadcast(String.valueOf(first) + " " + String.valueOf(second));
            } else {
                server.sendTo(id, "Wrong");
            }
        }
    }
}
