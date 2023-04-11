package com.ssdiscusskiny.data;

public class ContentDataItem {
    private final String firstKey;
    private final String secondKey;
    private final String thirdKey;
    private final String name;


    public ContentDataItem(String firstKey, String secondKey, String thirdKey, String name){
        this.firstKey = firstKey;
        this.secondKey = secondKey;
        this.thirdKey = thirdKey;
        this.name = name;

    }

    public String getItemTitle(){ return name; }

    public String getFirstKey(){return firstKey;}

    public String getSecondKey(){return secondKey;}

    public String getThirdKey(){return thirdKey;}

}
