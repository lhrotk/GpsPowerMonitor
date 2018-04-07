package com.github.lhrotk.gpspowermonitor;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements Runnable{


    private listFragment fragment1;
    private gpsStatusFragment fragment2;
    private summaryFragment fragment3;
    private LogcatParser logcatParser;
    private GPSInfo gpsInfo;

    private void showFragment1(){
        if(fragment1.isHidden()){
            FragmentTransaction transaction= getFragmentManager().beginTransaction();
            transaction.hide(fragment2);
            transaction.show(fragment1);
            transaction.hide(fragment3);
            transaction.commit();

        }
    }

    private void showFragment2(){
        if(fragment2.isHidden()){
            FragmentTransaction transaction= getFragmentManager().beginTransaction();
            transaction.hide(fragment1);
            transaction.hide(fragment3);
            transaction.show(fragment2);
            transaction.commit();
        }
    }

    private void showFragment3(){
        if(fragment3.isHidden()){
            FragmentTransaction transaction= getFragmentManager().beginTransaction();
            transaction.hide(fragment1);
            transaction.hide(fragment2);
            transaction.show(fragment3);
            transaction.commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment1();
                    return true;
                case R.id.navigation_dashboard:
                    showFragment2();
                    return true;
                case R.id.navigation_notifications:
                    showFragment3();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        gpsInfo = new GPSInfo();

        fragment1 = new listFragment();
        fragment2 = new gpsStatusFragment();
        fragment3 = new summaryFragment();
        fragment2.setGpsInfo(gpsInfo);
        fragment3.setGpsInfo(gpsInfo);
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.add(R.id.container, fragment1);
        trans.add(R.id.container, fragment2);
        trans.add(R.id.container, fragment3);
        trans.hide(fragment2);
        trans.hide(fragment3);
        trans.commit();




        logcatParser = new LogcatParser(false, gpsInfo, getApplicationContext().getPackageManager());

        new Thread(MainActivity.this).start();

    }

    public void onStart(){
        super.onStart();
        /*GPSstatus statusThread = new GPSstatus(fragment2.handler);
        new Thread(statusThread).start();*/

    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void run() {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            Runtime.getRuntime().exec(new String[] {"logcat", "-c"});
            mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat", "-s",
                    "GpsLocationProvider:*", "SendBroadcastPermission","LocationManagerService:*", "PGMiddleWare:*", "HwGpsPowerTracker:*"});
            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

            String line;

            while (true) {
                if ((line = reader.readLine())!=null) {;
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    GpsEvent gpsEvent = logcatParser.parseLog(line);
                    if(gpsEvent!=null){
                        bundle.putSerializable("GpsEvent", gpsEvent);
                        msg.setData(bundle);
                        fragment1.handler.sendMessage(msg);
                    }
                    /*Log.v("dd","ddd");*/
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
