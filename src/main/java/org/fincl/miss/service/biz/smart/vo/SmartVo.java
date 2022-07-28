package org.fincl.miss.service.biz.smart.vo;

import org.fincl.miss.server.message.MessageVO;

public class SmartVo extends MessageVO {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6266552565778169874L;
	
	private String bizGubun;
    private String orgCode;
    private String commandId;
    private String gubun;
    private String inOutFlag;
    private String fileName;
    private String resCode;
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getCommandId() {
		return commandId;
	}
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	public String getInOutFlag() {
		return inOutFlag;
	}
	public void setInOutFlag(String inOutFlag) {
		this.inOutFlag = inOutFlag;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getGubun() {
		return gubun;
	}
	public void setGubun(String gubun) {
		this.gubun = gubun;
	}
	public String getBizGubun() {
		return bizGubun;
	}
	public void setBizGubun(String bizGubun) {
		this.bizGubun = bizGubun;
	}
    
	
}
