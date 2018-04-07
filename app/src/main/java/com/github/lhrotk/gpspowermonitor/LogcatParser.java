package com.github.lhrotk.gpspowermonitor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Calendar;

/**
 * Created by lhrotk on 2018/3/28.
 */

public class LogcatParser {
    private String lastRemoveUI;
    private String currentUI;
    //private String lastGPSUI;
    HashMap<String, Boolean> gpsPackageList;
    HashSet<String> backgroundGPS;
    private boolean gpsStatus;
    private GPSInfo gpsInfo;
    private boolean removeGPSUI=false;
    private PackageManager pm;
    //private Handler handler;
    public boolean checkGpsPermission(String packageName){

        String[] permissions = null;
        try{
            PackageInfo packinfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            permissions = packinfo.requestedPermissions;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }


        boolean coarse = false;
        boolean fine =false;
        for(int i=0; i<permissions.length; i++){
            if(permissions[i].equals("android.permission.ACCESS_FINE_LOCATION")){
                fine = true;
            }
            if(permissions[i].equals("android.permission.ACCESS_COARSE_LOCATION")){
                coarse = true;
            }
        }
        //Log.e("case", packageName + coarse + fine);
        return coarse&&fine;
    }

    public LogcatParser(boolean gpsStatus, GPSInfo gpsInfo, PackageManager pm){
        this.gpsStatus = gpsStatus;
        gpsPackageList = new HashMap<String, Boolean>();
        backgroundGPS = new HashSet<String>();
        this.gpsInfo = gpsInfo;
        this.pm = pm;
    }

    public boolean isSystemUI(String target){
        if(target.equals("com.android.systemui")||target.equals("com.huawei.android.launcher"))
            return true;
        else
            return false;
    }


    public GpsEvent parseEvent(int eventNumber, String packageName, Calendar cal){
        /*
        * 1: new gps data -> currentUI switch on GPS
        * 2: locatinManager remove -> currentUI stop/ last removed UI stop
        * 3: system stop location -> system tell the packagename
        * 4: Gps switch on/off
        * 5: new surface UI -> store current UI used GPS
        * */
        Log.e("case", eventNumber+"");
        String temp = "";

        if(removeGPSUI&&eventNumber%2==1){
            temp = lastRemoveUI;
        }

        removeGPSUI=false;

        switch(eventNumber){
            case 1: // a new gps active notice
                if(!gpsPackageList.containsKey(currentUI)){
                    gpsPackageList.put(currentUI, false);
                }

                Log.e("case", currentUI);

                if(gpsPackageList.get(currentUI)==false&&(!isSystemUI(currentUI))&&checkGpsPermission(currentUI)){
                    gpsPackageList.put(currentUI, true);
                    //TODO: tell profiler
                    GpsLogInfo log = new GpsLogInfo();
                    log.setOnOff(true);
                    log.setPackageName(currentUI);
                    log.setTime(Calendar.getInstance());
                    gpsInfo.updateUsage(log);
                    return new GpsEvent(1, currentUI, cal, temp);
                }
                break;
            case 2: // package location stopped actively
                //Log.e("gps", currentUI+lastRemoveUI);
                if(gpsPackageList.containsKey(currentUI)){
                    if(gpsPackageList.get(currentUI)==true){
                        gpsPackageList.put(currentUI, false);
                        //TODO: tell profiler
                        GpsLogInfo log = new GpsLogInfo();
                        log.setOnOff(false);
                        log.setPackageName(currentUI);
                        log.setTime(Calendar.getInstance());
                        gpsInfo.updateUsage(log);
                        return new GpsEvent(2, currentUI, cal, temp);
                    }
                }else{
                    //Log.e("gps", lastRemoveUI);
                    if(gpsPackageList.get(lastRemoveUI)==true){
                        gpsPackageList.put(lastRemoveUI, false);
                        //TODO: tell profiler
                        GpsLogInfo log = new GpsLogInfo();
                        log.setOnOff(false);
                        log.setPackageName(lastRemoveUI);
                        log.setTime(Calendar.getInstance());
                        gpsInfo.updateUsage(log);
                        return new GpsEvent(2, lastRemoveUI, cal, temp);
                    }
                }
                break;
            case 3://system stop location
                if(gpsPackageList.containsKey(packageName)){
                    if(gpsPackageList.get(packageName)==true){
                        gpsPackageList.put(packageName, false);
                        //TODO: tell profiler
                        GpsLogInfo log = new GpsLogInfo();
                        log.setOnOff(false);
                        log.setPackageName(packageName);
                        log.setTime(Calendar.getInstance());
                        gpsInfo.updateUsage(log);
                        return new GpsEvent(2, packageName, cal, temp);
                    }
                }
                break;
            case 4: // gps status change
                //TODO: BUG FIX
                gpsStatus = !gpsStatus;
                if(gpsStatus==false){
                    for(String key: gpsPackageList.keySet()){
                        if(gpsPackageList.get(key)==true){
                            //TODO: tell profiler
                            GpsLogInfo log = new GpsLogInfo();
                            log.setOnOff(false);
                            log.setPackageName(key);
                            log.setTime(Calendar.getInstance());
                            gpsInfo.updateUsage(log);
                            return new GpsEvent(2, key, cal, temp);
                        }
                        gpsPackageList.put(key, false);
                    }
                }
                break;
            case 5: // system UI change event
                if(gpsPackageList.containsKey(currentUI)){
                    this.lastRemoveUI = currentUI;
                    Log.e("remove", lastRemoveUI);
                    if(gpsPackageList.get(lastRemoveUI)==true)
                        removeGPSUI = true;
                    currentUI = packageName;
                }else{
                    currentUI = packageName;

                }
                break;
            default:
                break;
        }

        if(temp.equals(""))
            return null;
        else
            return new GpsEvent(0, "", cal, temp);
    }

