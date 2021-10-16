package com.alec.returnhome.models;

public class Pet {

    String name;
    String breed;
    String gender;
    String description;


    public Pet(String name, String breed, String gender, String description) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
