package edu.und.seau.firebase.models.uav;

import com.google.firebase.Timestamp;

public class UavDBModel {

    private String id;
    private String name;
    private Timestamp timestamp;

    public String  getId(){ return id;}
    public String getName() {return name;}
    public Timestamp getTimestamp() { return timestamp;}
    public void setTimestamp(Timestamp timestamp) {this.timestamp = timestamp;}

    public UavDBModel(String id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
