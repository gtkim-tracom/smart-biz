package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QRReturnRequestVo extends BicycleVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("elecbicycle", 1);
        requestFields.put("regDttm", 7);
        requestFields.put("beaconId", 7);
        requestFields.put("lockState", 1);
        requestFields.put("latitude", 4);
        requestFields.put("longitude", 4);
        requestFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String elecbicycle;
    private String regDttm;
    private String beaconId;
    private String lockState;
    private String latitude;
    private String longitude;
    private String errorId;
    
    
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
	public String getElecbicycle() {
		return elecbicycle;
	}
	public void setElecbicycle(String elecbicycle) {
		this.elecbicycle = elecbicycle;
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
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
}
