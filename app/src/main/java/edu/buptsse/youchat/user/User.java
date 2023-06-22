package edu.buptsse.youchat.user;

public class User {
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String id;
    public String name;
    public String password;
}