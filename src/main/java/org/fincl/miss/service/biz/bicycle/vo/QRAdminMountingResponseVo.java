package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QRAdminMountingResponseVo extends BicycleVo{
	
	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("bicycleState", 1);
        responseFields.put("bicycleId", 7);
        responseFields.put("ble_firmwareUpdate", 2);
        responseFields.put("modem_firmwareUpdate", 2);
        responseFields.put("configChn", 1);
        responseFields.put("periodHour", 1);
		responseFields.put("periodMinute", 1);
    }
    
    public final Map<String, Integer> responseFailFields = new LinkedHashMap<String, Integer>();
    {
    	responseFailFields.put("bicycleState", 1);
    	responseFailFields.put("bicycleId", 7);
    	responseFailFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String ble_firmwareUpdate;
    private String modem_firmwareUpdate;
    private String configChn;
    private String errorId;
    private String periodHour;
    private String periodMinute;
    
    
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
	
	public String getBle_firmwareUpdate() {
		return ble_firmwareUpdate;
	}
	public void setBle_firmwareUpdate(String ble_firmwareUpdate) {
		this.ble_firmwareUpdate = ble_firmwareUpdate;
	}
	public String getModem_firmwareUpdate() {
		return modem_firmwareUpdate;
	}
	public void setModem_firmwareUpdate(String modem_firmwareUpdate) {
		this.modem_firmwareUpdate = modem_firmwareUpdate;
	}
	public String getConfigChn() {
		return configChn;
	}
	public void setConfigChn(String configChn) {
		this.configChn = configChn;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	public String getPeriodHour() {
		return periodHour;
	}
	public void setPeriodHour(String periodHour) {
		this.periodHour = periodHour;
	}
	public String getPeriodMinute() {
		return periodMinute;
	}
	public void setPeriodMinute(String periodMinute) {
		this.periodMinute = periodMinute;
	}
	
	
	
	
    
    

}
