package com.ssdiscusskiny.data;

import java.text.ParseException;
import java.util.List;

public class ParentItem {
    private final String parentTitle;
    private final List<ChildItem> childList;

    public ParentItem(String parentTitle, List<ChildItem> childList){
        this.parentTitle = parentTitle;
        this.childList = childList;
    }

    public String getParentTitle(){
        return parentTitle;
    }

    public List<ChildItem> getChildList(){
        return childList;
    }

    public int getParentKey(){
        int key = 0;
        try{
            key = Integer.parseInt(parentTitle);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return key;
    }

}
