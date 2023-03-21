package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QRAdminMountingRequestVo extends BicycleVo{
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("regDttm", 7);
        requestFields.put("beaconId", 5);
        requestFields.put("current_speed", 1);
        requestFields.put("batt_lock", 1);      // 배터리 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("usrseq", 5);			//2020.01.02 usr_seq 추가 
        requestFields.put("lockState", 1);		// 모터 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("battery", 1);
        requestFields.put("beaconBattery", 1);
        requestFields.put("bikeBattery", 1);
        requestFields.put("gps_Latitude", 4);
        requestFields.put("gps_Longitude", 4);
        requestFields.put("ble_firmwareVersion", 2);
        requestFields.put("modem_firmwareVersion", 2);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String regDttm;
    private String beaconId;
    private String current_speed;
    private String batt_lock;
    private String lockState;
    private String battery;
    private String beaconBattery;
    private String bikeBattery;
    private String gps_Latitude;
    private String gps_Longitude;
    private String ble_firmwareVersion;
    private String modem_firmwareVersion;
    private String	usrseq;
    
    
    
	public String getCurrent_speed() {
		return current_speed;
	}
	public void setCurrent_speed(String current_speed) {
		this.current_speed = current_speed;
	}
	public String getBatt_lock() {
		return batt_lock;
	}
	public void setBatt_lock(String batt_lock) {
		this.batt_lock = batt_lock;
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
	public String getBeaconId() {
		return beaconId;
	}
	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
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
	
	public String getUsrseq(){
    	
    	return usrseq;
    }
    
    public void setUsrseq(String usrseq){
    	
    	this.usrseq = usrseq;
    }
	
	public String getBikeBattery() {
		return bikeBattery;
	}
	public void setBikeBattery(String bikeBattery) {
		this.bikeBattery = bikeBattery;
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
	public String getBle_firmwareVersion() {
		return ble_firmwareVersion;
	}
	public void setBle_firmwareVersion(String ble_firmwareVersion) {
		this.ble_firmwareVersion = ble_firmwareVersion;
	}
	public String getModem_firmwareVersion() {
		return modem_firmwareVersion;
	}
	public void setModem_firmwareVersion(String modem_firmwareVersion) {
		this.modem_firmwareVersion = modem_firmwareVersion;
	}
	
	
	/*
	pubic Map<String, Integer> getRequestFields() {
		return requestFields;
	}
    */
    
	
    
    
}
