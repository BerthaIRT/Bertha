package com.ua.cs495f2018.berthaIRT;


//Message class used for messaging, alerts and logs.
public class Message {

    private Long messageTimestamp = 0L;
    private String messageSubject = "";
    private String messageBody = "";
    private Integer reportID = 0;

    public Message(){}

    public Long getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Long messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
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

    public Integer getReportID() {
        return reportID;
    }

    public void setReportID(Integer reportID) {
        this.reportID = reportID;
    }
}

