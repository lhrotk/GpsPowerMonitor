package com.github.lhrotk.gpspowermonitor;

import java.util.Calendar;

/**
 * Created by LHROTK on 2018/3/27.
 */

public class GpsLogInfo {
    private boolean OnOff;
    private String packageName;
    private Calendar time;
	public boolean isOnOff() {
		return OnOff;
	}
	public void setOnOff(boolean onOff) {
		OnOff = onOff;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Calendar getTime() {
		return time;
	}
	public void setTime(Calendar time) {
		this.time = time;
	}
}
