package com.github.lhrotk.gpspowermonitor;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class listFragment extends Fragment {


    //private TextView tv1;
    private BufferedReader buffer;
    public Handler handler;
    private Button btn_clr;
    private List<GpsEventList> eventList = new ArrayList<GpsEventList>();
    private ListView lvEvent;
    private PackageManager pm;
    private EventAdaptor eventAdaptor;
    public listFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        lvEvent = (ListView) v.findViewById(R.id.lv1);
        pm = getActivity().getPackageManager();

        eventAdaptor = new EventAdaptor(getActivity(), R.layout.list_item, this.eventList);
        lvEvent.setAdapter(eventAdaptor);
        handler = new Handler(){
            public void handleMessage(Message msg){
                Log.e("debug", "data received");
                GpsEvent gpsEvent = (GpsEvent) msg.getData().getSerializable("GpsEvent");
                switch (gpsEvent.EventType){
                    case 1:
                        try{
                            ApplicationInfo info = pm.getApplicationInfo(gpsEvent.PackageName, 0);
                            String name = info.loadLabel(pm).toString();
                            Drawable icon = info.loadIcon(pm);
                            eventList.add(new GpsEventList(gpsEvent.time, "start using gps", name, icon));
                        }catch(PackageManager.NameNotFoundException e){
                            e.printStackTrace();
                        }
                        ((EventAdaptor)lvEvent.getAdapter()).notifyDataSetChanged();
                        break;
                    case 2:
                        try{
                            ApplicationInfo info = pm.getApplicationInfo(gpsEvent.PackageName, 0);
                            String name = info.loadLabel(pm).toString();
                            Drawable icon = info.loadIcon(pm);
                            eventList.add(new GpsEventList(gpsEvent.time, "stop using gps", name, icon));
                        }catch(PackageManager.NameNotFoundException e){
                            e.printStackTrace();
                        }
                        ((EventAdaptor)lvEvent.getAdapter()).notifyDataSetChanged();
                        break;
                    default: break;
                }
                if(!gpsEvent.BackGroundEvent.equals("")){
                    try{
                        ApplicationInfo info = pm.getApplicationInfo(gpsEvent.BackGroundEvent, 0);
                        String name = info.loadLabel(pm).toString();
                        Drawable icon = info.loadIcon(pm);
                        eventList.add(new GpsEventList(gpsEvent.time, "is using gps in background", name, icon));
                    }catch(PackageManager.NameNotFoundException e){
                        e.printStackTrace();
                    }
                    ((EventAdaptor)lvEvent.getAdapter()).notifyDataSetChanged();
                }
                Log.e("debug", eventList.size()+"");
            }
        };
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        //this.tv1 = (TextView)getView().findViewById(R.id.tv1);

        /*this.btn_clr = (Button)getView().findViewById(R.id.button_clr);
        btn_clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ApplicationInfo info = pm.getApplicationInfo("com.github.lhrotk.gpspowermonitor", 0);
                    String name = info.loadLabel(pm).toString();
                    Drawable icon = info.loadIcon(pm);
                    eventList.add(new GpsEventList(Calendar.getInstance(), "", name, icon));
                    Log.e("name", name);
                }catch(PackageManager.NameNotFoundException e){
                    e.printStackTrace();
                }
                eventList.get(0).setMessage(eventList.size()+"");
                ((EventAdaptor)lvEvent.getAdapter()).notifyDataSetChanged();
            }
        });*/
    }

    public void addLog(String s){
    }

}
