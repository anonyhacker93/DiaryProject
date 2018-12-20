package com.example.dineshkumar.diary.Model;

/**
 * Created by Dinesh Kumar on 2/26/2018.
 */

public class User {
    private String uname;
    private String email;
    private String password;

    public User(){}

    public User(String uname, String password) {
        this.uname = uname;
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public String getPassword() {
        return password;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


