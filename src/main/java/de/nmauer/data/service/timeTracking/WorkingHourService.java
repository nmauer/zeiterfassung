package de.nmauer.data.service.timeTracking;

import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.entity.timeMapping.WorkingMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkingHourService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<WorkingHour> getAllWorkingHours(){
        return jdbcTemplate.query("SELECT * FROM working_hours",  (rs, rowNum) ->
                new WorkingHour(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("login_date"),
                        rs.getTimestamp("logout_date"),
                        rs.getInt("day"),
                        rs.getInt("month"),
                        rs.getInt("year")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByUserId(int id){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE user_id=%s", id),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("login_date"),
                        rs.getTimestamp("logout_date"),
                        rs.getInt("day"),
                        rs.getInt("month"),
                        rs.getInt("year")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByUserId(int userId, int month, int year){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE user_id='%s' AND month='%s' AND year='%s'", userId, month, year),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("login_date"),
                        rs.getTimestamp("logout_date"),
                        rs.getInt("day"),
                        rs.getInt("month"),
                        rs.getInt("year")
                )
        );
    }

    public List<WorkingMonth> getWorkingMonth(int userId){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE user_id='%s' GROUP BY month", userId), (rs, rowNum) ->
                new WorkingMonth(
                        userId,
                        rs.getInt("month"),
                        rs.getInt("year"),
                        getWorkingHourByUserId(userId, rs.getInt("month"), rs.getInt("year"))
                )
        );
    }

    public int calcWorkingMinutesInMonth(int userId, int month, int year){
        return jdbcTemplate.query(String.format("SELECT SUM(minutes) FROM working_hours WHERE user_id='%s' AND year='%s' AND month='%s'",
                userId, year, month), (rs, rowNum) -> rs.getInt("SUM(minutes)")).get(0);
    }

    public List<WorkingHour> getWorkingHourByYear(int year){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE year=%s", year),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("login_date"),
                        rs.getTimestamp("logout_date"),
                        rs.getInt("day"),
                        rs.getInt("month"),
                        rs.getInt("year")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByYearAndMonth(int year, int month){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE year=%s AND month=%s", year, month),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("login_date"),
                        rs.getTimestamp("logout_date"),
                        rs.getInt("day"),
                        rs.getInt("month"),
                        rs.getInt("year")
                )
        );
    }

    public void update(WorkingHour workingHour){
        jdbcTemplate.update(String.format("UPDATE working_hours SET year='%s', month='%s', day='%s', minutes='%s' WHERE id='%s'",
                workingHour.getYear(), workingHour.getMonth(), workingHour.getDay(), workingHour.getWorkingTime(), workingHour.getId()));
    }

}
