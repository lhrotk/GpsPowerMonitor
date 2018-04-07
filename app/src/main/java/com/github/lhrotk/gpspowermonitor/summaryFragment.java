package com.github.lhrotk.gpspowermonitor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;


/**
 *
 */
public class summaryFragment extends Fragment implements Runnable {

    private GPSInfo gpsInfo;
    private TextView textView;
    private RadioButton model1;
    private RadioButton model2;
    private RadioButton model3;

    public summaryFragment() {
        // Required empty public constructor
    }

    public void setGpsInfo(GPSInfo gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GpsUsageSummary summary = gpsInfo.getSummary();
            /*Log.e("size1", gpsInfo.packageName.size() + "");
            Log.e("size2", gpsInfo.GPSStatus.size() + "");
            Log.e("size3", gpsInfo.onOff.size() + "");
            Log.e("size4", gpsInfo.time.size() + "");*/
            ArrayList<String> PackageNames = summary.getGPSPackageName();
            ArrayList<Double> GpsPower;
            if(model1.isChecked())
                GpsPower = summary.getGPSPower();
            else if(model2.isChecked())
                GpsPower = summary.getGPSPower1();
            else
                GpsPower = summary.getGPSPower2();
            Message msg = new Message();
            msg.what = 2;
            this.handler.sendMessage(msg);
            for (int i = 0; i < PackageNames.size(); i++) {
                Message line = new Message();
                line.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("msg", PackageNames.get(i) + " : " + GpsPower.get(i));
                line.setData(bundle);
                this.handler.sendMessage(line);
            }
        }
    }

    public void onResume() {
        super.onResume();
        textView = (TextView) getView().findViewById(R.id.summary_text);
        model1 = (RadioButton)getView().findViewById(R.id.radioButton1);
        model2 = (RadioButton)getView().findViewById(R.id.radioButton2);
        model3 = (RadioButton)getView().findViewById(R.id.radioButton3);
        model1.setChecked(true);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 2)
                    textView.setText("");
                else {
                    textView.append(msg.getData().getString("msg")+"\n");
                }
            }
        };
        new Thread(summaryFragment.this).start();
    }


}
