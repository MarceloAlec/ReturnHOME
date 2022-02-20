package com.returnhome.models;

public class Cliente {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private String numeroCelular;

    public Cliente(String nombre, String email, String password, String phoneNumber, String token) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.numeroCelular = phoneNumber;
    }

    public Cliente(int id, String nombre, String email, String phoneNumber) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.numeroCelular = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }




}
