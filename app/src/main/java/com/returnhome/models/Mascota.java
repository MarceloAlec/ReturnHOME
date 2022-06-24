package com.returnhome.models;


import com.returnhome.utils.retrofit.IMascotaApi;
import com.returnhome.utils.retrofit.ClienteRetrofit;

import java.io.Serializable;

import retrofit2.Call;

public class Mascota implements Serializable {


    private int idMascota;
    private String nombre;
    private String raza;
    private char genero;
    private String descripcion;
    private boolean desaparecida;
    private int idCliente;

    public Mascota() {
       
    }

    public Mascota(int idMascota, String nombre, String raza, char gender, String descripcion) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.raza = raza;
        this.genero = gender;
        this.descripcion = descripcion;
    }

    public Mascota(String nombre, String raza, char gender, String descripcion, boolean desaparecida, int idClient) {
        this.nombre = nombre;
        this.raza = raza;
        this.genero = gender;
        this.descripcion = descripcion;
        this.desaparecida = desaparecida;
        this.idCliente = idClient;
    }

    public Mascota(int idMascota, boolean desaparecida) {
        this.idMascota = idMascota;
        this.desaparecida = desaparecida;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public char getGenero() {
        return genero;
    }

    public void setGenero(char genero) {
        this.genero = genero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public boolean isDesaparecida() {
        return desaparecida;
    }

    public void setDesaparecida(boolean desaparecida) {
        this.desaparecida = desaparecida;
    }

    @Override
    public String toString() {
        return nombre;
    }






}
