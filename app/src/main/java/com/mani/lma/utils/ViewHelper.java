package com.mani.lma.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewHelper {

    public static void displayDatePicker(final EditText editText , final Context context ) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));

        editText.setText(sdf.format(System.currentTimeMillis()));
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editText.setText(sdf.format(cal.getTime()));
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        context, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }
    public static void displayDatePicker(final ImageView imageView, final EditText editText , final Context context ) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));

        editText.setText(sdf.format(System.currentTimeMillis()));
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editText.setText(sdf.format(cal.getTime()));
            }
        };

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        context, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }

    public static String getToday(){
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        return sdf.format(cal.getTime());
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
