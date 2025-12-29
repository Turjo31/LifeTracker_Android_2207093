package com.turjo2207093.lifetracker;

public class User {
    private String username;
    private int level;

    public User(String username, int level) {
        this.username = username;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }
}
