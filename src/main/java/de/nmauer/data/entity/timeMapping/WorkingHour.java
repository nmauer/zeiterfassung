package de.nmauer.data.entity.timeMapping;

import de.nmauer.data.DateType;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WorkingHour {

    private int id;

    private long user_id;

    private Timestamp loginDate, logoutDate;
    private DateType dateType;
    private int day, month, year;
    private HashMap<Integer, String> monthNames;

    public WorkingHour(int id, long user_id, Timestamp loginDate, Timestamp logoutDate, int day, int month, int year, DateType dateType) {
        this.id = id;
        this.user_id = user_id;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.day = day;
        this.month = month;
        this.year = year;
        this.dateType = dateType;


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

    public DateType getDateType() {
        return dateType;
    }
    public String getDateTypeName() {
        return dateType.getTitle();
    }


    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }

    public WorkingHour(long user_id, Timestamp loginDate, Timestamp logoutDate, int day, int month, int year, DateType dateType) {
        this.id = -1;
        this.user_id = user_id;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.day = day;
        this.month = month;
        this.year = year;
        this.dateType = dateType;

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

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDate(){
        String date = "";
        if(day <= 9){
            date += "0" + day;
        }else{
            date += day;
        }
        date +=".";
        if(month <= 9){
            date += "0" + month;
        }else{
            date += month;
        }
        date +=  "."+ getYear();
        return date;
    }

    public String getMonthName(){
        return monthNames.get(month);
    }

    public void setLogoutDate(Timestamp logoutDate) {
        this.logoutDate = logoutDate;
    }

    public String getPauseTime(){
        return getWorkingTime()<6 ? "0m": "30m";
    }

    public String getBeginFormatted(){
        return new SimpleDateFormat("HH:mm").format(new Date(loginDate.getTime())) + " Uhr";
    }

    public String getEndFormatted(){
        return new SimpleDateFormat("HH:mm").format(new Date(logoutDate.getTime())) + " Uhr";
    }
    public String getWorkingTimeFormatted(){
        return getWorkingTime() + "h";
    }
}
