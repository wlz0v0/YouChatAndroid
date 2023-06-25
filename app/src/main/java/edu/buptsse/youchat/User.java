package edu.buptsse.youchat;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 500384L;
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String id;
    public String name;
    public String password;
}