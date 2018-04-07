package com.github.lhrotk.gpspowermonitor;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class gpsStatusFragment extends Fragment implements Runnable{

    public DynamicLineChartManager dynamicLineChartManager1;
    private List<Integer> list = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<Integer> colour = new ArrayList<>();
    //private Button btn;
    private GPSInfo gpsInfo;
    public Handler handler;
    private int gpsStatus = 0;

    public gpsStatusFragment() {
        // Required empty public constructor
    }

    public void setGpsInfo(GPSInfo gpsInfo){
        this.gpsInfo = gpsInfo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_gps_status, container, false);
        LineChart mChart1 = (LineChart) v.findViewById(R.id.dynamic_chart1);
        names.add("GPS");
        colour.add(Color.CYAN);
        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1, names.get(0), colour.get(0));
        dynamicLineChartManager1.setYAxis(2, 0, 2);
        dynamicLineChartManager1.setDescription("GPS Status");
        //btn = (Button)v.findViewById(R.id.button);
        handler = new Handler(){
            public void handleMessage(Message msg){
                String content = msg.getData().getString("msg");
                int data = Integer.parseInt(content);
                if(data!=gpsStatus){
                    gpsStatus = data;
                    Calendar cal = Calendar.getInstance();
                    gpsInfo.updateGPSStatus(cal);
                    //Log.e("update cal", "updata gps status");
                }
                dynamicLineChartManager1.addEntry(data*1);
            }
        };

        new Thread(gpsStatusFragment.this).start();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();



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
                    ;
                    //Log.e("E", line);
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
