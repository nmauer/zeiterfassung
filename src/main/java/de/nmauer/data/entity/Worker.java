package de.nmauer.data.entity;

public class Worker {

    private int id;

    private String name,
            username,
            hashedPassword;

    private String phoneNumber,
            mobileNumber,
            email;

    private String street,
            city,
            zipcode;

    public Worker(int id, String name, String username, String hashedPassword, String phoneNumber, String mobileNumber,
                  String email, String street, String city, String zipcode) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.phoneNumber = phoneNumber;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipcode() {
        return zipcode;
    }
}
