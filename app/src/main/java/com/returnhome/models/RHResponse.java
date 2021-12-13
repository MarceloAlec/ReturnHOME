package com.returnhome.models;

import com.google.gson.annotations.SerializedName;
import com.returnhome.models.Client;
import com.returnhome.models.Pet;

import java.util.ArrayList;

public class RHResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("client")
    private Client client;

    @SerializedName("pet")
    private Pet pet;

    @SerializedName("pets")
    ArrayList<Pet> pets = new ArrayList<Pet>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
}
