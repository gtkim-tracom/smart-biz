package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CardAuthRequestVo  extends BicycleVo {

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("cardLength", 1);
        requestFields.put("userCardNum", 8);
        requestFields.put("cardType", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String cardLength;
    private String userCardNum;
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
	public String getCardLength() {
		return cardLength;
	}
	public void setCardLength(String cardLength) {
		this.cardLength = cardLength;
	}
	public String getUserCardNum() {
		return userCardNum;
	}
	public void setUserCardNum(String userCardNum) {
		this.userCardNum = userCardNum;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
    
}
