package com.turjo2207093.lifetracker;

public class User {
    private String name;
    private int level;

    public User() {
    }

    public User(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
