package com.istiaksaif.medops.Model;
/**
 * Created by Istiak Saif on 28/07/21.
 */

public class User {
    String name,email,phone,height,dob,bloodgroup,isUser,imageUrl,nid,userId,balanceTk,address,weight,key;

    public User() {
    }

    public User(String name, String email, String address, String isUser, String phone, String dob,
                String bloodgroup, String imageUrl, String nid, String userId,
                String balanceTk,String height,String weight,String key) {
        this.name = name;
        this.email = email;
        this.height = height;
        this.isUser = isUser;
        this.phone = phone;
        this.dob = dob;
        this.bloodgroup = bloodgroup;
        this.imageUrl = imageUrl;
        this.nid = nid;
        this.userId = userId;
        this.balanceTk = balanceTk;
        this.address = address;
        this.weight = weight;
        this.key = key;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
