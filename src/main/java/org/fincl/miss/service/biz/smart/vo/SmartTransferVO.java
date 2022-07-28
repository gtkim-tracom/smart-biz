package org.fincl.miss.service.biz.smart.vo;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.ibatis.type.Alias;

@Alias(value="smartTransferVO")
public class SmartTransferVO implements Serializable {
	
	static final long serialVersionUID = -5143574934344342267L;
	
	String seq;
	BigInteger usrSeq;
	String mbCardNo;
	String strDttm;
	String endDttm;
	String cardTypeCd;
	String cardUseClsCd;
	String schDate;
	String curDate;
	
	String rideDttm;
	String alightDttm;
	String transportCd;
	BigInteger transferSeq;
	
	BigInteger mbCardSeq;
	String mileageClsCd;
	int mileagePoint;
	String rentHistSeq;
	
	String searchDttm;
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public BigInteger getUsrSeq() {
		return usrSeq;
	}
	
	public void setUsrSeq(BigInteger usrSeq) {
		this.usrSeq = usrSeq;
	}
	
	public String getMbCardNo() {
		return mbCardNo;
	}
	
	public void setMbCardNo(String mbCardNo) {
		this.mbCardNo = mbCardNo;
	}
	
	public String getStrDttm() {
		return strDttm;
	}
	
	public void setStrDttm(String strDttm) {
		this.strDttm = strDttm;
	}
	
	public String getEndDttm() {
		return endDttm;
	}
	
	public void setEndDttm(String endDttm) {
		this.endDttm = endDttm;
	}
	
	public String getCardTypeCd() {
		return cardTypeCd;
	}
	
	public void setCardTypeCd(String cardTypeCd) {
		this.cardTypeCd = cardTypeCd;
	}
	
	public String getCardUseClsCd() {
		return cardUseClsCd;
	}
	
	public void setCardUseClsCd(String cardUseClsCd) {
		this.cardUseClsCd = cardUseClsCd;
	}
	
	public String getSchDate() {
		return schDate;
	}
	
	public void setSchDate(String schDate) {
		this.schDate = schDate;
	}
	
	public String getCurDate() {
		return curDate;
	}
	
	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}
	
	public String getRideDttm() {
		return rideDttm;
	}
	
	public void setRideDttm(String rideDttm) {
		this.rideDttm = rideDttm;
	}
	
	public String getAlightDttm() {
		return alightDttm;
	}
	
	public void setAlightDttm(String alightDttm) {
		this.alightDttm = alightDttm;
	}
	
	public String getTransportCd() {
		return transportCd;
	}
	
	public void setTransportCd(String transportCd) {
		this.transportCd = transportCd;
	}

	public BigInteger getMbCardSeq() {
		return mbCardSeq;
	}

	public void setMbCardSeq(BigInteger mbCardSeq) {
		this.mbCardSeq = mbCardSeq;
	}

	public String getMileageClsCd() {
		return mileageClsCd;
	}

	public void setMileageClsCd(String mileageClsCd) {
		this.mileageClsCd = mileageClsCd;
	}

	public int getMileagePoint() {
		return mileagePoint;
	}

	public void setMileagePoint(int mileagePoint) {
		this.mileagePoint = mileagePoint;
	}

	public String getRentHistSeq() {
		return rentHistSeq;
	}

	public void setRentHistSeq(String rentHistSeq) {
		this.rentHistSeq = rentHistSeq;
	}

	public String getSearchDttm() {
		return searchDttm;
	}

	public void setSearchDttm(String searchDttm) {
		this.searchDttm = searchDttm;
	}

	public BigInteger getTransferSeq() {
		return transferSeq;
	}

	public void setTransferSeq(BigInteger transferSeq) {
		this.transferSeq = transferSeq;
	}
}
