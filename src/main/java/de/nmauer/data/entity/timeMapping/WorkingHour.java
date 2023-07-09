package de.nmauer.data.entity.timeMapping;

import java.util.HashMap;

public class WorkingHour {

    private int id;
    private long user_id;
    private int year, month, day, minutes;

    private HashMap<Integer, String> monthNames;

    public WorkingHour(int id, long user_id, int year, int month, int day, int minutes) {
        this.id = id;
        this.user_id = user_id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.minutes = minutes;

        this.monthNames = new HashMap<>();
        monthNames.put(1, "Januar");
        monthNames.put(2, "Februar");
        monthNames.put(3, "März");
        monthNames.put(4, "April");
        monthNames.put(5, "Mai");
        monthNames.put(6, "Juni");
        monthNames.put(7, "Juli");
        monthNames.put(8, "August");
        monthNames.put(9, "September");
        monthNames.put(10, "Oktober");
        monthNames.put(11, "November");
        monthNames.put(12, "Dezember");
    }

    public int getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonthName(){
        return monthNames.get(month);
    }

}
