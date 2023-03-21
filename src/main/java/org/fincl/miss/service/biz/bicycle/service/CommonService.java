package org.fincl.miss.service.biz.bicycle.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BicycleStopChkRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.TheftReportRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;

public interface CommonService {

	public Map<String, Object> checkBicycle(CommonVo com);
	
	//강제 반납 과련 추가 20190523
	public String checkdBikeStateInfo(CommonVo com);
		
	public Integer checkForcedReturnInfo(CommonVo com);
		
	public void updateForcedReturnState(int enfrc_return_hist_seq);

	public Map<String, Object> checkParkingInfo(CommonVo com);

	public int deleteFaultInfo(CommonVo com);

	public int updatePeriodState(CommonVo com);

	public int updateblebike(CommonVo com);
	
	public Map<String, Object> checkMount(CommonVo com);
	
	public HashMap<String, Object> CheckStation_ForGPS(Map<String, String> GPS_DATA);
	
	public void InsertBikeGPS_Status(Map<String, String> GPS_DATA);
	
	public Map<String, Object> CheckBeaconID(Map<String, String> Info);
	public Map<String, Object> CheckBeaconID_Shoot(Map<String, String> Info);
	
	public Map<String, String> CheckBeacon_Station(Map<String, String> Info);
	
	public int CheckBeacon_RACK(Map<String, String> Info);
	
	public String CheckQRBIKE_Info(Map<String, String> Info);
	
	public Map<String, String> CheckValidELEC_GPS();
	
	public void InsertQR_RACK_0(Map<String, String> STATION_INFO);
	public void InsertQR_RACK_99(Map<String, String> STATION_INFO);
	
	public OverFeeVO getOverFeeRETURN(String USR_SEQ);
	
	int setPaymentBillingKey(OverFeeVO fee);
	
	int setOverFeePayComplete(OverFeeVO fee);

	int addTicketPayment(OverFeeVO fee);
	
	java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee);
	
	String getLockOffPay(Map<String, String> LockOff);
	
	int setOverFeePayReset(OverFeeVO fee);

	public int checkBreakDown(CommonVo com);

	public Map<String, Object> reservationCheck(CommonVo com);

	public Map<String, Object> passWordCheck(CommonVo com);

	public Map<String, Object> registCard(CommonVo com);

	public Map<String, Object> getUserInfo(CommonVo com);

	public Map<String, Object> getAdminInfo(CommonVo com);

	public boolean isLastCascade(CommonVo com);

	public Map<String, Object> checkCardNum(CommonVo com);

	public void updateBatteryDischarge(CommonVo com);

	public boolean isBlackList(CommonVo com);

	public boolean isUnpaidList(CommonVo com);

	public void tempReservation(CommonVo com);

	public Map<String, Object> getComCd(CommonVo com);

	public void updateCheckBike(CommonVo com);

	public void updateBrokenBike(CommonVo com);

	public String getResMessage(CommonVo com);

	public Map<String, Object> tempReservationCheck(CommonVo com);
	
	public boolean chkExistCard(CommonVo com);

	public void theftReport(TheftReportRequestVo vo);

	public boolean adminPassWordCheck(CommonVo com);
	
	public boolean chkDelayTime(CommonVo com);
	
	public boolean chkUseStation(CommonVo com);
	
	public boolean chkUseStationByRockId(String rockId);
	
	public boolean chkUseTime(CommonVo com);
	
	public boolean hasNetFault(CommonVo com);
	
	public Map<String, Object> checkParkingRack(CommonVo com);
	
	public int checkParkingCount(CommonVo com);
	
	public boolean checkAdminPwd(CommonVo com);
	
	/*public boolean checkUserPwd(CommonVo com);	비회원 일일권 오류수정(cardInfo.get("USR_SEQ")) == NULL POINT EXCEPTION)_20160704_JJH_START*/ 
	
	public Map<String, Object> checkUserPwd(CommonVo com);	// 수정완료_20160704_JJH_END
	
	public void updateBatteryInfo(Map<String, String> pMap);	// 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE_20160808_JJH
	
	
	
	public void updateElecBatteryInfo(Map<String, String> pMap);	// 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE_20160808_JJH
	
	public Integer selectBatteryInfo(Map<String, String> pMap);
	
	public Integer selectBatteryDetl(int FAULT_SEQ);
	
	public void updateBeaconBatteryInfo(Map<String, String> pMap);
	public void insertBrokenInvalidLocation(CommonVo com);

	public String getFaultSeq(CommonVo com);

	public void insertBrokenBikeErr(CommonVo com);
	public void insertBrokenBikeErr_H(CommonVo com);

	public void insertBrokenBikeDetailErr(CommonVo com);

	public int isFaultDtl(CommonVo com);
	
	public boolean isInvalidLocationDtl(CommonVo com);

	public int isBrokenReport(CommonVo com);

	public void insertBrokenBikeReport(CommonVo com);
	
	public void changeBikeBreakDowon(CommonVo com);
	
	public void changeValidBike(CommonVo com);
	
	public boolean chkExistAdminCard(CommonVo com);
	
	public void insertBrokenLocker(CommonVo com);
	
	public void insertBrokenThift(CommonVo com);
	
	public void insertBrokenLowBattery(CommonVo com);
	
	public boolean isBrokenLocker(CommonVo com);
	
	public boolean isBrokenThift(CommonVo com);
	
	public boolean isBrokenLowBattery(CommonVo com);

	public boolean chkRentDoubleBooking(CommonVo com);

	public void updateRegistCard(CommonVo com);
	
	public Map<String, Object> getBaseTime(RentHistVo rentHistVo);	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH
	
	public void insertinsertPeriodInfo(CommonVo com, PeriodicStateReportsRequestVo vo);
	
	public boolean isBlackListByUserSeq(CommonVo com);
	
	public boolean isUnpaidListByUserSeq(CommonVo com);
	
	public String getRockId(AdminMoveRequestVo vo);
	
	public String getDayAndNightFlag();	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
	
	public String getPeriodSetInfo();	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
	
	public boolean bicycleStopChkProc(BicycleStopChkRequestVo vo);	// 정차 자전거 자동확인 Proc_20170731_JJH

	public List<HashMap<String, String>> CheckPeriodTime();

}
