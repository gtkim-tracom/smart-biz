package org.fincl.miss.service.biz.smart.vo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fincl.miss.server.message.parser.IncludeFieldsRepeatable;

@SuppressWarnings("serial")
public class ReceiveFileVo extends SmartVo implements IncludeFieldsRepeatable {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("blockNo", 4);
        requestFields.put("seqNo", 3);
        requestFields.put("size", 4);
        requestFields.put("data", 100);
        
    }
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    {
        repeatFields.put("data", "size"); // 반복될 필드, 반복수 참조 필드
    }
    
    public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    
    private String blockNo;
    private String seqNo;
    private String size;
    private String[] data;
    
	public String getBlockNo() {
		return blockNo;
	}
	public void setBlockNo(String blockNo) {
		this.blockNo = blockNo;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String[] getData() {
		return data;
	}
	public void setData(String[] data) {
		this.data = data;
	}
	@Override
	public Map<String, String> getRepeatFields() {
		return this.repeatFields;
	}
    
    
}
