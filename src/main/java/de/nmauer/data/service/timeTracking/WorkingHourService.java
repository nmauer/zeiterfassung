package de.nmauer.data.service.timeTracking;

import de.nmauer.data.entity.timeMapping.WorkingHour;
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
                        rs.getLong("user_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day"),
                        rs.getInt("minutes")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByUserId(int id){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE user_id=%s", id),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getLong("user_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day"),
                        rs.getInt("minutes")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByYear(int year){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE year=%s", year),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getLong("user_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day"),
                        rs.getInt("minutes")
                )
        );
    }

    public List<WorkingHour> getWorkingHourByYearAndMonth(int year, int month){
        return jdbcTemplate.query(String.format("SELECT * FROM working_hours WHERE year=%s AND month=%s", year, month),  (rs, rowNum) ->
                new WorkingHour(
                        rs.getLong("user_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day"),
                        rs.getInt("minutes")
                )
        );
    }

}
