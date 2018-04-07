package com.github.lhrotk.gpspowermonitor;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sherry on 2018/4/4.
 */

public class GpsEventList {


    private String AppName;
    private String Time;
    private Drawable AppIcon;
    private String Message;

    public GpsEventList(){

    };

    public GpsEventList(Calendar cal, String Message, String AppName, Drawable AppIcon ){
        this.AppName = AppName;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
        Time = sdf.format(cal.getTime());
        this.Message = Message;
        this.AppIcon = AppIcon;
    };

    public String getAppName() {
        return AppName;
    }

    public String getTime() {
        return Time;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public String getMessage() {
        return Message;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
