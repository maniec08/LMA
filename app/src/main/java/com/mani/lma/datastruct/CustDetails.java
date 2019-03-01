package com.mani.lma.datastruct;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity
public class CustDetails implements Parcelable {
    @NonNull
    @PrimaryKey
    private String custid ="0" ;

    private String custName;

    private String emailId;

    private String phoneNumber;

    @Ignore
    public CustDetails(){
    }

    public CustDetails(@NonNull String custid, String custName, String emailId, String phoneNumber) {
        this.custid = custid;
        this.custName = custName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }



    @NonNull
    public String getCustid() {
        return custid;
    }

    public void setCustid(@NonNull String custid) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(custid);
        dest.writeString(custName);
        dest.writeString(emailId);
        dest.writeString(phoneNumber);
    }
    protected CustDetails(Parcel in) {
        custid = Objects.requireNonNull(in.readString());
        custName = in.readString();
        emailId = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<CustDetails> CREATOR = new Creator<CustDetails>() {
        @Override
        public CustDetails createFromParcel(Parcel in) {
            return new CustDetails(in);
        }

        @Override
        public CustDetails[] newArray(int size) {
            return new CustDetails[size];
        }
    };
}
