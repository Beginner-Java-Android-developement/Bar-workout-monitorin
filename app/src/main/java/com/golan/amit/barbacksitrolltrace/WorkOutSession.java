package com.golan.amit.barbacksitrolltrace;

public class WorkOutSession {

    private int id;
    private int total;
    private int good;
    private String curr_datetime;

    public WorkOutSession() {
    }

    public WorkOutSession(int id, int total, int good, String curr_datetime) {
        this.id = id;
        this.total = total;
        this.good = good;
        this.curr_datetime = curr_datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public String getCurr_datetime() {
        return curr_datetime;
    }

    public void setCurr_datetime(String curr_datetime) {
        this.curr_datetime = curr_datetime;
    }
}
