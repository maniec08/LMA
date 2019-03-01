package com.mani.lma.datastruct;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity
public class LoanDetails implements Parcelable {

    @NonNull
    @PrimaryKey
    private String loanId;

    private String date;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    private Long amount;

    private int interest;

    private String settlementDate;

    private Long settlementAmount;

    private String custId;

    @Ignore
    public LoanDetails(@NonNull String loanId,String date){
        this.loanId = loanId;
        this.date = date;
    }

    public LoanDetails(@NonNull String loanId, String date,long amount, int interest, String settlementDate, Long settlementAmount, String custId) {
        this.loanId = loanId;
        this.date = date;
        this.amount = amount;
        this.interest = interest;
        this.settlementDate = settlementDate;
        this.settlementAmount = settlementAmount;
        this.custId = custId;
    }

    @NonNull
    public String getLoanId() {
        return loanId;
}

    public void setLoanId(@NonNull String loanId) {
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

    public Long getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(Long settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loanId);
        dest.writeString(date);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(amount);
        }
        dest.writeInt(interest);
        dest.writeString(settlementDate);
        if (settlementAmount == null ) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(settlementAmount);
        }
        dest.writeString(custId);
    }
    protected LoanDetails(Parcel in) {
        loanId = Objects.requireNonNull(in.readString());
        date = in.readString();
        if (in.readByte() == 0) {
            amount = 0L;
        } else {
            amount = in.readLong();
        }
        interest = in.readInt();
        settlementDate = in.readString();
        if (in.readByte() == 0) {
            settlementAmount = 0L;
        } else {
            settlementAmount = in.readLong();
        }
        custId = in.readString();
    }

    public static final Creator<LoanDetails> CREATOR = new Creator<LoanDetails>() {
        @Override
        public LoanDetails createFromParcel(Parcel in) {
            return new LoanDetails(in);
        }

        @Override
        public LoanDetails[] newArray(int size) {
            return new LoanDetails[size];
        }
    };
}
