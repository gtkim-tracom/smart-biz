package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class FirmwareDownRequestVo extends BicycleVo{

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("fileNum", 1);
        requestFields.put("packetNum", 2);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String fileNum;
    private String packetNum;
    
    
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
	public String getFileNum() {
		return fileNum;
	}
	public void setFileNum(String fileNum) {
		this.fileNum = fileNum;
	}
	public String getPacketNum() {
		return packetNum;
	}
	public void setPacketNum(String packetNum) {
		this.packetNum = packetNum;
	}
    
    
    
    
}
