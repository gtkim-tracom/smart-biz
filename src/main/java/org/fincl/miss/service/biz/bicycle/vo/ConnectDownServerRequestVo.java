package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ConnectDownServerRequestVo extends BicycleVo{
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("ble_firmwareVs", 2);
        requestFields.put("modem_firmwareVs", 2);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String ble_firmwareVs;
    private String modem_firmwareVs;
    
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
	public String getBle_firmwareVs() {
		return ble_firmwareVs;
	}
	public void setBle_firmwareVs(String ble_firmwareVs) {
		this.ble_firmwareVs = ble_firmwareVs;
	}
	public String getModem_firmwareVs() {
		return modem_firmwareVs;
	}
	public void setModem_firmwareVs(String modem_firmwareVs) {
		this.modem_firmwareVs = modem_firmwareVs;
	}
	
    
    
    

}
