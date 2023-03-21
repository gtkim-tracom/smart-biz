package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PeriodicStateReportsResponseVo extends BicycleVo{
	
	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("bicycleState", 1);
        responseFields.put("bicycleId", 7);
        responseFields.put("ble_fwupdate", 1);
        responseFields.put("modem_fwupdate", 1);
        responseFields.put("dayAndNight", 1);	//환경설정
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
    private String ble_fwupdate;
    private String modem_fwupdate;
    private String errorId;
    private String dayAndNight;
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
	public String getBle_fwupdate() {
		return ble_fwupdate;
	}
	public void setBle_fwupdate(String ble_fwupdate) {
		this.ble_fwupdate = ble_fwupdate;
	}
	public String getModem_fwupdate() {
		return modem_fwupdate;
	}
	public void setModem_fwupdate(String modem_fwupdate) {
		this.modem_fwupdate = modem_fwupdate;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	public String getDayAndNight() {
		return dayAndNight;
	}
	public void setDayAndNight(String dayAndNight) {
		this.dayAndNight = dayAndNight;
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
