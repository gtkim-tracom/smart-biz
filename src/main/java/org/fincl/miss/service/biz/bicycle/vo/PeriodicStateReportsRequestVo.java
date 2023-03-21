package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PeriodicStateReportsRequestVo  extends BicycleVo{
	
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("timestamp", 7);	//수정 (기존 거치대)
        requestFields.put("usrType", 1);	//add 사용자 타입
        requestFields.put("usrseq",5);		//2019.12.26 
        requestFields.put("bikeType", 1);	//add 자전거 Type(전기)
        requestFields.put("beaconId", 5);	//비콘ID
        requestFields.put("current_speed", 1);
        requestFields.put("batt_lock", 1);    // 배터리 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("battery", 1);
        requestFields.put("beaconbatt", 1);	//비콘 밧데리 
        requestFields.put("elecbatt", 1);	//전기 밧데리 
        requestFields.put("distance", 2);
        requestFields.put("modem_firmwareVs", 2);
        requestFields.put("latitude", 4);
        requestFields.put("longitude", 4);
        requestFields.put("lockState", 1);     // 모터 잠금 : lock(0x01) / unlock(0x00)
        requestFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String timestamp;
    private String usrType;
    private String bikeType;
    private String beaconId;
    private String current_speed;
    private String batt_lock;
    private String battery;
    private String beaconbatt;
    private String elecbatt;
    private String distance;
    private String modem_firmwareVs;
    private String latitude;
    private String longitude;
    private String lockState;
    private String errorId;
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
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	//usrType
	public String getUsrType() {
		return usrType;
	}
	public void setUsrType(String usrType) {
		this.usrType = usrType;
	}
	//bikeType
	public String getBikeType() {
		return bikeType;
	}
	public void setBikeType(String bikeType) {
		this.bikeType = bikeType;
	}
	
	public String getBeaconId() {
		return beaconId;
	}
	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}
	//
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	
	public String getBeaconbatt() {
		return beaconbatt;
	}
	public void setBeaconbatt(String beaconbatt) {
		this.beaconbatt = beaconbatt;
	}
	//elecbatt
	public String getElecbatt() {
		return elecbatt;
	}
	public void setElecbatt(String elecbatt) {
		this.elecbatt = elecbatt;
	}
	
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getModem_firmwareVs() {
		return modem_firmwareVs;
	}
	public void setModem_firmwareVs(String modem_firmwareVs) {
		this.modem_firmwareVs = modem_firmwareVs;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
    
    public String getUsrseq(){
    	
    	return usrseq;
    }
    
    public void setUsrseq(String usrseq){
    	
    	this.usrseq = usrseq;
    	
    }
}
