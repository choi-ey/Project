package org.techtown.project;

public class Day {
    int count=0;
    int month;
    int date;

    public Day(int month,int date) {
        this.month = month;
        this.date = date;
        count++;
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
