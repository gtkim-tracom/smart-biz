package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ConnectDownServerResponseVo extends BicycleVo{
	
	
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("bicycleState", 1);
        responseFields.put("bicycleId", 7);
        responseFields.put("update", 1);
        responseFields.put("version", 2);
        responseFields.put("fileCnt", 1);
        responseFields.put("packetSize", 2);
        responseFields.put("total", 4);
        
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
    private String version;
    private String fileCnt;
    private String packetSize;
    private String total;
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
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFileCnt() {
		return fileCnt;
	}
	public void setFileCnt(String fileCnt) {
		this.fileCnt = fileCnt;
	}
	public String getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(String packetSize) {
		this.packetSize = packetSize;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
    
    
    

}
