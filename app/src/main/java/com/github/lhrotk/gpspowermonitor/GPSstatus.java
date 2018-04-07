package com.github.lhrotk.gpspowermonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by lhrotk on 2018/3/28.
 */

public class GPSstatus implements Runnable {
    private Handler handler;
    private boolean status = false;

    public GPSstatus(Handler handler) {
        this.handler = handler;
        this.status = false;
    }


    @Override
    public void run() {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        while (true) {

            try {
                Thread.sleep(200);
                mLogcatProc = Runtime.getRuntime().exec(new String[]{"cat", "/proc/gps/nstandby"});
                reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));

                String line;
                if ((line = reader.readLine()) != null) {
                    Log.e("E", line);
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", line);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
                //mLogcatProc.destroy();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}
