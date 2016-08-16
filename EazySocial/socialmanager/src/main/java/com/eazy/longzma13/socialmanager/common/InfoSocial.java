package com.eazy.longzma13.socialmanager.common;

/**
 * Created by Harry on 7/24/16.
 */

public class InfoSocial {
    private String userId;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;

    public String getUserId() {
        return userId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void clearData() {
        this.userId = null;
        this.phone = null;
        this.email = null;
        this.firstName = null;
        this.lastName = null;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
