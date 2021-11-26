package com.returnhome.models;


public class Pet {


    private int id;
    private String name;
    private String breed;
    private char gender;
    private String description;
    private int idClient;


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
        this.idClient = idClient;
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

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return name;
    }
}
