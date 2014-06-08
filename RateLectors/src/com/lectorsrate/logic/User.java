package com.lectorsrate.logic;


public class User {
    private String  name;
    private String  email;

    User() {
        name = "";
        email = "";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
