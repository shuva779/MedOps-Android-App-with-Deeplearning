package com.istiaksaif.medops.Model;

public class QAItem {
    private String qaimage,userimage,userName,ques,quesdes,reply,quesId;

    public QAItem() {
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public String getQaimage() {
        return qaimage;
    }

    public void setQaimage(String qaimage) {
        this.qaimage = qaimage;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getQuesdes() {
        return quesdes;
    }

    public void setQuesdes(String quesdes) {
        this.quesdes = quesdes;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
