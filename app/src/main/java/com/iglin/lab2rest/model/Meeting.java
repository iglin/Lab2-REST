package com.iglin.lab2rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

import java.text.ParseException;
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
    private String startTime;
    private String endTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
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
        String result = description + "\n\n"
                + priority + "\n\n"
                + "From: " + DateTimeFormatter.getFullFormat(startTime) + "\n"
                + "To: " + DateTimeFormatter.getFullFormat(endTime);
        if (participants == null) return result;
        result += "\n\nParticipants: ";
        for (Participant participant : participants.values()) {
            result += "\n" + participant.getName() + ", " + participant.getPosition();
        }
        return result;
    }
}
