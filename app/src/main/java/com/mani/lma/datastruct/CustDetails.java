package com.mani.lma.datastruct;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CustDetails {
    @PrimaryKey
    private int custid  = 0;

    private String custName;

    private String emailId;

    private String phoneNumber;

    public CustDetails(int custid, String custName, String emailId, String phoneNumber) {
        this.custid = custid;
        this.custName = custName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public int getCustid() {
        return custid;
    }

    public void setCustid(int custid) {
        this.custid = custid;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
