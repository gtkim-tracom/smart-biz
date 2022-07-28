package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PeriodicStateReportsRequestVo  extends BicycleVo{
	
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("mountsId", 7);
        requestFields.put("battery", 1);
        requestFields.put("returnForm", 1);
        requestFields.put("firmwareVs", 2);
        requestFields.put("imageVs", 2);
        requestFields.put("soundVs", 2);
        requestFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String mountsId;
    private String battery;
    private String returnForm;
    private String firmwareVs;
    private String imageVs;
    private String soundVs;
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
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getReturnForm() {
		return returnForm;
	}
	public void setReturnForm(String returnForm) {
		this.returnForm = returnForm;
	}
	public String getFirmwareVs() {
		return firmwareVs;
	}
	public void setFirmwareVs(String firmwareVs) {
		this.firmwareVs = firmwareVs;
	}
	public String getImageVs() {
		return imageVs;
	}
	public void setImageVs(String imageVs) {
		this.imageVs = imageVs;
	}
	public String getSoundVs() {
		return soundVs;
	}
	public void setSoundVs(String soundVs) {
		this.soundVs = soundVs;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
    
    
    

}
