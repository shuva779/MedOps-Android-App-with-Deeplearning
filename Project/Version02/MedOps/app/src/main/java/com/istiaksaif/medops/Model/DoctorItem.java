package com.istiaksaif.medops.Model;

public class DoctorItem {
    private String name,email,phone,dob,bloodgroup,isUser,imageUrl,nid,userId,balanceTk
            ,verifyStatus,bmdcID,consultFee,workingIn,degrees,designation,workingExperience,
            consultHour,consultHourTo,consultDays;
    public DoctorItem() {
    }

    public DoctorItem(String name, String email, String phone, String dob, String bloodgroup, String isUser, String imageUrl, String nid, String userId, String balanceTk, String verifyStatus, String bmdcID, String consultFee, String workingIn, String degrees, String designation, String workingExperience, String consultHour, String consultHourTo,String consultDays) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.bloodgroup = bloodgroup;
        this.isUser = isUser;
        this.imageUrl = imageUrl;
        this.nid = nid;
        this.userId = userId;
        this.balanceTk = balanceTk;
        this.verifyStatus = verifyStatus;
        this.bmdcID = bmdcID;
        this.consultFee = consultFee;
        this.workingIn = workingIn;
        this.degrees = degrees;
        this.designation = designation;
        this.workingExperience = workingExperience;
        this.consultHour = consultHour;
        this.consultHourTo = consultHourTo;
        this.consultDays = consultDays;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBalanceTk() {
        return balanceTk;
    }

    public void setBalanceTk(String balanceTk) {
        this.balanceTk = balanceTk;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getBmdcID() {
        return bmdcID;
    }

    public void setBmdcID(String bmdcID) {
        this.bmdcID = bmdcID;
    }

    public String getConsultFee() {
        return consultFee;
    }

    public void setConsultFee(String consultFee) {
        this.consultFee = consultFee;
    }

    public String getWorkingIn() {
        return workingIn;
    }

    public void setWorkingIn(String workingIn) {
        this.workingIn = workingIn;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWorkingExperience() {
        return workingExperience;
    }

    public void setWorkingExperience(String workingExperience) {
        this.workingExperience = workingExperience;
    }

    public String getConsultHour() {
        return consultHour;
    }

    public void setConsultHour(String consultHour) {
        this.consultHour = consultHour;
    }

    public String getConsultHourTo() {
        return consultHourTo;
    }

    public void setConsultHourTo(String consultHourTo) {
        this.consultHourTo = consultHourTo;
    }
}
