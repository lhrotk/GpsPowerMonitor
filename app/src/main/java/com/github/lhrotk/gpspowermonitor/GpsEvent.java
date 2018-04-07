package com.github.lhrotk.gpspowermonitor;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by lhrotk on 2018/4/4.
 */

public class GpsEvent implements Serializable{
    public int EventType;
    public String PackageName;
    public Calendar time;
    public String BackGroundEvent;

    public GpsEvent(int EventType, String PackageName, Calendar cal, String BackGroundEvent){
        this.EventType = EventType;
        this.PackageName = PackageName;
        this.time = cal;
        this.BackGroundEvent = BackGroundEvent;
    }
}
