package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ImageFileInfoDownRequestVo extends BicycleVo{

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("imageVersion", 2);
        requestFields.put("changeFileCnt", 1);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String imageVersion;
    private String changeFileCnt;
    
    
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
	public String getImageVersion() {
		return imageVersion;
	}
	public void setImageVersion(String imageVersion) {
		this.imageVersion = imageVersion;
	}
	public String getChangeFileCnt() {
		return changeFileCnt;
	}
	public void setChangeFileCnt(String changeFileCnt) {
		this.changeFileCnt = changeFileCnt;
	}
    
    
    
    
    
}
