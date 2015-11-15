package ru.spbau.mit;

import java.io.*;
import java.util.*;


public class QuizGame implements Game {
    private GameServer server;
    private int maxLettersToOpen;
    private Integer openedLetters = 0;
    private long delayUntilNextLetter;
    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private int currentQuestion = 0;
    private boolean isRunning = false;
    Timer currentTimer = null;

    public QuizGame(GameServer server) {
        this.server = server;

    }

    public void setDelayUntilNextLetter(Integer delayUntilNextLetter) {
        this.delayUntilNextLetter = delayUntilNextLetter;
    }

    public void setMaxLettersToOpen(Integer maxLettersToOpen) {
        this.maxLettersToOpen = maxLettersToOpen;
    }

    public void setDictionaryFilename(String dictionaryFilename) {
        try {
            Scanner in = new Scanner(new File(dictionaryFilename));
            while (in.hasNextLine()) {
                String[] parts = in.nextLine().split(";");
                questions.add(parts[0]);
                answers.add(parts[1]);
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found");
            System.exit(1);
        }
    }

    @Override
    public void onPlayerConnected(String id) {
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
        switch (msg) {
            case "!start":
                if (!isRunning) {
                    startNewRound();
                }
                break;
            case "!stop":
                isRunning = false;
                currentQuestion = (currentQuestion + 1) % questions.size();
                server.broadcast("Game has been stopped by " + id);
                if (currentTimer != null) {
                    currentTimer.cancel();
                    currentTimer = null;
                }
                break;
            default:
                if (isRunning) {
                    if (msg.equals(answers.get(currentQuestion))) {
                        server.broadcast("The winner is " + id);
                        currentQuestion = (currentQuestion + 1) % questions.size();
                        startNewRound();
                    } else {
                        server.sendTo(id, "Wrong try");
                    }
                }
        }
    }

    private synchronized void startNewRound() {
        isRunning = true;
        openedLetters = 0;
        server.broadcast("New round started: " + questions.get(currentQuestion) + " (" +
                String.valueOf(answers.get(currentQuestion).length()) + " letters)");
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (openedLetters == maxLettersToOpen) {
                    server.broadcast("Nobody guessed, the word was " + answers.get(currentQuestion));
                    currentQuestion = (currentQuestion + 1) % questions.size();
                    startNewRound();
                } else {
                    openedLetters++;
                    server.broadcast("Current prefix is " + answers.get(currentQuestion).substring(0, openedLetters));
                }
            }
        };
        currentTimer = new Timer();
        currentTimer.schedule(task, delayUntilNextLetter, delayUntilNextLetter);
    }
}
