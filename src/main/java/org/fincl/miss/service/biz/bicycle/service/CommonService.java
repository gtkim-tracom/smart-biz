package org.fincl.miss.service.biz.bicycle.service;

import java.util.Map;

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

	public Map<String, Object> checkMount(CommonVo com);

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
	
	public void updateBatteryInfo(PeriodicStateReportsRequestVo vo);	// 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE_20160808_JJH
	
	public void insertBrokenInvalidLocation(CommonVo com);

	public String getFaultSeq(CommonVo com);

	public void insertBrokenBikeErr(CommonVo com);

	public void insertBrokenBikeDetailErr(CommonVo com);

	public int isFaultDtl(CommonVo com);
	
	public boolean isInvalidLocationDtl(CommonVo com);

	public int isBrokenReport(CommonVo com);

	public void insertBrokenBikeReport(CommonVo com);
	
	public void changeBikeBreakDowon(CommonVo com);
	
	public void changeValidBike(CommonVo com);
	
	public boolean chkExistAdminCard(CommonVo com);
	
	public void insertBrokenLocker(CommonVo com);
	
	public boolean isBrokenLocker(CommonVo com);

	public boolean chkRentDoubleBooking(CommonVo com);

	public void updateRegistCard(CommonVo com);
	
	public Map<String, Object> getBaseTime(RentHistVo rentHistVo);	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH
	
	public void insertinsertPeriodInfo(CommonVo com, PeriodicStateReportsRequestVo vo);
	
	public boolean isBlackListByUserSeq(CommonVo com);
	
	public boolean isUnpaidListByUserSeq(CommonVo com);
	
	public String getRockId(AdminMoveRequestVo vo);
	
	public String getDayAndNightFlag();	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
	
	public boolean bicycleStopChkProc(BicycleStopChkRequestVo vo);	// 정차 자전거 자동확인 Proc_20170731_JJH

}
