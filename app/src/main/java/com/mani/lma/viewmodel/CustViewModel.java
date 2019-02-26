package com.mani.lma.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mani.lma.datastruct.CustDetails;
import com.mani.lma.db.AppDb;

public class CustViewModel extends ViewModel {
    private LiveData<CustDetails> custDetails;

    public CustViewModel(AppDb appDb, int id) {
        custDetails = appDb.appDao().getCustDetails(id);
    }

    public LiveData<CustDetails> getCustDetails() {
        return custDetails;
    }

    public void setMovieDetails(LiveData<CustDetails> custDetails) {
        this.custDetails = custDetails;
    }
}
