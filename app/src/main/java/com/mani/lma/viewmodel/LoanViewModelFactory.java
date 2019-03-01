package com.mani.lma.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.mani.lma.db.AppDb;

public class LoanViewModelFactory  extends ViewModelProvider.NewInstanceFactory {
        private final AppDb appDb;
        private final String id;

        public LoanViewModelFactory(AppDb appDb, String id) {
            this.appDb = appDb;
            this.id = id;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new LoanViewModel(appDb, id);
        }


}
