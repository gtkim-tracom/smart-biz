package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QrSetPeriodResponseVo extends BicycleVo{

	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
		responseFields.put("bicycleState", 1);
		responseFields.put("bicycleId", 7);
		responseFields.put("managerType", 1);
		responseFields.put("movePeriod_Hour", 1);
		responseFields.put("movePeriod_Minute", 1);
		responseFields.put("installPeriod_Hour", 1);
		responseFields.put("installPeriod_Minute", 1);
		responseFields.put("userType", 1);
		responseFields.put("rentPeriod_Hour", 1);
		responseFields.put("rentPeriod_Minute", 1);
		responseFields.put("returnPeriod_Hour", 1);
		responseFields.put("returnPeriod_Minute", 1);
    }
    
    public final Map<String, Integer> responseFailFields = new LinkedHashMap<String, Integer>();
    {
    	responseFailFields.put("bicycleState", 1);
    	responseFailFields.put("bicycleId", 7);
    	responseFailFields.put("errorId", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String managerType;
    private String rentPeriod_Hour;
    private String rentPeriod_Minute;
    private String returnPeriod_Hour;
    private String returnPeriod_Minute;
    private String userType;
    private String movePeriod_Hour;
    private String movePeriod_Minute;
    private String installPeriod_Hour;
    private String installPeriod_Minute;
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
	
		
	public String getuserType() {
		return userType;
	}
	public void setuserType(String userType) {
		this.userType = userType;
	}
	
    
	public String getRentPeriod_Hour() {
		return rentPeriod_Hour;
	}
	public void setRentPeriod_Hour(String rentPeriod_Hour) {
		this.rentPeriod_Hour = rentPeriod_Hour;
	}
	public String getRentPeriod_Minute() {
		return rentPeriod_Minute;
	}
	public void setRentPeriod_Minute(String rentPeriod_Minute) {
		this.rentPeriod_Minute = rentPeriod_Minute;
	}
	public String getReturnPeriod_Hour() {
		return returnPeriod_Hour;
	}
	public void setReturnPeriod_Hour(String returnPeriod_Hour) {
		this.returnPeriod_Hour = returnPeriod_Hour;
	}
	public String getReturnPeriod_Minute() {
		return returnPeriod_Minute;
	}
	public void setReturnPeriod_Minute(String returnPeriod_Minute) {
		this.returnPeriod_Minute = returnPeriod_Minute;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getManagerType() {
		return managerType;
	}
	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}
	
	public String getMovePeriod_Hour() {
		return movePeriod_Hour;
	}
	public void setMovePeriod_Hour(String movePeriod_Hour) {
		this.movePeriod_Hour = movePeriod_Hour;
	}
	public String getMovePeriod_Minute() {
		return movePeriod_Minute;
	}
	public void setMovePeriod_Minute(String movePeriod_Minute) {
		this.movePeriod_Minute = movePeriod_Minute;
	}
	public String getInstallPeriod_Hour() {
		return installPeriod_Hour;
	}
	public void setInstallPeriod_Hour(String installPeriod_Hour) {
		this.installPeriod_Hour = installPeriod_Hour;
	}
	public String getInstallPeriod_Minute() {
		return installPeriod_Minute;
	}
	public void setInstallPeriod_Minute(String installPeriod_Minute) {
		this.installPeriod_Minute = installPeriod_Minute;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
}
