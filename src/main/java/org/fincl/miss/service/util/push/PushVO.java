/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push
 * @파일명          : PushVO.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util.push;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.type.Alias;

/**
 * @파일명          : PushVO.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
@Alias("pushVo")
public class PushVO {
	private String pushType;
	private String tokenId;
	private String message;
	private String messageId;
	private String encryptYn = "N";
	private String pushLinkUrl;
	private String appId;
	private String alert;
	private String bedge;
	private String sound;
	
	private List<String> tokenList;
	private List<HashMap<String, String>> errorList;
	public String getPushType() {
		return pushType;
	}
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getEncryptYn() {
		return encryptYn;
	}
	public void setEncryptYn(String encryptYn) {
		this.encryptYn = encryptYn;
	}
	public String getPushLinkUrl() {
		return pushLinkUrl;
	}
	public void setPushLinkUrl(String pushLinkUrl) {
		this.pushLinkUrl = pushLinkUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public String getBedge() {
		return bedge;
	}
	public void setBedge(String bedge) {
		this.bedge = bedge;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public List<String> getTokenList() {
		return tokenList;
	}
	public void setTokenList(List<String> tokenList) {
		this.tokenList = tokenList;
	}
	public List<HashMap<String, String>> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<HashMap<String, String>> errorList) {
		this.errorList = errorList;
	}
	
	

}
