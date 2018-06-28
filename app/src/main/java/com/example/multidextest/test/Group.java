package com.example.multidextest.test;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private String id;
    private String name;
    private Set<User> users = new HashSet<>();

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }
}
