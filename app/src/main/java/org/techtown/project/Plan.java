package org.techtown.project;

import java.util.ArrayList;

public class Plan {
    String place;
    int year;
    int month;
    int sDate;
    int eDate;
    ArrayList<String> day1 = new ArrayList<>();

    public ArrayList<String> getDay1() {
        return day1;
    }

    public void setDay1(ArrayList<String> day1) {
        this.day1 = day1;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getsDate() {
        return sDate;
    }

    public void setsDate(int sDate) {
        this.sDate = sDate;
    }

    public int geteDate() {
        return eDate;
    }

    public void seteDate(int eDate) {
        this.eDate = eDate;
    }
}
