package com.alec.returnhome.models;

public class Pet {

    private int idPet;
    private String name;
    private String breed;
    private char gender;
    private String description;


    public Pet(int idPet, String name, String breed, char gender, String description) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
    }

    public int getIdPet() {
        return idPet;
    }

    public void setIdPet(int idPet) {
        this.idPet = idPet;
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


}
