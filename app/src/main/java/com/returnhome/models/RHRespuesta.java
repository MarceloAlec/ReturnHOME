package com.returnhome.models;


import java.util.ArrayList;

public class RHRespuesta {

    private Cliente cliente;

    private Mascota mascota;

    ArrayList<Mascota> mascotas = new ArrayList<Mascota>();

    public Cliente getClient() {
        return cliente;
    }

    public void setClient(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mascota getPet() {
        return mascota;
    }

    public void setPet(Mascota mascota) {
        this.mascota = mascota;
    }

    public ArrayList<Mascota> getPets() {
        return mascotas;
    }

    public void setPets(ArrayList<Mascota> mascotas) {
        this.mascotas = mascotas;
    }
}
