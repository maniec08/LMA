package com.mani.lma.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.mani.lma.db.AppDb;

public class QueryViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private final AppDb appDb;
        private final String id;

        public QueryViewModelFactory(AppDb appDb, String id) {
            this.appDb = appDb;
            this.id = id;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new QueryViewModel(appDb, id);
        }


}
