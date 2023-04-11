package com.ssdiscusskiny.data;

public class Lesson{
    private final String fourthKey;
    private final String title;

    public Lesson(String key, String title){
        this.fourthKey = key;
        this.title = title;
    }

    public String getDateKey(){return fourthKey;}
    public String getTitle(){return title;}

}
