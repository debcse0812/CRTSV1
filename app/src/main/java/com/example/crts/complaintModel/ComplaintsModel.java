package com.example.crts.complaintModel;

public class ComplaintsModel {
    private String cid, name, email, address, c_type, c_detail, date, status, assigned_to, feedback, resolved_on;

    public ComplaintsModel(String cid, String name, String email, String address, String c_type, String c_detail, String date, String status, String assigned_to, String feedback, String resolved_on) {
        this.cid = cid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.c_type = c_type;
        this.c_detail = c_detail;
        this.date = date;
        this.status = status;
        this.assigned_to = assigned_to;
        this.feedback = feedback;
        this.resolved_on = resolved_on;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getC_detail() {
        return c_detail;
    }

    public void setC_detail(String c_detail) {
        this.c_detail = c_detail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getResolved_on() {
        return resolved_on;
    }

    public void setResolved_on(String resolved_on) {
        this.resolved_on = resolved_on;
    }
}
