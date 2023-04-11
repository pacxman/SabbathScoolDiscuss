package com.ssdiscusskiny.data;

public class ChildItem {
    private final String name;
    private final String firstKey;
    private final String secondKey;

    public ChildItem(String firstKey, String secondKey, String name){
        this.name = name;
        this.firstKey = firstKey;
        this.secondKey = secondKey;
    }

    public String getName(){
        return name;
    }

    public String getFirstKey(){return firstKey;}

    public String getSecondKey(){return secondKey;}

}
