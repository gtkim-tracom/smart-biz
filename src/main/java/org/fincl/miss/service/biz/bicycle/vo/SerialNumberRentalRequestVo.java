package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class SerialNumberRentalRequestVo extends BicycleVo {

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("serialNumber", 8);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String serialNumber;
    
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
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	@Override
	public String toString() {
		return "SerialNumberRentalRequestVo [bicycleState=" + bicycleState
				+ ", bicycleId=" + bicycleId + ", serialNumber=" + serialNumber
				+ "]";
	}

}
