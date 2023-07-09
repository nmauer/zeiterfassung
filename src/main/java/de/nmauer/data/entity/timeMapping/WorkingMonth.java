package de.nmauer.data.entity.timeMapping;

import java.util.HashMap;
import java.util.List;

public class WorkingMonth {

    private int userId;
    private int month, year;
    private List<WorkingHour> workingHours;

    private HashMap<Integer, String> monthNames;

    public WorkingMonth(int userId, int month, int year, List<WorkingHour> workingHours){
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.workingHours = workingHours;

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

    public int getUserId() {
        return userId;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public List<WorkingHour> getWorkingHours() {
        return workingHours;
    }

    public int getWorkingHoursSum(){
        int sum = 0;
        for(WorkingHour workingHour: workingHours){
            sum += workingHour.getMinutes();
        }
        return sum;
    }

    public String getMonthName(){
        return monthNames.get(month);
    }

    public String getSorting(){
        return year + "" + month;
    }
}
