package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class RentWaitingResponseVer2Vo extends BicycleVo{
	
	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("bicycleState", 1);
        responseFields.put("bicycleId", 7);
        responseFields.put("rentWait", 1);
        responseFields.put("userCardRegist", 1);
        responseFields.put("langCd", 1);
    }
    
    public final Map<String, Integer> responseFailFields = new LinkedHashMap<String, Integer>();
    {
    	responseFailFields.put("bicycleState", 1);
    	responseFailFields.put("bicycleId", 7);
    	responseFailFields.put("errorId", 1);
    	responseFailFields.put("langCd", 1);
    }
    
    
    
    private String bicycleState;
    private String bicycleId;
    private String rentWait;
    private String userCardRegist;
    private String errorId;
    private String langCd;
    private String Password;
    
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
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
	public String getRentWait() {
		return rentWait;
	}
	public void setRentWait(String rentWait) {
		this.rentWait = rentWait;
	}
	public String getUserCardRegist() {
		return userCardRegist;
	}
	public void setUserCardRegist(String userCardRegist) {
		this.userCardRegist = userCardRegist;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	public String getLangCd() {
		return langCd;
	}
	public void setLangCd(String langCd) {
		this.langCd = langCd;
	}
    
    
    
    

}
