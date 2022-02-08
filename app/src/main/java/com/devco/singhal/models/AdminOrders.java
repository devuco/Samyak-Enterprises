package com.devco.singhal.models;

public class AdminOrders {
    private String name, phone, address, city, state, date, time, totalamount, transport, lrno;

    public AdminOrders() {
    }

    public AdminOrders(String name, String phone, String address, String city, String state, String date, String time, String totalamount, String transport, String lrno) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalamount = totalamount;
        this.transport = transport;
        this.lrno = lrno;
    }

    public String getLrno() {
        return lrno;
    }

    public void setLrno(String lrno) {
        this.lrno = lrno;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }
}
