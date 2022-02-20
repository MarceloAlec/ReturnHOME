package com.returnhome.models;

import java.util.Map;

public class FCMCuerpo {

    private String to;
    private String priority;
    private String ttl;
    Map<String, String> data;

    public FCMCuerpo(String to, String priority, Map<String, String> data) {
        this.to = to;
        this.priority = priority;
        this.data = data;

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

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}
