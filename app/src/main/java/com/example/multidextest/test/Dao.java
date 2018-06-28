package com.example.multidextest.test;

import java.util.HashMap;
import java.util.List;

public interface Dao {
    void save();
    void delete();
    List<User> queryAll();
    List<User> query(HashMap<String, String> paras);
    void update(String password, int age);
}
