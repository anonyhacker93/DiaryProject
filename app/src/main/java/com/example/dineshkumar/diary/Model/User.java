package com.example.dineshkumar.diary.Model;

/**
 * Created by Dinesh Kumar on 2/26/2018.
 */

public class User {
    private String uname;
    private String password;

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
}
