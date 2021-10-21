package com.alec.returnhome.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("client")
    private Client client;

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
}
