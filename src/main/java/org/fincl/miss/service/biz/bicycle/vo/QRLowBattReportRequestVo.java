package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QRLowBattReportRequestVo extends BicycleVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("regDttm", 7);
        requestFields.put("battery", 1);
        requestFields.put("beaconBattery", 1);
        requestFields.put("elecBattery", 1);
        requestFields.put("lockState", 1);
        requestFields.put("lat", 4);
        requestFields.put("lon", 4);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String regDttm;
    private String battery;
    private String beaconBattery;
    private String elecBattery;
    private String lockState;
    private String lat;
    private String lon;
    
    
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getBicycleState() {
		return bicycleState;
	}
	public void setBicycleState(String bicycleState) {
		this.bicycleState = bicycleState;
	}
	public String getBicycleId() {
		return bicycleId;
	}
	public void setBicycleId(String bicycleId) {
		this.bicycleId = bicycleId;
	}
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getBeaconBattery() {
		return beaconBattery;
	}
	public void setBeaconBattery(String beaconBattery) {
		this.beaconBattery = beaconBattery;
	}
	public String getElecBattery() {
		return elecBattery;
	}
	public void setElecBattery(String elecBattery) {
		this.elecBattery = elecBattery;
	}
	
    
    
    
    

}
