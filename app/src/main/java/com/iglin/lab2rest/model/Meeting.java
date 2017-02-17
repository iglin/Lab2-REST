package com.iglin.lab2rest.model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 18.01.2017.
 */

public class Meeting {
    @Exclude
    @JsonIgnore
    private String id;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private Priority priority;
    private Map<String, Participant> participants;

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

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEndTime() {
        return endTime.getTime();
    }

    @Exclude
    public Date getEndTimeAsDate() {
        return endTime;
    }

    @Exclude
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = new Date(endTime);
    }

    public long getStartTime() {
        return startTime.getTime();
    }

    @Exclude
    public Date getStartTimeAsDate() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = new Date(startTime);
    }

    @Exclude
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getPriority() {
        if (priority == null) return null;
        return priority.name();
    }

    public void setPriority(String priority) {
        if (priority == null) return;
        this.priority = Priority.valueOf(priority);
    }

    public Map<String, Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        if (participants == null) {
            participants = new HashMap<>();
        }
        participants.put(participant.getId(), participant);
    }

    public String representDetails() throws ParseException {
        String result = "";
        if (description != null && description.length() > 0) {
            result = description + "\n\n";
        }
        result += priority + "\n\n"
                + "From: " + DateTimeFormatter.getFullFormat(startTime) + "\n"
                + "To: " + DateTimeFormatter.getFullFormat(endTime);
        if (participants == null) return result;
        result += "\n\nParticipants: ";
        for (Participant participant : participants.values()) {
            result += "\n" + participant.getName();
            if (participant.getPosition() != null) result += ", " + participant.getPosition();
        }
        return result;
    }
}
