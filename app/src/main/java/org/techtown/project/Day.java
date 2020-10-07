package org.techtown.project;

import java.util.ArrayList;

public class Day {

    int month;
    int date;
    ArrayList<String> day1 = new ArrayList<>();

    public ArrayList<String> getDay1() {
        return day1;
    }

    public void setDay1(ArrayList<String> day1) {
        this.day1 = day1;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
