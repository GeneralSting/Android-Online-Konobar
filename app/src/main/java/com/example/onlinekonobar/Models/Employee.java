package com.example.onlinekonobar.Models;

public class Employee {
    String cafeId;
    String gmail;
    String lastname;
    String name;
    String oib;

    public Employee() {
        // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
    }

    public Employee(String cafeId, String gmail, String lastname, String name, String oib) {
        this.cafeId = cafeId;
        this.gmail = gmail;
        this.lastname = lastname;
        this.name = name;
        this.oib = oib;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
    }
}
