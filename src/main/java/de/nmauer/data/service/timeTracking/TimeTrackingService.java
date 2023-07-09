package de.nmauer.data.service.timeTracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        String year = new SimpleDateFormat("YYYY").format(loginTime);
        String month = new SimpleDateFormat("MM").format(loginTime);
        String day = new SimpleDateFormat("dd").format(loginTime);

        int realMinutes = (int) (System.currentTimeMillis() - loginTime.getTime())/60000;
        int minutes = (int) roundTime(System.currentTimeMillis() - loginTime.getTime()).getTimeInMillis()/60000;
        if(realMinutes <= 0){
            jdbcTemplate.update(String.format("DELETE FROM user_login WHERE user_id='%s'", id));
            return;
        }else if(realMinutes < 15){
            minutes = (int) ((System.currentTimeMillis() - loginTime.getTime())/60000);
        }
        jdbcTemplate.update(String.format("INSERT INTO working_hours (user_id, year, month, day, minutes) VALUES" +
                " ('%s', '%s', '%s', '%s', '%s')", id, year, month, day, minutes));

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
