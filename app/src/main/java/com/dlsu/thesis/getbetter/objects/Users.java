package com.dlsu.thesis.getbetter.objects;

/**
 * GetBetter. Created by Mike Dayupay on 7/6/15.
 */
public class Users {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String birthdate;
    private String gender;
    private String civilStatus;
    private String bloodType;
    private String homeAddress;
    private String officeAddress;


    public Users (String firstName, String middleName, String lastName, String email,
                  String password, String birthdate, String gender, String civilStatus) {

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.gender = gender;
        this.civilStatus = civilStatus;

    }

    public Users (String firstName, String middleName, String lastName, String birthdate,
                  String gender, String civilStatus, String bloodType) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.civilStatus = civilStatus;
        this.bloodType = bloodType;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String street, String barangay, String city) {
        this.homeAddress = street + " " + barangay + " " + city;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String street, String barangay, String city) {
        this.homeAddress = street + " " + barangay + " " + city;
    }

}
