package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class PasswordAuthRequestVo extends BicycleVo {

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("cardLength", 1);
        requestFields.put("cardNum", 8);
        requestFields.put("cardType", 1);
        requestFields.put("password", 4);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String password;
    private String cardLength;
    private String cardNum;
    private String cardType;
    
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	@Override
	public String toString() {
		return "PasswordAuthRequestVo [requestFields=" + requestFields
				+ ", bicycleState=" + bicycleState + ", bicycleId=" + bicycleId
				+ ", password=" + password + ", cardLength=" + cardLength
				+ ", cardNum=" + cardNum + ", cardType=" + cardType + "]";
	}

}
