package com.mani.lma.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.db.AppDb;

public class LoanViewModel extends ViewModel {
    private LiveData<LoanDetails> loanDetails;

    public LoanViewModel(AppDb appDb, String id) {
        loanDetails = appDb.appDao().getLoanDetails(id);
    }

    public LiveData<LoanDetails> getLoanDetails() {
        return loanDetails;
    }

    public void setLoanDetails(LiveData<LoanDetails> loanDetails) {
        this.loanDetails = loanDetails;
    }
}
