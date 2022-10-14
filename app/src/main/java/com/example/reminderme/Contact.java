package com.example.reminderme;

import java.util.List;


public class Contact {
    private Integer Year =-1, Month =-1, Day =-1, Hour =-1, Minute =-1;
    private String name;

    private List<String> phoneNumbers;

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDay(int day) {
        this.Day = day;
    }

    public Integer getYear() {
        return Year;
    }

    public void setYear(Integer year) {
        this.Year = year;
    }

    public Integer getMonth() {
        return Month;
    }

    public void setMonth(Integer month) {
        this.Month = month;
    }

    public Integer getDay() {
        return Day;
    }

    public void setmDay(Integer mDay) {
        this.Day = mDay;
    }

    public Integer getHour() {
        return Hour;
    }

    public void setHour(Integer hour) {
        this.Hour = hour;
    }

    public Integer getMinute() {
        return Minute;
    }

    public void setMinute(Integer minute) {
        this.Minute = minute;
    }
}

