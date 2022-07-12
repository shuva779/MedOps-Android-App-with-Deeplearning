package com.istiaksaif.medops.Model;

public class DoctorItem {
    private String image,doctorName,designation,nid,bmdcid,email,phonenum,status,doctorId,time,hospital;

    public DoctorItem() {
    }

    public DoctorItem(String image, String doctorName, String designation, String nid, String bmdcid, String email, String phonenum, String status, String doctorId, String time) {
        this.image = image;
        this.doctorName = doctorName;
        this.designation = designation;
        this.nid = nid;
        this.bmdcid = bmdcid;
        this.email = email;
        this.phonenum = phonenum;
        this.status = status;
        this.doctorId = doctorId;
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getBmdcid() {
        return bmdcid;
    }

    public void setBmdcid(String bmdcid) {
        this.bmdcid = bmdcid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
