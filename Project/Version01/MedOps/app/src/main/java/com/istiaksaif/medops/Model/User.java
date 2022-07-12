package com.istiaksaif.medops.Model;
/**
 * Created by Istiak Saif on 28/07/21.
 */

public class User {
    String name,email,phone,password,dob,bloodgroup,isUser,imageUrl,nid,userId,status;

    public User() {
    }

    public User(String name, String email, String password, String isUser, String phone, String dob, String bloodgroup, String imageUrl, String nid, String userId, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isUser = isUser;
        this.phone = phone;
        this.dob = dob;
        this.bloodgroup = bloodgroup;
        this.imageUrl = imageUrl;
        this.nid = nid;
        this.userId = userId;
        this.status = status;
    }


    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
