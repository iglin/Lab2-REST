package com.iglin.lab2rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

/**
 * Created by user on 18.01.2017.
 */

public class Meeting {
    @Exclude
    @JsonIgnore
    private String id;
    private String name;
    private String time;

    public Meeting() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
