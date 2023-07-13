package de.nmauer.data.entity.timeMapping;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

public class WorkingHour {

    private int id;

    private long user_id;

    private Timestamp loginDate, logoutDate;

    private int day, month, year;

    public WorkingHour(int id, long user_id, Timestamp loginDate, Timestamp logoutDate, int day, int month, int year) {
        this.id = id;
        this.user_id = user_id;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.day = day;
        this.month = month;
        this.year = year;
    }
    public WorkingHour(long user_id, Timestamp loginDate, Timestamp logoutDate, int day, int month, int year) {
        this.id = -1;
        this.user_id = user_id;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    private Calendar roundTime(long millis) {
        Date whateverDateYouWant = new Date(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whateverDateYouWant);

        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 15 ? -mod : (30-mod));
        return calendar;
    }

    public double getWorkingTime(){
        return (double) Math.round((float) roundTime((logoutDate.getTime() - loginDate.getTime())).getTimeInMillis() / 3600000 * 100) / 100;
    }

    public long getUser_id() {
        return user_id;
    }

    public int getDay(){
        return day;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }

    public Timestamp getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Timestamp logoutDate) {
        this.logoutDate = logoutDate;
    }
}
