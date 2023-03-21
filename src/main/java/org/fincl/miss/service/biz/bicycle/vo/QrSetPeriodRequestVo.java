package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QrSetPeriodRequestVo  extends BicycleVo {

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
    	requestFields.put("bicycleState", 1);
    	requestFields.put("bicycleId", 7);
    	requestFields.put("curtimestamp", 7);
    	requestFields.put("managerType", 1);
    	requestFields.put("rentPeriod", 2);
    	requestFields.put("returnPeriod", 2);
    	requestFields.put("userType", 1);
    	requestFields.put("movePeriod", 2);
    	requestFields.put("installPeriod", 2);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String curtimestamp;
    
    private String userType;
    private String rentPeriod;
    private String returnPeriod;
    
    private String managerType;
    private String movePeriod;
    private String installPeriod;
  
    
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
	
	public String getCurtimestamp() {
		return curtimestamp;
	}
	public void setCurtimestamp(String curtimestamp) {
		this.curtimestamp = curtimestamp;
	}
	
		
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getRentPeriod() {
		return rentPeriod;
	}
	public void setRentPeriod(String rentPeriod) {
		this.rentPeriod = rentPeriod;
	}
	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
    
	public String getManagerType() {
		return managerType;
	}
	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}
	
	public String getMovePeriod() {
		return movePeriod;
	}
	public void setMovePeriod(String movePeriod) {
		this.movePeriod = movePeriod;
	}
	
	public String getInstallPeriod() {
		return installPeriod;
	}
	public void setInstallPeriod(String installPeriod) {
		this.installPeriod = installPeriod;
	}
    
}
