package com.mani.lma.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.datastruct.LoanDetails;
import com.mani.lma.db.AppDb;

public class DefaultViewModel extends AndroidViewModel {
    private LiveData<LoanDetails> loanDetails;
    private LiveData<CustDetails> custDetails;

    public LiveData<LoanDetails> getLoanDetails() {
        return loanDetails;
    }

    public void setLoanDetails(LiveData<LoanDetails> loanDetails) {
        this.loanDetails = loanDetails;
    }

    public LiveData<CustDetails> getCustDetails() {
        return custDetails;
    }

    public void setCustDetails(LiveData<CustDetails> custDetails) {
        this.custDetails = custDetails;
    }



    public DefaultViewModel(@NonNull Application application) {
        super(application);
        loanDetails = AppDb.getLoanInstance(this.getApplication()).appDao().getLoanDetails("0");
        custDetails = AppDb.getCustInstance(this.getApplication()).appDao().getCustDetails("0");
    }
}
