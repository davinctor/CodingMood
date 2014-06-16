package com.lectorsrate.logic;


public class User {
	private int		id;
	private boolean admin;

    User() {
    	id = 0;
    	admin = false;
    }
    
    public boolean isAdmin() {
    	return admin;
    }
    public void setAdmin(boolean admin) {
    	this.admin = admin;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
