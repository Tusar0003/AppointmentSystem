package com.example.no0ne.appointmentsystem;

/**
 * Created by no0ne on 2/23/18.
 */

public class Teacher {

    public String user_name;
    public String department;
    public String image;
    public String account_for;

    public Teacher() {
    }

    public Teacher(String user_name, String department, String image, String account_for) {
        this.user_name = user_name;
        this.department = department;
        this.image = image;
        this.account_for = account_for;
    }

    public String getName() {
        return user_name;
    }

    public void setName(String name) {
        this.user_name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAccount_for() {
        return account_for;
    }

    public void setAccount_for(String account_for) {
        this.account_for = account_for;
    }
}
