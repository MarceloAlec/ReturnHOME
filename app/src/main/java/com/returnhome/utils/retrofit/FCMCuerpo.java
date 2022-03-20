package com.returnhome.utils.retrofit;

import java.util.ArrayList;
import java.util.Map;

public class FCMCuerpo {

    //ATRIBUTO QUE PERMITE ESPECIFICAR EL NOMBRE DEL TEMA AL QUE SE ENVIA UN MENSAJE
    private String to;
    //LISTA DE TOKENS A LOS QUE SE ENVIARAN UN MENSAJE
    private ArrayList<String> registration_ids;
    //ESTABLECE LA PRIORIDAD DEL ENVIO DE MENSAJE
    private String priority;
    //VARIBLE QUE INCLUIRA EL CONTENIDO DEL MENSAJE COMO TITULO, CUERPO, ETC
    Map<String, String> data;

    public FCMCuerpo(String to, String priority, Map<String, String> data) {
        this.to = to;
        this.priority = priority;
        this.data = data;
    }

    public FCMCuerpo(ArrayList<String> registration_ids, String priority, Map<String, String> data) {
        this.registration_ids = registration_ids;
        this.priority = priority;
        this.data = data;
    }

    public ArrayList<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(ArrayList<String> registration_ids) {
        this.registration_ids = registration_ids;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public String getPriority() {
        return priority;
    }

    public void setTo( String to ) {
        this.to = to;
    }

    public void setPriority( String priority ) {
        this.priority = priority;
    }

}
