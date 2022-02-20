package com.returnhome.models;

public class Token {

    private String token;
    private int idCliente;

    public Token(String token, int idCliente) {
        this.token = token;
        this.idCliente = idCliente;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
