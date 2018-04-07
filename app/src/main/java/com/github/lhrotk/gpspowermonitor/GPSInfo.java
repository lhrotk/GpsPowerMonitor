package com.github.lhrotk.gpspowermonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by LHROTK on 2018/3/27.
 */

public class GPSInfo {
	public static final int MAX_MATRIX = 5000;
	public static final int GPS_ENERGY = 143;
    public ArrayList<Boolean> onOff = new ArrayList<Boolean>();
    public ArrayList<String> packageName = new ArrayList<String>();
    public ArrayList<Calendar> time = new ArrayList<Calendar>();
    public ArrayList<Calendar> GPSStatus = new ArrayList<Calendar>();
    private ArrayList<String> GPSPackageName = new ArrayList<String>();
    private ArrayList<Double> GPSPower = new ArrayList<Double>();
    private ArrayList<Double> GPSPower1 = new ArrayList<Double>();
    private ArrayList<Double> GPSPower2 = new ArrayList<Double>();
    private GpsUsageSummary summary = new GpsUsageSummary();
    private int counter=-1;
    private int GPScounter=-1;
    private int calculatedC=-1;

    public GPSInfo(){
		GPSPower.add(0.0);
		GPSPower1.add(0.0);
		GPSPower2.add(0.0);
		GPSPackageName.add("unknown");
	}
    
    void updateUsage(GpsLogInfo inputlog)
    {

		onOff.add(inputlog.isOnOff());
		packageName.add(inputlog.getPackageName());
		time.add(inputlog.getTime());
		counter++;
		//System.out.println("if");
    	//if (counter>=MAX_MATRIX)
    	//{
    		//counter=0;
    	//}
    	//System.out.println(getCounter());
    }
    
    void updateGPSStatus (Calendar gpstime) {
		GPSStatus.add(gpstime);
		//System.out.println("if");
    	GPScounter++;
    }
    
