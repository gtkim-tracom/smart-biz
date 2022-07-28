package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ReturnRequestVo extends BicycleVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("mountsId", 7);
        requestFields.put("lockState", 1);
        requestFields.put("rentTime", 2);
        requestFields.put("distance", 2);
        requestFields.put("returnForm", 1);
        requestFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String mountsId;
    private String lockState;
    private String rentTime;
    private String distance;
    private String returnForm;
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
	public String getMountsId() {
		return mountsId;
	}
	public void setMountsId(String mountsId) {
		this.mountsId = mountsId;
	}
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	public String getRentTime() {
		return rentTime;
	}
	public void setRentTime(String rentTime) {
		this.rentTime = rentTime;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getReturnForm() {
		return returnForm;
	}
	public void setReturnForm(String returnForm) {
		this.returnForm = returnForm;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
    
    
    

}
