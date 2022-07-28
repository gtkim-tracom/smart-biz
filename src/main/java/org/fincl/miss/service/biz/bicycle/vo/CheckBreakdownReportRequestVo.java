package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CheckBreakdownReportRequestVo extends BicycleVo{
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("mountsId", 7);
        requestFields.put("brokenType", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String mountsId;
    private String brokenType;
    
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
	public String getBrokenType() {
		return brokenType;
	}
	public void setBrokenType(String brokenType) {
		this.brokenType = brokenType;
	}

    
    
}
