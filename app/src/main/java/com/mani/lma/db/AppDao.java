package com.mani.lma.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;

import java.util.List;

@Dao
public interface AppDao {

    @Query("SELECT * FROM loanDetails WHERE loanId = :id")
    LiveData<LoanDetails> getLoanDetails(int id);


    @Query("SELECT * FROM CustDetails WHERE custid = :id")
    LiveData<CustDetails> getCustDetails(int id);
}
