package org.techtown.project;

import java.util.List;

public class Parent {
    public String parentData;
    public List<Child> items;

    public void setData(String parentData){
        this.parentData = parentData;
    }

    public void setItems(List<Child> items){
        this.items = items;
    }

    public String getData(){
        return this.parentData;
    }

    public List<Child> getItems(){
        return this.items;
    }
}
