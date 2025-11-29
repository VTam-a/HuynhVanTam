package com.mycompany.huynhvantam;

import java.io.*;
import java.util.*;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SAVE_FILE = "maze_save.dat";
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    
    private String playerName;
    private int currentLevel;
    private long startTime;
    
    public GameState(String playerName) {
        this.playerName = playerName;
        this.currentLevel = 1;
        this.startTime = System.currentTimeMillis();
    }
    
    public String getPlayerName() { return playerName; }
    public int getCurrentLevel() { return currentLevel; }
    public long getPlayTime() { return System.currentTimeMillis() - startTime; }
    
    public void nextLevel() { currentLevel++; }
    
    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(this);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static GameState loadGame() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) ois.readObject();
        }
    }
    
    public void saveToLeaderboard() {
        if (currentLevel >= 6) {
            List<LeaderboardEntry> entries = loadLeaderboard();
            long completionTime = System.currentTimeMillis() - startTime;
            LeaderboardEntry newEntry = new LeaderboardEntry(playerName, completionTime);
            entries.add(newEntry);
            entries.sort((a, b) -> Long.compare(a.time, b.time));
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
                for (LeaderboardEntry entry : entries) {
                    writer.println(entry.name + "|" + entry.time);
                }
                writer.flush();
                System.out.println("Da luu vao leaderboard.txt");
                System.out.println("Người chơi: " + playerName);
                System.out.println("Thời gian: " + formatTime(completionTime));
            } catch (IOException e) {
                System.err.println("LOI khi luu leaderboard:");
                e.printStackTrace();
            }
        }
    }
    
    public static List<LeaderboardEntry> loadLeaderboard() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return entries;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String name = parts[0];
                    long time = Long.parseLong(parts[1]);
                    entries.add(new LeaderboardEntry(name, time));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return entries;
    }
    
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

class LeaderboardEntry {
    String name;
    long time;
    
    public LeaderboardEntry(String name, long time) {
        this.name = name;
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + ", Time: " + GameState.formatTime(time);
    }
}