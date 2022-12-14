package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PeriodicStateReportsResponseVo extends BicycleVo{
	
	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("bicycleState", 1);
        responseFields.put("bicycleId", 7);
        responseFields.put("update", 1);
        responseFields.put("dayAndNight", 1);
    }
    
    public final Map<String, Integer> responseFailFields = new LinkedHashMap<String, Integer>();
    {
    	responseFailFields.put("bicycleState", 1);
    	responseFailFields.put("bicycleId", 7);
    	responseFailFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String update;
    private String errorId;
    private String dayAndNight;
    
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
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
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

}