    public GpsEvent parseLog(String log){
        //Log.e("parser", log);
        if(!(log.charAt(0)-'0'>=0&&log.charAt(0)-'0'<=9))
            return null;
        SimpleDateFormat sdf= new SimpleDateFormat("MM-dd hh:mm:ss.SSS");
        Date date = null;
        try {
            date = sdf.parse(log.substring(0, 18));
        } catch (ParseException e) {
            e.printStackTrace();
            //Log.e("parser", log.substring(0, 18));
        }

        //Log.e("parser", "line2");
        Calendar calendar = Calendar.getInstance();
        //Log.e("parser", "line3");
        calendar.setTime(date);
        //Log.e("parser", "line4");
        int i = log.indexOf( ':', 18);
        int j = 32;

        String logName = log.substring(j+1,i);

        String logContent = log.substring(j+1);



        //TODO DELETE HERE
        //Log.e("parser", logName);

        if(logContent.equals("GpsLocationProvider: mEngineOn && mStarted ,get location from DB, inject to GPS  Module")){
            return this.parseEvent(1, null, calendar);
        }else if(logContent.equals("SendBroadcastPermission: action:android.location.GPS_ENABLED_CHANGE, mPermissionType:0")){
            return this.parseEvent(4, null, calendar);
        }else if(logName.equals("HwGpsPowerTracker")){
            int k = log.indexOf("stop");
            if(k!=-1) {
                int start = log.indexOf(' ', k);
                int end = log.indexOf(' ', start+1);
                //log.substring(start+1, end));
                return this.parseEvent(3, log.substring(start+1, end), calendar);
            }
        }else if(logName.equals("PGMiddleWare")){

            int k = log.indexOf("pkg =");
            if(k!=-1) {
                int end = log.indexOf(' ', k+4);
                return this.parseEvent(5, log.substring(k+5, end), calendar);
            }
        }else if(logName.equals("LocationManagerService")){
            return this.parseEvent(2, null, calendar);
        }
        return null;
    }
}
