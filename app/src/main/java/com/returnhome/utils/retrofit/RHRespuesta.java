package com.returnhome.utils.retrofit;


import com.returnhome.models.Cliente;
import com.returnhome.models.Mascota;
import com.returnhome.models.Token;

import java.util.ArrayList;

public class RHRespuesta {

    private Cliente cliente;

    private Mascota mascota;

    ArrayList<Mascota> mascotas = new ArrayList<Mascota>();

    ArrayList<Token> tokens = new ArrayList<Token>();

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public ArrayList<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(ArrayList<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
}
