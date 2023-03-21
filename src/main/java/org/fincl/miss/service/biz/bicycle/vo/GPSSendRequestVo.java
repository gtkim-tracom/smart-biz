package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fincl.miss.server.message.parser.IncludeFieldsRepeatable;

@SuppressWarnings("serial")
public class GPSSendRequestVo extends BicycleVo implements IncludeFieldsRepeatable {
    
    public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("baseId", 7);
        requestFields.put("gpsCnt", 1);
        requestFields.put("packetNumber", 1);
        requestFields.put("nw", 4);
        requestFields.put("es", 4);
    }
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    {
        repeatFields.put("nw", "gpsCnt"); // 반복될 필드, 반복수 참조 필드
        repeatFields.put("es", "gpsCnt"); // 반복될 필드, 반복수 참조 필드
    }
    
    private String bicycleState;
    private String bicycleId;
    
    private String baseId;
    private int gpsCnt;
    private int packetNumber;
    private String[] nw;
    private String[] es;
    
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
    
    public String getBaseId() {
        return baseId;
    }
    
    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }
    
    public int getGpsCnt() {
        return gpsCnt;
    }
    
    public void setGpsCnt(int gpsCnt) {
        this.gpsCnt = gpsCnt;
    }
    
    public int getPacketNumber() {
        return packetNumber;
    }
    
    public void setPacketNumber(int packetNumber) {
        this.packetNumber = packetNumber;
    }
    
    public String[] getNw() {
        return nw;
    }
    
    public void setNw(String[] nw) {
        this.nw = nw;
    }
    
    public String[] getEs() {
        return es;
    }
    
    public void setEs(String[] es) {
        this.es = es;
    }
    
    @Override
    public Map<String, String> getRepeatFields() {
        return this.repeatFields;
    }
    
}
