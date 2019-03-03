package com.mani.lma.datastruct;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryDetails implements Parcelable {

    private String fromDate;
    private String toDate;
    private String userId;
    private boolean userIdSearch = false;

    public QueryDetails(String fromDate, String toDate, boolean userIdSearch) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.userIdSearch = userIdSearch;
    }

    public QueryDetails(String userId, boolean userIdSearch) {
        this.userId = userId;
        this.userIdSearch = userIdSearch;
    }

    protected QueryDetails(Parcel in) {
        fromDate = in.readString();
        toDate = in.readString();
        userId = in.readString();
        userIdSearch = in.readByte() != 0;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUserIdSearch() {
        return userIdSearch;
    }

    public void setUserIdSearch(boolean userIdSearch) {
        this.userIdSearch = userIdSearch;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromDate);
        dest.writeString(toDate);
        dest.writeString(userId);
        dest.writeByte((byte) (userIdSearch ? 1 : 0));
    }
    public static final Creator<QueryDetails> CREATOR = new Creator<QueryDetails>() {
        @Override
        public QueryDetails createFromParcel(Parcel in) {
            return new QueryDetails(in);
        }

        @Override
        public QueryDetails[] newArray(int size) {
            return new QueryDetails[size];
        }
    };
}
