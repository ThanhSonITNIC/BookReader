package com.itnic.thanhson.readbooktuthulanhdaothuatxuthe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import Model.DatabaseBook;

public class SettingActivity extends DatabaseBook {

    TextView txtTextSizeReview, txtViewTimerScroll, txtReviewTimerScroll;
    SeekBar seekBarTextSize, seekBarTimerScroll;
    TimePicker timePickerRemind;
    Switch swSeeAdmob, swRemind, swThemeDark;
    ScrollView scrollViewSetting;
    SharedPref sharedpref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        else  setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        swThemeDark=(Switch)findViewById(R.id.swThemeDark);
        if (sharedpref.loadNightModeState()==true) {
            swThemeDark.setChecked(true);
        }
        swThemeDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                }
                else {
                    sharedpref.setNightModeState(false);
                    restartApp();
                }
            }
        });
        setupActionBar();
        addControls();
        addEvents();
        readUser();
        loadConfig();
    }

    public void restartApp () {
        Intent i = new Intent(getApplicationContext(),SettingActivity.class);
        Intent im = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(im);
        startActivity(i);
        finish();
    }

    Intent notifyIntent;
    private void setNotify(boolean isRemind)
    {
        try {
            notifyIntent = new Intent(this, MyReceiver.class);
            if (isRemind) {
                notification(Integer.parseInt(getTimeRead().split(":")[0]), Integer.parseInt(getTimeRead().split(":")[1]));
            } else {
                stopService(notifyIntent);
            }
        } catch (Exception ex) {
        }
    }

    private void notification(int hour, int minute) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent); //set repeating every 24 hours
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadConfig() {
        //theme(getThemeId());
        seekBarTimerScroll.setProgress(getTimerScroll());
        txtReviewTimerScroll.setText(getTimerScroll() + " giây");
        seekBarTextSize.setProgress(getTextSize());
        txtTextSizeReview.setTextSize(getTextSize());
        swRemind.setChecked(isRemind());
        swSeeAdmob.setChecked(DatabaseBook.IsSeeAdmod);
        if(getTimeRead() != null)
        {
            timePickerRemind.setHour(Integer.parseInt(getTimeRead().split(":")[0]));
            timePickerRemind.setMinute(Integer.parseInt(getTimeRead().split(":")[1]));
        }
    }

    private void addEvents() {
        swSeeAdmob.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DatabaseBook.IsSeeAdmod = b;
            }
        });

        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtTextSizeReview.setTextSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTextSize(seekBar.getProgress());
            }
        });

        swRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setRemind(b);
                setNotify(b);
            }
        });

        timePickerRemind.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                setTimeRead(i+":"+i1);
                swRemind.setChecked(false);
            }
        });

        seekBarTimerScroll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtReviewTimerScroll.setText((i<5?5:i) + " giây");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTimerScroll(seekBar.getProgress()<5?5:seekBar.getProgress());
            }
        });
    }

    private void addControls() {
        txtTextSizeReview = findViewById(R.id.txtReviewTextSize);
        //txtTheme = findViewById(R.id.txtTheme);
        seekBarTextSize = findViewById(R.id.seekBarTextSize);
        timePickerRemind = findViewById(R.id.pickerTimeRemind);
        swRemind = findViewById(R.id.swRemind);
        swSeeAdmob = findViewById(R.id.swSeeAdmob);
        scrollViewSetting = findViewById(R.id.scrollViewSetting);
        scrollViewSetting.setFocusableInTouchMode(true);//                                  scroll up
        scrollViewSetting.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);////---------////////

        txtViewTimerScroll = findViewById(R.id.txtReviewtimerscroll);
        seekBarTimerScroll = findViewById(R.id.seekBarTimerScroll);
        txtReviewTimerScroll = findViewById(R.id.txtViewtimerscroll);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Cài đặt");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
