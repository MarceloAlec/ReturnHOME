package com.alec.returnhome.models;

public class Client {

    private String name;
    private String email;
    private String pass;


    public Client(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.pass = password;
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

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }



}
