package com.github.lhrotk.gpspowermonitor;

import java.util.ArrayList;

/**
 * Created by lhrotk on 2018/3/27.
 */

public class GpsUsageSummary {
    private ArrayList<String> GPSPackageName = new ArrayList<String>();
    private ArrayList<Double> GPSPower = new ArrayList<Double>();
    private ArrayList<Double> GPSPower1 = new ArrayList<Double>();
    private ArrayList<Double> GPSPower2 = new ArrayList<Double>();
	public ArrayList<String> getGPSPackageName() {
		return GPSPackageName;
	}
	public void setGPSPackageName(ArrayList<String> gPSPackageName) {
		GPSPackageName = gPSPackageName;
	}
	public ArrayList<Double> getGPSPower() {
		return GPSPower;
	}
	public void setGPSPower(ArrayList<Double> gPSPower) {
		GPSPower = gPSPower;
	}
	public ArrayList<Double> getGPSPower1() {
		return GPSPower1;
	}
	public void setGPSPower1(ArrayList<Double> gPSPower1) {
		GPSPower1 = gPSPower1;
	}
	public ArrayList<Double> getGPSPower2() {
		return GPSPower2;
	}
	public void setGPSPower2(ArrayList<Double> gPSPower2) {
		GPSPower2 = gPSPower2;
	}
}
