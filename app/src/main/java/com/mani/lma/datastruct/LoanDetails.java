package com.mani.lma.datastruct;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LoanDetails {

    @PrimaryKey
    private int loanId = 0;

    private String date;

    private int interest;

    private String settlementDate;

    private String settlementAmount;

    private int custId = 0;

    public LoanDetails(int loanId, String date, int interest, String settlementDate, String settlementAmount, int custId) {
        this.loanId = loanId;
        this.date = date;
        this.interest = interest;
        this.settlementDate = settlementDate;
        this.settlementAmount = settlementAmount;
        this.custId = custId;
    }

    public int getLoanId() {
        return loanId;
}

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(String settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }
}
