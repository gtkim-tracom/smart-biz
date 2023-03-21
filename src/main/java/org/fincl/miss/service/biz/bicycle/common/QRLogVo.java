package org.fincl.miss.service.biz.bicycle.common;

import java.util.Map;

import org.apache.ibatis.type.Alias;

public class QRLogVo {

	private String QR_LOG_SEQ;
    private String bicycleState;
    private String bicycleNo;
    private String bicycleId;
    private String Lock;
    private String timeStamp;
    private String userSeq;
    private String userType;
    
    private String biketype;
    private String beaconid;
    private String dev_BATT;
    private String beacon_BATT;
    private String bike_BATT;
    
    private String firm_fw;
    private String modem_fw;
    
    private String xpos;
    private String ypos;
    
    private String Message;
    private String qr_frame;
    
    private String resAck;
    
	
    
	



	public String getQR_LOG_SEQ() {
		return QR_LOG_SEQ;
	}



	public void setQR_LOG_SEQ(String qR_LOG_SEQ) {
		QR_LOG_SEQ = qR_LOG_SEQ;
	}



	public String getBicycleNo() {
		return bicycleNo;
	}



	public void setBicycleNo(String bicycleNo) {
		this.bicycleNo = bicycleNo;
	}



	public String getBicycleState() {
		return bicycleState;
	}



	public void setBicycleState(String bicycleState) {
		this.bicycleState = bicycleState;
	}

	


	public String getUserType() {
		return userType;
	}



	public void setUserType(String userType) {
		this.userType = userType;
	}



	public String getBicycleId() {
		return bicycleId;
	}



	public void setBicycleId(String bicycleId) {
		this.bicycleId = bicycleId;
	}


	

	public String getLock() {
		return Lock;
	}



	public void setLock(String lock) {
		Lock = lock;
	}



	public String getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}



	public String getUserSeq() {
		return userSeq;
	}



	public void setUserSeq(String userSeq) {
		this.userSeq = userSeq;
	}



	public String getBiketype() {
		return biketype;
	}



	public void setBiketype(String biketype) {
		this.biketype = biketype;
	}



	public String getBeaconid() {
		return beaconid;
	}



	public void setBeaconid(String beaconid) {
		this.beaconid = beaconid;
	}



	public String getDev_BATT() {
		return dev_BATT;
	}



	public void setDev_BATT(String dev_BATT) {
		this.dev_BATT = dev_BATT;
	}



	public String getBeacon_BATT() {
		return beacon_BATT;
	}



	public void setBeacon_BATT(String beacon_BATT) {
		this.beacon_BATT = beacon_BATT;
	}



	public String getBike_BATT() {
		return bike_BATT;
	}



	public void setBike_BATT(String bike_BATT) {
		this.bike_BATT = bike_BATT;
	}



	public String getFirm_fw() {
		return firm_fw;
	}



	public void setFirm_fw(String firm_fw) {
		this.firm_fw = firm_fw;
	}



	public String getModem_fw() {
		return modem_fw;
	}



	public void setModem_fw(String modem_fw) {
		this.modem_fw = modem_fw;
	}



	public String getXpos() {
		return xpos;
	}



	public void setXpos(String xpos) {
		this.xpos = xpos;
	}



	public String getYpos() {
		return ypos;
	}



	public void setYpos(String ypos) {
		this.ypos = ypos;
	}



	public String getMessage() {
		return Message;
	}



	public void setMessage(String message) {
		Message = message;
	}



	public String getQr_frame() {
		return qr_frame;
	}



	public void setQr_frame(String qr_frame) {
		this.qr_frame = qr_frame;
	}

	public String getResAck() {
		return resAck;
	}

	public void setResAck(String resAck)
	{
		this.resAck = resAck;
		
	}
	
    
}
