package com.ua.cs495f2018.berthaIRT;


//Message class used for messaging, alerts and logs.
public class Message {
    private Integer messageID; //to keep track of who has read it
    private Long messageTimestamp;
    private Integer reportID; //used only for Alerts to link to relevant report
    private String messageSubject;
    private String messageBody;

    public Message(){}

    public Integer getMessageID() { return messageID; }

    public void setMessageID(Integer messageID) { this.messageID = messageID; }

    public Long getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Long messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public Integer getReportID() {
        return reportID;
    }

    public void setReportID(Integer reportID) {
        this.reportID = reportID;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}