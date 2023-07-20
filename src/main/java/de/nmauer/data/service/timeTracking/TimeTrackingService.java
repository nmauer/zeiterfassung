package de.nmauer.data.service.timeTracking;

import de.nmauer.data.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Component
public class TimeTrackingService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isUserLoggedIn(long id){
       return jdbcTemplate.query(String.format("SELECT * FROM user_login WHERE user_id='%s'", id), (rs, rowNum) -> rs).size() > 0;
    }

    public Timestamp getLogInTime(long id){
        if(isUserLoggedIn(id)){
          return jdbcTemplate.query(String.format("SELECT login_time FROM user_login WHERE user_id='%s'", id), (rs, rowNum) ->
                  rs.getTimestamp("login_time")).get(0);
        }
        return null;
    }

    public void login(long id){
        jdbcTemplate.update(String.format("INSERT INTO user_login (user_id) VALUES ('%s')", id));
    }

    public void logout(long id){
        Timestamp loginTime = getLogInTime(id);
        Timestamp logoutTime = new Timestamp(System.currentTimeMillis());

        jdbcTemplate.update(String.format("INSERT INTO working_hours (user_id, login_date, logout_date, day, month, year, day_type) VALUES" +
                " ('%s', '%s', '%s', '%s','%s','%s','%s')", id, loginTime, logoutTime, loginTime.toLocalDateTime().getDayOfMonth(), loginTime.getMonth()+ 1, loginTime.getYear()+1900, DateType.WORKING_DAY));

        jdbcTemplate.update(String.format("DELETE FROM user_login WHERE user_id='%s'", id));
    }

    public Calendar roundTime(long millis) {
        Date whateverDateYouWant = new Date(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(whateverDateYouWant);

        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 15 ? -mod : (30-mod));
        return calendar;
    }

}
