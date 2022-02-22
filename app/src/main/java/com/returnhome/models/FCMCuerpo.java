package com.returnhome.models;

import java.util.ArrayList;
import java.util.Map;

public class FCMCuerpo {

    private String to;
    private ArrayList<String> registration_ids;
    private String priority;
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
