package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class AdminMoveRequestVo extends BicycleVo{
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("mountsId", 7);
        requestFields.put("cardLength", 1);
        requestFields.put("cardNum", 8);
        requestFields.put("cardType", 1);
        requestFields.put("password",4);
        requestFields.put("lockState", 1);
        requestFields.put("battery", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String mountsId;
    private String lockState;
    private String cardLength;
    private String cardNum;
    private String cardType;
    private String password;
    private String battery;
    private String rentTime;
    private String distance;
    
    
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
	public String getLockState() {
		return lockState;
	}
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getRentTime() {
		return rentTime;
	}
	public void setRentTime(String rentTime) {
		this.rentTime = rentTime;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getCardLength() {
		return cardLength;
	}
	public void setCardLength(String cardLength) {
		this.cardLength = cardLength;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
    
    
}
