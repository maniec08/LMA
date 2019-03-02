package com.mani.lma.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;

import java.util.List;

@Dao
public interface AppDao {

    @Query("SELECT * FROM loanDetails WHERE loanId = :id")
    LiveData<LoanDetails> getLoanDetails(String id);

    @Query("SELECT * FROM loanDetails WHERE loanId = :id")
    LoanDetails getLoanDetail(String id);

    @Query("SELECT * FROM CustDetails WHERE custid = :id")
    LiveData<CustDetails> getCustDetails(String id);

    @Query("SELECT * FROM CustDetails WHERE custid = :id")
    CustDetails getCustDetail(String id);

    @Query("UPDATE loanDetails SET date=:date, amount =:amount,interest=:interest,settlementDate =:paidDate,settlementAmount=:paidAmount, custId=:custID WHERE loanId =:id")
    void updateLoanDb(String id, String date, Long amount, int interest, String paidDate, Long paidAmount, String custID);

    @Query("UPDATE CustDetails SET  custName=:name, phoneNumber =:phoneNumber,emailId=:emailId WHERE custid =:id")
    void updateCustDb(String id, String name, String phoneNumber, String emailId);

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    void insertLoan(LoanDetails loanDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLoan(List<LoanDetails> loanDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomerInfo(CustDetails custDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomerInfo(List<CustDetails> custDetails);
}
