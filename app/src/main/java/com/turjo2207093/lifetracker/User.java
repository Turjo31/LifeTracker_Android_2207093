package com.turjo2207093.lifetracker;

public class User {
    private String name; // Changed from username
    private int level;

    // Add a no-argument constructor
    public User() {
    }

    public User(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() { // Changed from getUsername
        return name;
    }

    public int getLevel() {
        return level;
    }
}
