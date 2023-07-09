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
        return jdbcTemplate.query("SELECT * FROM application_user au LEFT JOIN application_user_address aua on au.address_id = aua.id LEFT JOIN user_contact_information uci on au.contact_information_id = uci.id", (rs, rowNum) ->
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

    public Worker getWorkerById(int id){
        return jdbcTemplate.query(String.format("SELECT * FROM application_user au LEFT JOIN application_user_address aua on au.address_id = aua.id LEFT JOIN user_contact_informations uci on au.contact_information_id = uci.id WHERE au.id='%s'", id), (rs, rowNum) ->
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
        ).get(0);
    }

    public Worker getWorkerByUsername(String username){
        return jdbcTemplate.query(String.format("SELECT * FROM application_user au LEFT JOIN application_user_address aua on au.address_id = aua.id LEFT JOIN user_contact_informations uci on au.contact_information_id = uci.id WHERE au.id='%s'", username), (rs, rowNum) ->
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
        ).get(0);
    }

    public void delete(Worker worker) {
        if(doesWorkerExist(worker.getId())){
            jdbcTemplate.update(String.format("DELETE FROM user_roles WHERE user_id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM user_login WHERE user_id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM working_hours WHERE user_id='%s'", worker.getId()));

            int address_id = jdbcTemplate.query(String.format("SELECT address_id FROM application_user WHERE id='%s'", worker.getId()), (rs, rowNum) -> rs.getInt("address_id")).get(0);
            int contact_id = jdbcTemplate.query(String.format("SELECT contact_information_id FROM application_user WHERE id='%s'", worker.getId()), (rs, rowNum) -> rs.getInt("contact_information_id")).get(0);

            jdbcTemplate.update(String.format("DELETE FROM application_user WHERE id='%s'", worker.getId()));
            jdbcTemplate.update(String.format("DELETE FROM application_user_address WHERE id='%s'", address_id));
            jdbcTemplate.update(String.format("DELETE FROM user_contact_information WHERE id='%s'", contact_id));
        }
    }

    public void createWorker(Worker worker){
        if(worker.getId() == -1){
            int addressId = jdbcTemplate.query("SELECT id FROM application_user_address ORDER BY id DESC", (rs, rowNum) ->
                    rs.getInt("id")).get(0)+1;
            int contactId = jdbcTemplate.query("SELECT id FROM user_contact_information ORDER BY id DESC", (rs, rowNum) ->
                    rs.getInt("id")).get(0)+1;

            jdbcTemplate.update(String.format("INSERT INTO application_user_address (id, street, city, zipcode) " +
                    "VALUES ('%s', '%s', '%s', '%s')",
                    addressId, worker.getStreet(), worker.getCity(), worker.getZipcode()));
            jdbcTemplate.update(String.format("INSERT INTO user_contact_information (id, phone_number, mobile_number, email) " +
                    "VALUES ('%s', '%s', '%s', '%s')",
                    contactId, worker.getPhoneNumber(), worker.getMobileNumber(), worker.getEmail()));
            jdbcTemplate.update(String.format("INSERT INTO application_user (version, name, username, hashed_password, address_id, contact_information_id) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                    1, worker.getName(), worker.getUsername(), worker.getHashedPassword(), addressId, contactId));
        }
    }

    public void updateWorker(Worker worker){
        //update user
        jdbcTemplate.update(String.format("UPDATE application_user SET name='%s', hashed_password='%s' WHERE id='%s'", worker.getName(), worker.getHashedPassword(), worker.getId()));
        //update contact information
        int contactId = jdbcTemplate.query(String.format("SELECT contact_information_id FROM application_user WHERE id='%s'", worker.getId()), (rs, rowNum) -> rs.getInt("contact_information_id")).get(0);
        jdbcTemplate.update(String.format("UPDATE user_contact_information SET phone_number='%s', mobile_number='%s', email='%s' WHERE id='%s'", worker.getPhoneNumber(), worker.getMobileNumber(), worker.getEmail(), contactId));
        //update address information
        int addressId = jdbcTemplate.query(String.format("SELECT address_id FROM application_id WHERE id='%s'", worker.getId()), (rs, rowNum) -> rs.getInt("address_id")).get(0);
        jdbcTemplate.update(String.format("UPDATE application_user_address SET street='%s', city='%s', zipcode='%' WHERE id='%s'", worker.getStreet(), worker.getCity(), worker.getZipcode(), addressId));
    }
}
