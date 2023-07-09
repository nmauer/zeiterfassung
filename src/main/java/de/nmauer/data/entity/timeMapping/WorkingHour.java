package de.nmauer.data.entity.timeMapping;

public class WorkingHour {

    private long user_id;
    private int year, month, day, minutes;

    public WorkingHour(long user_id, int year, int month, int day, int minutes) {
        this.user_id = user_id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.minutes = minutes;
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
}
