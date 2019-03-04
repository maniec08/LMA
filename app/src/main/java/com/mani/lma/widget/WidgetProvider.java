package com.mani.lma.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.mani.lma.R;
import com.mani.lma.utils.KeyConstants;


public class WidgetProvider extends AppWidgetProvider {

    public static int id = 0;
    private static int intentId = 0;
    private Context context;
    public static String custId = "";
    public static boolean isLender = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        readFromSharedPref(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if(isLender){

        }
        Intent intent = new Intent(context, LoanListViewService.class);
        views.setRemoteAdapter(R.id.widget_lv, intent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void readFromSharedPref(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        custId = sharedPrefs.getString(KeyConstants.custId, "");
        isLender = sharedPrefs.getBoolean(custId, false);
    }

    public static void updateAllWidgets(Context context, AppWidgetManager widgetManager, int appWidgetIds[]) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, widgetManager, appWidgetId);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}
