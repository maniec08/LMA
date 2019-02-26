package com.mani.lma.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;

@Database(entities = {LoanDetails.class, CustDetails.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String TAG = AppDb.class.getSimpleName();
    private static final String custDbName = CustDetails.class.getSimpleName();
    private static final String loanDbName = LoanDetails.class.getSimpleName();
    private static AppDb custInstance;
    private static AppDb loanInstance;

    public static AppDb getCustInstance(Context context) {
        if (custInstance == null) {
            synchronized (LOCK) {
                custInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDb.class,
                        custDbName)
                        .build();
            }
        }
        return custInstance;
    }

    public static AppDb getLoanInstance(Context context) {
        if (loanInstance == null) {
            synchronized (LOCK) {
                loanInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDb.class,
                        loanDbName)
                        .build();
            }
        }
        return loanInstance;
    }

    public abstract AppDao appDao();
}

