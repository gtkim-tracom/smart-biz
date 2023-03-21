package org.fincl.miss.service.biz.bicycle.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class AdministratorAuthRequestVo extends BicycleVo {
    
    public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("bicycleState", 1);
        requestFields.put("bicycleId", 7);
        requestFields.put("administratorId", 8);
    }
    
    private String bicycleState;
    private String bicycleId;
    private String administratorId;
    
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
    
    public String getAdministratorId() {
        return administratorId;
    }
    
    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
    }
    
}
