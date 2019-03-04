package com.mani.lma.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.mani.lma.R;
import com.mani.lma.utils.SessionVariables;


public class WidgetService extends IntentService {

    private static final String ACTION_ON_WIDGET_LAUNCH = "com.mani.baking.action.widget.launch";
    private static final String ACTION_ON_WIDGET_NEXT = "com.mani.baking.action.widget.next";
    private static final String ACTION_ON_WIDGET_PREVIOUS = "com.mani.baking.action.widget.previous";


    public WidgetService() {
        super(WidgetService.class.getSimpleName());
    }

    public static Intent getActionNextRecipeIntent(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_ON_WIDGET_NEXT);
        return intent;
    }

    public static Intent getActionPreviousRecipeIntent(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_ON_WIDGET_PREVIOUS);
        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
  /*          if (ACTION_ON_WIDGET_NEXT.equals(action)) {
               if( ++WidgetProvider.id >= SessionVariables.recipeDetailsList.size()){
                   WidgetProvider.id =0;
               }

                handleAction();
            } else if (ACTION_ON_WIDGET_PREVIOUS.equals(action)) {
                if ( --WidgetProvider.id < 0) {
                    WidgetProvider.id = SessionData.recipeDetailsList.size()-1;
                }
                handleAction();
            }*/
        }
    }

    private void handleAction() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        WidgetProvider.updateAllWidgets(this, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv);
    }
}
