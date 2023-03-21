package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fincl.miss.server.message.parser.IncludeFieldsRepeatable;

@SuppressWarnings("serial")
public class GpsSendDataRequestVo extends BicycleVo implements IncludeFieldsRepeatable{

	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("regDttm", 7);
        requestFields.put("gpsCount", 1);
        requestFields.put("packetNumber", 1);
        requestFields.put("gpsData", 8);
        
//        requestFields.put("latitude", 4);
//        requestFields.put("longitude", 4);
    }
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    {
    	repeatFields.put("gpsData", "gpsCount"); // 반복될 필드, 반복수 참조 필드
//        repeatFields.put("latitude", "gpsCount"); // 반복될 필드, 반복수 참조 필드
//        repeatFields.put("longitude", "gpsCount"); // 반복될 필드, 반복수 참조 필드
    }
    
    private String bicycleState;
    private String bicycleId;
    private String regDttm;
    private String gpsCount;
    private String packetNumber;
    private String [] gpsData;
    private String [] latitude;
    private String [] longitude;
    
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
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	public String getGpsCount() {
		return gpsCount;
	}
	public void setGpsCount(String gpsCount) {
		this.gpsCount = gpsCount;
	}
	public String getPacketNumber() {
		return packetNumber;
	}
	public void setPacketNumber(String packetNumber) {
		this.packetNumber = packetNumber;
	}
	public String[] getGpsData() {
		return gpsData;
	}
	public void setGpsData(String[] gpsData) {
		this.gpsData = gpsData;
	}
	public String[] getLatitude() {
		return latitude;
	}
	public void setLatitude(String[] latitude) {
		this.latitude = latitude;
	}
	public String[] getLongitude() {
		return longitude;
	}
	public void setLongitude(String[] longitude) {
		this.longitude = longitude;
	}
	public Map<String, Integer> getRequestFields() {
		return requestFields;
	}
	public Map<String, String> getRepeatFields() {
		return repeatFields;
	}
    
}
