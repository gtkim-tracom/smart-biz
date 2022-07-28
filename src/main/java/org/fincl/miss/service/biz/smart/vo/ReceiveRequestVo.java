package org.fincl.miss.service.biz.smart.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ReceiveRequestVo extends SmartVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("sendDtm", 10);
        requestFields.put("bizInfo", 3);
        requestFields.put("sendName", 20);
        requestFields.put("sendPwd", 16);
    }
    
    public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
    	responseFields.put("sendDtm", 10);
    	responseFields.put("bizInfo", 3);
    	responseFields.put("sendName", 20);
    	responseFields.put("sendPwd", 16);
    }
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    
    private String sendDtm;
    private String bizInfo;
    private String sendName;
    private String sendPwd;
	public String getSendDtm() {
		return sendDtm;
	}
	public void setSendDtm(String sendDtm) {
		this.sendDtm = sendDtm;
	}
	public String getBizInfo() {
		return bizInfo;
	}
	public void setBizInfo(String bizInfo) {
		this.bizInfo = bizInfo;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getSendPwd() {
		return sendPwd;
	}
	public void setSendPwd(String sendPwd) {
		this.sendPwd = sendPwd;
	}

}
