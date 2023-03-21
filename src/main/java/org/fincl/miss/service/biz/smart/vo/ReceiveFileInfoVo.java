package org.fincl.miss.service.biz.smart.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ReceiveFileInfoVo extends SmartVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("rfileName", 8);
        requestFields.put("rfileSize", 12);
        requestFields.put("byteCount", 4);
     }
    
    public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
    	responseFields.put("rfileName", 8);
    	responseFields.put("rfileSize", 12);
    	responseFields.put("byteCount", 4);
    }
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    
    private String rfileName;
    private String rfileSize;
    private String byteCount;
	public String getRfileName() {
		return rfileName;
	}
	public void setRfileName(String rfileName) {
		this.rfileName = rfileName;
	}
	
	public String getByteCount() {
		return byteCount;
	}
	public void setByteCount(String byteCount) {
		this.byteCount = byteCount;
	}
	public String getRfileSize() {
		return rfileSize;
	}
	public void setRfileSize(String rfileSize) {
		this.rfileSize = rfileSize;
	}
 
}
