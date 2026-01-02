package com.turjo2207093.lifetracker;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class User {
    @Exclude
    public String uid;

    public String name;
    public String gender;
    public String age;
    public String email;
    public int level;
    public int exp;
    public Map<String, String> habits;

    public User() {
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, String> getHabits() {
        return habits;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
