package com.github.lhrotk.gpspowermonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by sherry on 2018/4/4.
 */

public class EventAdaptor extends ArrayAdapter<GpsEventList> {

    private int resourceId;

    public EventAdaptor(@NonNull Context context, int resource, @NonNull List<GpsEventList> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater lay = LayoutInflater.from(getContext());
            view = lay.inflate(resourceId, parent, false);
            viewHolder.Time = (TextView) view.findViewById(R.id.time);
            viewHolder.Message = (TextView) view.findViewById(R.id.message);
            viewHolder.AppName = (TextView) view.findViewById(R.id.packageName);
            viewHolder.AppImage = (ImageView) view.findViewById(R.id.appIcon);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }



        GpsEventList listItem = getItem(position);

        viewHolder.AppImage.setImageDrawable(listItem.getAppIcon());
        viewHolder.AppName.setText(listItem.getAppName());
        viewHolder.Message.setText(listItem.getMessage());
        viewHolder.Time.setText(listItem.getTime());
        return view;
    }

    class ViewHolder{      //当布局加载过后，保存获取到的控件信息。
        ImageView AppImage;
        TextView Message;
        TextView Time;
        TextView AppName;
    }
}
