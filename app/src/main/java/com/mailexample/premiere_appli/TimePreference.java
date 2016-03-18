package com.mailexample.premiere_appli;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimePreference extends DialogPreference {
    private int mHour = 0;
    private int mMinute = 0;
    private TimePicker picker = null;
    private final String DEFAULT_VALUE = "00:00";

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
        String time = toTime(mHour, mMinute);
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }
    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);
        setPositiveButtonText(R.string.exit);
        setNegativeButtonText(R.string.lost);
    }
    public String toTime(int hour, int minute) {
        return String.valueOf(hour) + ":" + String.valueOf(minute);
    }

    public void updateSummary() {
        String time = String.valueOf(mHour) + ":" + String.valueOf(mMinute);
        setSummary(time24to12(time));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }
    @Override     protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }
    @Override     protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }
    @Override     protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //setTime(5, 0);
        updateSummary();
    }

    public static String time24to12(String inTime) {
        Date inDate = toDate(inTime);
        if(inDate != null) {
            DateFormat outTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            return outTimeFormat.format(inDate);
        } else {
            return inTime;
        }
    }

    public static Date toDate(String inTime) {
        try {
            DateFormat inTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            return inTimeFormat.parse(inTime);
        } catch(ParseException e) {
            return null;
        }
    }
}