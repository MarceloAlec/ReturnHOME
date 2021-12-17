package com.returnhome.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pet implements Serializable {


    private int id;
    private String name;
    private String breed;
    private char gender;
    private String description;
    private boolean missing;
    private int id_client;


    public Pet(int id, String name, String breed, char gender, String description) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
    }

    public Pet(String name, String breed, char gender, String description, int idClient) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
        this.id_client = idClient;
    }


    public Pet(int id, boolean missing) {
        this.id = id;
        this.missing = missing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public String toString() {
        return name;
    }


}
