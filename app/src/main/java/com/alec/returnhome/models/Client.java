package com.alec.returnhome.models;

public class Client {

    private String name;
    private String email;
    private String hash_password;
    private String gender;


    public Client(String name, String email, String password, String gender) {
        this.name = name;
        this.email = email;
        this.hash_password = password;
        this.gender = gender;
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

    public String getHash_password() {
        return hash_password;
    }

    public void setHash_password(String hash_password) {
        this.hash_password = hash_password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
