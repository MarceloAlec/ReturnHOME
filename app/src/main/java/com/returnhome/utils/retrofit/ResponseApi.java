package com.returnhome.utils.retrofit;

import com.google.gson.annotations.SerializedName;
import com.returnhome.models.Client;
import com.returnhome.models.Pet;

import java.util.ArrayList;

public class ResponseApi {

    @SerializedName("message")
    private String message;

    @SerializedName("client")
    private Client client;

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

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
}
