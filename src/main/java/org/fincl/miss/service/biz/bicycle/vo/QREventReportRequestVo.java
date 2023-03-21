package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QREventReportRequestVo extends BicycleVo{
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("regDttm", 7);
        requestFields.put("battery", 1);
        requestFields.put("batt_lock", 1);			// 배터리 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("current_speed", 1);
        requestFields.put("lockState", 1);			// 모터 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("gps_Latitude", 4);
        requestFields.put("gps_Longitude",4);
        requestFields.put("eventType", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String regDttm;
    private String battery;
    private String batt_lock;
    private String current_speed;
    private String lockState;
    private String gps_Latitude;
    private String gps_Longitude;
    private String eventType;
    
    
    
    
    
	public String getBatt_lock() {
		return batt_lock;
	}
	public void setBatt_lock(String batt_lock) {
		this.batt_lock = batt_lock;
	}
	public String getCurrent_speed() {
		return current_speed;
	}
	public void setCurrent_speed(String current_speed) {
		this.current_speed = current_speed;
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
	
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	
	
	
	public String getGps_Latitude() {
		return gps_Latitude;
	}
	public void setGps_Latitude(String gps_Latitude) {
		this.gps_Latitude = gps_Latitude;
	}
	public String getGps_Longitude() {
		return gps_Longitude;
	}
	public void setGps_Longitude(String gps_Longitude) {
		this.gps_Longitude = gps_Longitude;
	}
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
}
