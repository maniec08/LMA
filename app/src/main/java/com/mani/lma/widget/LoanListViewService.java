package com.mani.lma.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class LoanListViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(this);
    }

}
