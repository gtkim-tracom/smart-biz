package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class RentalRequestVo extends BicycleVo{
	
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("mountsId", 7);
        requestFields.put("rentPassword", 4);
        requestFields.put("lockState", 1);
        requestFields.put("returnForm", 1);
        requestFields.put("battery", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String mountsId;
    private String rentPassword;
    private String lockState;
    private String returnForm;
    private String battery;
    
    
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
	public String getRentPassword() {
		return rentPassword;
	}
	public void setRentPassword(String rentPassword) {
		this.rentPassword = rentPassword;
	}
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	public String getReturnForm() {
		return returnForm;
	}
	public void setReturnForm(String returnForm) {
		this.returnForm = returnForm;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
    
    

}
