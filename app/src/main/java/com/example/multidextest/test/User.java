package com.example.multidextest.test;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;


public class User implements Dao {
    private String name;
    private String password;
    private int age;
    private Group group;

    private static class Email {
        public String address;
        public boolean isValid;
    }

    private class Company {
        public String name;
        public String city;
        public String street;
    }

    public User() {
    }

    public User(String name, String password, int age) {
        this.name = name;
        this.password = password;
        this.age = age;
    }
    @MyTest
    public String getName() {
        return name;
    }

    @MyTest
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public void save() {
        System.out.println("Get connection");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Save success");
    }

    @Override
    public void delete() {

    }

    @Override
    public List<User> queryAll() {
        return new ArrayList<>();
    }

    @Override
    public List<User> query(HashMap<String, String> paras) {
        return new LinkedList<>();
    }

    @Override
    public void update(String password, int age) {
        TreeSet<User> set = new TreeSet<>();

    }

    public static void main(String[] args) {
        System.out.println("Test User");
    }

    public boolean isLogin() {
        return TextUtils.isEmpty(name);
    }
}
