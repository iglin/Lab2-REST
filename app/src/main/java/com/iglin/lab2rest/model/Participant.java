package com.iglin.lab2rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

/**
 * Created by user on 04.02.2017.
 */

public class Participant {
    @Exclude
    @JsonIgnore
    private String id;
    private String name;
    private String position;

    public Participant() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
