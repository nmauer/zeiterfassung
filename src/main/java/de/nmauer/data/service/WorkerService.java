package de.nmauer.data.service;

import de.nmauer.data.entity.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean doesWorkerExist(int id){
        return jdbcTemplate.query(String.format("SELECT * FROM application_user au WHERE au.id='%s'", id), (rs, rowNum) ->
                rs
        ).size() > 0;
    }

    public boolean doesWorkerExist(String username){
        return jdbcTemplate.query(String.format("SELECT * FROM application_user au WHERE au.username='%s'", username), (rs, rowNum) ->
                rs
        ).size() > 0;
    }

    public List<Worker> findAllWorkers(){
        return jdbcTemplate.query("SELECT * FROM application_user au LEFT JOIN application_user_address aua on au.address_id = aua.id LEFT JOIN user_contact_informations uci on au.contact_information_id = uci.id", (rs, rowNum) ->
                new Worker(
                        rs.getInt("au.id"),
                        rs.getString("au.name"),
                        rs.getString("au.username"),
                        rs.getString("au.hashed_password"),
                        rs.getString("uci.phone_number"),
                        rs.getString("uci.mobile_number"),
                        rs.getString("uci.email"),
                        rs.getString("aua.street"),
                        rs.getString("aua.city"),
                        rs.getString("aua.zipcode")
                )
        );
    }

    public List<Worker> getWorkerById(int id){
        return jdbcTemplate.query(String.format("SELECT * FROM application_user au LEFT JOIN application_user_address aua on au.address_id = aua.id LEFT JOIN user_contact_informations uci on au.contact_information_id = uci.id WHERE id='%s'", id), (rs, rowNum) ->
                new Worker(
                        rs.getInt("au.id"),
                        rs.getString("au.name"),
                        rs.getString("au.username"),
                        rs.getString("au.hashed_password"),
                        rs.getString("uci.phone_number"),
                        rs.getString("uci.mobile_number"),
                        rs.getString("uci.email"),
                        rs.getString("aua.street"),
                        rs.getString("aua.city"),
                        rs.getString("aua.zipcode")
                )
        );
    }

    public void delete(Worker worker) {
        if(doesWorkerExist(worker.getId())){
            jdbcTemplate.update(String.format("DELETE FROM user_roles WHERE user_id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM user_login WHERE user_id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM working_hours WHERE user_id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM application_user WHERE id='%s'", worker.getId()));
        }
    }

    public void createWorker(Worker worker){
        if(worker.getId() == -1){
            jdbcTemplate.update(String.format("INSERT INTO application_user () VALUES ()"));
        }
    }
}
