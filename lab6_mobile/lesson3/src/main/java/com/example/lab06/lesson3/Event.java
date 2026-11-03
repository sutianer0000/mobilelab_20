package com.example.lab06.lesson3;

public class Event {
    public long id;
    public String name;
    public String place;
    public String date;
    public String time;
    public boolean enabled;

    public Event(long id, String name, String place, String date, String time, boolean enabled) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.date = date;
        this.time = time;
        this.enabled = enabled;
    }
}
