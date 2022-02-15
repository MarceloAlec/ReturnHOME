package com.returnhome.utils.retrofit;

import com.google.gson.annotations.SerializedName;
import com.returnhome.modelos.Cliente;
import com.returnhome.modelos.Mascota;


import java.util.ArrayList;

public class RHResponse {

    @SerializedName("client")
    private Cliente cliente;

    @SerializedName("mascota")
    private Mascota mascota;

    @SerializedName("mascotas")
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
