package com.mani.lma.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.db.AppDb;

import java.util.List;

public class QueryViewModel extends ViewModel {
    private LiveData<List<LoanDetails>> loanDetails;

    public QueryViewModel(AppDb appDb, String id) {
        loanDetails = appDb.appDao().getLoanDetailsWithCustId(id);
    }

    public LiveData<List<LoanDetails>> getLoanDetails() {
        return loanDetails;
    }

    public void setLoanDetails(LiveData<List<LoanDetails>> loanDetails) {
        this.loanDetails = loanDetails;
    }
}
