package org.fincl.miss.service.biz.bicycle.common;

import java.util.Map;

import org.apache.ibatis.type.Alias;

@Alias("CommonVo")
public class CommonVo {

	
    private String bicycleState;
    private String bicycleId;
    private String password;
    private String rockId;
    private String timeStamp;
    
    private String bikeId;
    //private String userCard;
    private String userSeq;
    private String cardNum;
    private String AdmincardNum;
    private String rent_seq;
    private String rent_cls_cd;
    private String voucher_seq;
    private String payment_cls_cd;
    private String errorId;
    private String stationId;
    
    private String comCd;
    private String bikeStusCd;
    private String bikeBrokenCd;
    private String rockStusCd;
    private String firmwareClsCd;
    private String version;
    private String firmwareSeq;
    private String firmwareFileNo;
    private String reqMsg;
    private String cardTypeCd;
    
    private String rent_return_ymd;
    private String hour_column;
    private String rent_return_hour;
    private String rent_return_flag;	// 대여면 1/반납이면 2
    private String company_cd;
    private String fileSeq;
    private String GPS_X;
    private String GPS_Y;
    
    private String serverhost;			//2018.03.28 해당 서버 host 명 
    
	public String getReqMsg() {
		return reqMsg;
	}
	public void setReqMsg(String reqMsg) {
		this.reqMsg = reqMsg;
	}
	public String getAdmincardNum() {
		return AdmincardNum;
	}
	public void setAdmincardNum(String admincardNum) {
		AdmincardNum = admincardNum;
	}
	public String getFirmwareSeq() {
		return firmwareSeq;
	}
	public void setFirmwareSeq(String firmwareSeq) {
		this.firmwareSeq = firmwareSeq;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFirmwareClsCd() {
		return firmwareClsCd;
	}
	public void setFirmwareClsCd(String firmwareClsCd) {
		this.firmwareClsCd = firmwareClsCd;
	}
	public String getRockStusCd() {
		return rockStusCd;
	}
	public void setRockStusCd(String rockStusCd) {
		this.rockStusCd = rockStusCd;
	}
	
	
	
	public String getBikeStusCd() {
		return bikeStusCd;
	}
	public void setBikeStusCd(String bikeStusCd) {
		this.bikeStusCd = bikeStusCd;
	}
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public String getComCd() {
		return comCd;
	}
	public void setComCd(String comCd) {
		this.comCd = comCd;
	}
	public String getErrorId() {
		return errorId;
	}
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	public String getPayment_cls_cd() {
		return payment_cls_cd;
	}
	public void setPayment_cls_cd(String payment_cls_cd) {
		this.payment_cls_cd = payment_cls_cd;
	}
	public String getRent_cls_cd() {
		return rent_cls_cd;
	}
	public void setRent_cls_cd(String rent_cls_cd) {
		this.rent_cls_cd = rent_cls_cd;
	}
	public String getVoucher_seq() {
		return voucher_seq;
	}
	public void setVoucher_seq(String voucher_seq) {
		this.voucher_seq = voucher_seq;
	}
	public String getRent_seq() {
		return rent_seq;
	}
	public void setRent_seq(String rent_seq) {
		this.rent_seq = rent_seq;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getUserSeq() {
		return userSeq;
	}
	public void setUserSeq(String userSeq) {
		this.userSeq = userSeq;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
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
	public String getRockId() {
		return rockId;
	}
	public void setRockId(String rockId) {
		this.rockId = rockId;
	}
	
	////timeStamp
	public String getTimeStamp(){
			return timeStamp;
	}
	public void setTimeStamp(String timeStamp ){
		this.timeStamp = timeStamp;
	}
	public String getBikeId() {
		return bikeId;
	}
	public void setBikeId(String bikeId) {
		this.bikeId = bikeId;
	}
	public String getFirmwareFileNo() {
		return firmwareFileNo;
	}
	public void setFirmwareFileNo(String firmwareFileNo) {
		this.firmwareFileNo = firmwareFileNo;
	}
	public String getBikeBrokenCd() {
		return bikeBrokenCd;
	}
	public void setBikeBrokenCd(String bikeBrokenCd) {
		this.bikeBrokenCd = bikeBrokenCd;
	}
	public String getCardTypeCd() {
		return cardTypeCd;
	}
	public void setCardTypeCd(String cardTypeCd) {
		this.cardTypeCd = cardTypeCd;
	}
	
	public String getRent_return_ymd() {
		return rent_return_ymd;
	}
	public void setRent_return_ymd(String rent_return_ymd) {
		this.rent_return_ymd = rent_return_ymd;
	}
	public String getHour_column() {
		return hour_column;
	}
	public void setHour_column(String hour_column) {
		this.hour_column = hour_column;
	}
	
	public String getRent_return_hour() {
		return rent_return_hour;
	}
	public void setRent_return_hour(String rent_return_hour) {
		this.rent_return_hour = rent_return_hour;
	}
	
	public String getRent_return_flag() {
		return rent_return_flag;
	}
	public void setRent_return_flag(String rent_return_flag) {
		this.rent_return_flag = rent_return_flag;
	}
	public String getCompany_cd() {
		return company_cd;
	}
	public void setCompany_cd(String company_cd) {
		this.company_cd = company_cd;
	}
	public String getFileSeq() {
		return fileSeq;
	}
	public void setFileSeq(String fileSeq) {
		this.fileSeq = fileSeq;
	}
	
	public void setServerHost(String serverHost) {
		this.serverhost = serverHost;
	}
	
	
	
	public String getGPS_X() {
		return GPS_X;
	}
	public void setGPS_X(String gPS_X) {
		GPS_X = gPS_X;
	}
	public String getGPS_Y() {
		return GPS_Y;
	}
	public void setGPS_Y(String gPS_Y) {
		GPS_Y = gPS_Y;
	}
	
	
	@Override
	public String toString() {
		return "CommonVo [bicycleState=" + bicycleState + ", bicycleId="
				+ bicycleId + ", password=" + password + ", rockId=" + rockId
				+ ", bikeId=" + bikeId + ", userSeq=" + userSeq + ", cardNum="
				+ cardNum + ", AdmincardNum=" + AdmincardNum + ", rent_seq="
				+ rent_seq + ", rent_cls_cd=" + rent_cls_cd + ", voucher_seq="
				+ voucher_seq + ", payment_cls_cd=" + payment_cls_cd
				+ ", errorId=" + errorId + ", stationId=" + stationId
				+ ", comCd=" + comCd + ", bikeStusCd=" + bikeStusCd
				+ ", bikeBrokenCd=" + bikeBrokenCd + ", rockStusCd="
				+ rockStusCd + ", firmwareClsCd=" + firmwareClsCd
				+ ", version=" + version + ", firmwareSeq=" + firmwareSeq
				+ ", firmwareFileNo=" + firmwareFileNo + ", reqMsg=" + reqMsg
				+ ", cardTypeCd=" + cardTypeCd + ", rent_return_ymd="
				+ rent_return_ymd + ", hour_column=" + hour_column
				+ ", rent_return_hour=" + rent_return_hour
				+ ", rent_return_flag=" + rent_return_flag + ", company_cd="
				+ company_cd + ", fileSeq=" + fileSeq + "]";
	}
    
	
    
}