    // average power on consumption 357mw
    void calculateUsage(int summaryMode, int tempGPScounter)
    {
    	int calGPScounter=0;
    	int calCounter=0;
    	int numberApps=1;
    	double tailpower=0;
    	int concurrent=0;
    	int tempcurrent=concurrent;
    	int []concurrentList=new int[10];
    	double tempEnergy;
    	int index=0;
    	
		if (tempGPScounter%2==0)
		{
			calGPScounter=tempGPScounter-1;
		}
		else
		{
			calGPScounter=tempGPScounter;
		}
		calCounter=counter;

		while(time.get(calCounter).getTimeInMillis()>GPSStatus.get(calGPScounter).getTimeInMillis())
		{
			calCounter--;
		}
		
    	int tempcalCounter=calCounter;
    	int tempcalCounter2=calCounter;
    	if (summaryMode==1) //tail energy to the last application 
    	{ 
    		if (calGPScounter>=1)
    		{
    			if(time.get(tempcalCounter2).getTimeInMillis()<=GPSStatus.get(calGPScounter-1).getTimeInMillis())
				{
					tempEnergy=(double) (GPSStatus.get(calGPScounter).getTimeInMillis() - GPSStatus.get(calGPScounter-1).getTimeInMillis()) / 1000000 * GPS_ENERGY;
					GPSPower.set(0,GPSPower.get(0)+tempEnergy);
				}
				else {
    				numberApps=1;
					while (time.get(tempcalCounter2).getTimeInMillis() > GPSStatus.get(calGPScounter - 1).getTimeInMillis()) {
						tempcalCounter2 = tempcalCounter - 1;
						if (numberApps == 1) {
							while (onOff.get(tempcalCounter) == true) {
								tempcalCounter--;
							}
							concurrentList = setconcurrentList(concurrentList, tempcalCounter);
							concurrent++;
							tailpower = (double) (GPSStatus.get(calGPScounter).getTimeInMillis() - time.get(tempcalCounter).getTimeInMillis()) / 1000000 * GPS_ENERGY;
							index = GPSPackageName.indexOf(packageName.get(tempcalCounter));
							if (index == -1) {
								GPSPower.add(tailpower);
								GPSPower1.add(0.0);
								GPSPower2.add(0.0);
								GPSPackageName.add(packageName.get(tempcalCounter));
							} else {
								GPSPower.set(index, GPSPower.get(index) + tailpower);
							}
						}
						else{
							if (onOff.get(tempcalCounter) == true) {
								concurrentList = clearconcurrentList(concurrentList, tempcalCounter);
								concurrent--;
							} else {
								concurrentList = setconcurrentList(concurrentList, tempcalCounter);
								concurrent++;
							}
						}

						tempEnergy = (double) (time.get(tempcalCounter).getTimeInMillis() - time.get(tempcalCounter2).getTimeInMillis()) / 1000000 * GPS_ENERGY;
						tempEnergy = tempEnergy / concurrent;
						for (int i = 0; i < 10; i++) {
							if (concurrentList[i] != 0) {
								index = GPSPackageName.indexOf(packageName.get(concurrentList[i]));
								if (index == -1) {
									GPSPower.add(tempEnergy);
									GPSPower1.add(0.0);
									GPSPower2.add(0.0);
									GPSPackageName.add(packageName.get(concurrentList[i]));
								} else {
									GPSPower.set(index, GPSPower.get(index) + tempEnergy);
								}
							}
						}
						if (onOff.get(tempcalCounter2) == false)
						{
							numberApps++;
						}
						tempcalCounter = tempcalCounter2;
					}
				}
    		}
    	}
    	else if (summaryMode==2) //tail energy apply to head
    	{
    		if (calGPScounter>=1)
    		{
				if(time.get(tempcalCounter2).getTimeInMillis()<=GPSStatus.get(calGPScounter-1).getTimeInMillis())
				{
					tempEnergy=(double) (GPSStatus.get(calGPScounter).getTimeInMillis() - GPSStatus.get(calGPScounter-1).getTimeInMillis()) / 1000000 * GPS_ENERGY;
					GPSPower1.set(0,GPSPower1.get(0)+tempEnergy);
				}
				else {
					numberApps=1;
					while (time.get(tempcalCounter2).getTimeInMillis() > GPSStatus.get(calGPScounter - 1).getTimeInMillis()) {
						tempcalCounter2 = tempcalCounter - 1;
						if (numberApps == 1) {
							while (onOff.get(tempcalCounter) == true) {
								tempcalCounter--;
							}
							concurrentList = setconcurrentList(concurrentList, tempcalCounter);
							concurrent++;
							tailpower = (double) (GPSStatus.get(calGPScounter).getTimeInMillis() - time.get(tempcalCounter).getTimeInMillis()) / 1000000 * GPS_ENERGY;

						}
						else {
							if (onOff.get(tempcalCounter) == true) {
								concurrentList = clearconcurrentList(concurrentList, tempcalCounter);
								concurrent--;
							} else {
								concurrentList = setconcurrentList(concurrentList, tempcalCounter);
								concurrent++;
							}
						}

						tempEnergy = (double) (time.get(tempcalCounter).getTimeInMillis() - time.get(tempcalCounter2).getTimeInMillis()) / 1000000 * GPS_ENERGY;
						tempEnergy = tempEnergy / concurrent;
						for (int i = 0; i < 10; i++) {
							if (concurrentList[i] != 0) {
								index = GPSPackageName.indexOf(packageName.get(concurrentList[i]));
								if (index == -1) {
									GPSPower1.add(tempEnergy);
									GPSPower.add(0.0);
									GPSPower2.add(0.0);
									GPSPackageName.add(packageName.get(concurrentList[i]));
								} else {
									GPSPower1.set(index, GPSPower1.get(index) + tempEnergy);
								}
							}
						}
						if (onOff.get(tempcalCounter) == false) {
							numberApps++;
						}
						tempcalCounter = tempcalCounter2;
					}
					index = GPSPackageName.indexOf(packageName.get(tempcalCounter + 1));
					if (index == -1) {
						GPSPower1.add(tailpower);
						GPSPower.add(0.0);
						GPSPower2.add(0.0);
						GPSPackageName.add(packageName.get(tempcalCounter + 1));
					} else {
						GPSPower1.set(index, GPSPower1.get(index) + tailpower);
					}
				}
    		}
    	}
    	else //tail energy applied distributed by average
    	{
    		if (calGPScounter>=1)
    		{
				if(time.get(tempcalCounter2).getTimeInMillis()<=GPSStatus.get(calGPScounter-1).getTimeInMillis())
				{
					tempEnergy=(double) (GPSStatus.get(calGPScounter).getTimeInMillis() - GPSStatus.get(calGPScounter-1).getTimeInMillis()) / 1000000 * GPS_ENERGY;
					GPSPower2.set(0,GPSPower2.get(0)+tempEnergy);
				}
				else {
					numberApps=1;
					while (time.get(tempcalCounter2).getTimeInMillis() > GPSStatus.get(calGPScounter - 1).getTimeInMillis()) {
						tempcalCounter2 = tempcalCounter - 1;
						if (numberApps == 1) {
							while (onOff.get(tempcalCounter) == true) {
								tempcalCounter--;
							}
							concurrent++;
							concurrentList = setconcurrentList(concurrentList, tempcalCounter);
							tailpower = (double) (GPSStatus.get(calGPScounter).getTimeInMillis() - time.get(tempcalCounter).getTimeInMillis()) / 1000000 * GPS_ENERGY;

						}
						else {
							if (onOff.get(tempcalCounter) == true) {
								concurrentList = clearconcurrentList(concurrentList, tempcalCounter);
								concurrent--;
							} else {
								concurrentList = setconcurrentList(concurrentList, tempcalCounter);
								concurrent++;
							}
						}

						tempEnergy = (double) (time.get(tempcalCounter).getTimeInMillis() - time.get(tempcalCounter2).getTimeInMillis()) / 1000000 * GPS_ENERGY;
						tempEnergy = tempEnergy / concurrent;
						for (int i = 0; i < 10; i++) {
							if (concurrentList[i] != 0) {
								index = GPSPackageName.indexOf(packageName.get(concurrentList[i]));
								if (index == -1) {
									GPSPower2.add(tempEnergy);
									GPSPower.add(0.0);
									GPSPower1.add(0.0);
									GPSPackageName.add(packageName.get(concurrentList[i]));
								} else {
									GPSPower2.set(index, GPSPower2.get(index) + tempEnergy);
								}
							}
						}
						if (onOff.get(tempcalCounter) == false) {
							numberApps++;
						}
						tempcalCounter = tempcalCounter2;
					}
					tailpower = tailpower / (numberApps-1);
					for (int i = tempcalCounter; i <= calCounter; i++) {
						if (onOff.get(i) == true) {
							index = GPSPackageName.indexOf(packageName.get(i));
							if (index == -1) {
								GPSPower2.add(tailpower);
								GPSPower1.add(0.0);
								GPSPower.add(0.0);
								GPSPackageName.add(packageName.get(i));
							} else {
								GPSPower2.set(index, GPSPower2.get(index) + tailpower);
							}
						}
					}
				}
    		}
    	}

    }
       
    int[] setconcurrentList (int [] concurrentlist, int counter) {
    	for (int i=0;i<10;i++)
    	{
    		if (concurrentlist[i]==0) {
    			concurrentlist[i]=counter;
    			break;
    		}
    	}
    	return concurrentlist;
    }
    
    int[] clearconcurrentList (int [] concurrentlist, int counter) {
    	for (int i=0;i<10;i++)
    	{
    		if (concurrentlist[i]==counter) {
    			concurrentlist[i]=0;
    		}
    	}
    	return concurrentlist;
    }

	public GpsUsageSummary getSummary() {
		if (GPScounter>0&&counter>0) {
	    	for(int i=calculatedC+1; i<=GPScounter; i++) {
				if (i%2!=0)
		    	{
		    		calculateUsage(1,i);    		
		    		calculateUsage(2,i);
		    		calculateUsage(3,i);
		    		calculatedC=i; 
		    	}
	    	}
		}
		summary.setGPSPackageName(GPSPackageName);
		summary.setGPSPower(GPSPower);
		summary.setGPSPower1(GPSPower1);
		summary.setGPSPower2(GPSPower2);
		return summary;
	}

	public void setSummary(GpsUsageSummary summary) {
		this.summary = summary;
	}    
}
