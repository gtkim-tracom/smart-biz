package org.fincl.miss.service.biz.bicycle.service.impl;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.job.bikeRob.BikeRobAlarmMapper;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.GoogleAddressAPI;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentMapper;
import org.fincl.miss.service.biz.bicycle.vo.BicycleStopChkRequestVo;
import org.fincl.miss.service.biz.bicycle.service.CommonMapper;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.TheftReportRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;

import com.ibatis.common.logging.Log;

/**
 * @author civan
 *
 */
@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	//@Qualifier("bizDataSource")
	private CommonMapper comm;
	
	@Autowired
	//@Qualifier("bizDataSource")
	 private BicycleRentMapper bicycleMapper;
	
	@Autowired
	private BikeRobAlarmMapper bikeRobAlarmMapper;

	@Override
	public Map<String, Object> checkBicycle(CommonVo com) {
		return comm.checkBicycle(com);
	}
	
	@Override
	public List<HashMap<String, String>> CheckPeriodTime() {
		return comm.CheckPeriodTime();
	}
	
	@Override
	public String checkdBikeStateInfo(CommonVo com)
	{
		return comm.checkdBikeStateInfo(com);
	}
	@Override
	public Integer checkForcedReturnInfo(CommonVo com)
	{
		return comm.checkForcedReturnInfo(com);
	}
	@Override
	public void updateForcedReturnState(int enfrc_return_hist_seq)
	{
		comm.updateForcedReturnState(enfrc_return_hist_seq);
	}
	@Override
	public Map<String, Object> checkParkingInfo(CommonVo com) {
		return comm.checkParkingInfo(com);
	}
	
	@Override
	public int setPaymentBillingKey(OverFeeVO fee)
	{
		return comm.setPaymentBillingKey(fee);
	}

	@Override
	public int deleteFaultInfo(CommonVo com) {
		return comm.deleteFaultInfo(com);
	}

	@Override
	public int updatePeriodState(CommonVo com) {
		return comm.updatePeriodState(com);
	}
	
	@Override
	public int updateblebike(CommonVo com) {
		return comm.updateblebike(com);
	}
	
	@Override
	public void updateBatteryDischarge(CommonVo com) {
		comm.updateBatteryDischarge(com);
	}

	@Override
	public Map<String, Object> checkMount(CommonVo com) {
		return comm.checkMount(com);
	}

	@Override
	public int checkBreakDown(CommonVo com) {
		return comm.checkBreakDown(com);
	}

	@Override
	public Map<String, Object> reservationCheck(CommonVo com) {
		return comm.reservationCheck(com);
	}

	@Override
	public Map<String, Object> passWordCheck(CommonVo com) {
		return comm.passWordCheck(com);
	}
	
	@Override
	public boolean adminPassWordCheck(CommonVo com) {
		//return comm.passWordCheck(com);
		boolean data = true;
		return data;
	}

	@Override
	public Map<String, Object> registCard(CommonVo com) {
		
		
		// 카드 정보 업데이트
		comm.updateCard(com);
		// 카드 등록
		comm.registCard(com);
		
		// 대여 테이블 카드 등록여부 N 변경
		comm.updateRegistCard(com);
		
		Map<String, Object> userLang = comm.getUserInfo(com);
			
		return userLang;
	}

	@Override
	public Map<String, Object> getUserInfo(CommonVo com) {
		return comm.getUserInfo(com);
	}

	@Override
	public Map<String, Object> getAdminInfo(CommonVo com) {
		return comm.getAdminInfo(com);
	}

	@Override
	public boolean isLastCascade(CommonVo com) {
		int cnt = comm.isLastCascade(com);
		if(cnt > 0){
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> checkCardNum(CommonVo com) {
		
		
		Map<String, Object> info = null;
		
		info = comm.getUserInfo(com);
		if(info == null){
			info = comm.getAdminInfo(com);
			if(info == null){
				return info;
			}else{
				info.put("USER", "ADMIN");
			}
		}else{
			info.put("USER", "USER");
		}
		
		return info;
		
		
	}

	/**
	 * 블랙리스트 여부 확인.
	 * 블랙리스트 목록에 0 이면. 정상..
	 * 0 이상이면 블랙리스트.
	 */
	@Override
	public boolean isBlackList(CommonVo com) {
		int cnt = comm.isBlackList(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}

	/**
	 * 초과여부 확인.
	 * 0이상이면 초과요금 존재...
	 */
	@Override
	public boolean isUnpaidList(CommonVo com) {
		int cnt = comm.isUnpaidList(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}

	@Override
	public void tempReservation(CommonVo com) {
		comm.tempReservation(com);
	}

	@Override
	public Map<String, Object> getComCd(CommonVo com) {
		return comm.getComCd(com);
	}

	@Override
	public void updateCheckBike(CommonVo com) {
		
		// 자전거 점검완료 상태코드 , 점검일자 UPDATE
		comm.updateBikeCheck(com);
		
		// DEVICE 상태 UPDATE
		comm.updateDeviceCheck(com);
		
		// 고장 내역 삭제
	//	comm.deleteFaultInfoDetail(com);
	//	comm.deleteFaultInfo(com);
		
		
	}

	@Override
	public void updateBrokenBike(CommonVo com) {
		
		com.setBikeId(comm.getBikeId(com));
		
		// 자전거 고장상태 update , 점검일자 UPDATE
		comm.updateBikeCheck(com);
		String faultSeq = comm.getFaultSeq(com);
		
		/**
		 * 고장신고시 동일고장신고에 대하여 등록하지 않으며,
		 * 동일 자전거에 대하여 중복신고 된 경우
		 * 신고 상세 및 수리부품 정보가 상이한 경우에만 추가.
		 * 
		 */
		if(faultSeq == null || faultSeq.equals("")){
			/**
			 * 장애신고가 없는 경우.
			 */
			// 자전거 고장신고
			comm.insertBrokenBikeErr(com);	//TB_MTC_FAULT_INFO 
			faultSeq = comm.getFaultSeq(com);
			if(com.getUserSeq() == null || com.getUserSeq().equals("")){
				 faultSeq = comm.getFaultSeq(com);
				 com.setUserSeq(faultSeq);
			}
			comm.insertBrokenBikeDetailErr(com);	//TB_MTC_FAULT_DETL  , ERB_002(신고) , ERB_005 (도난)
			comm.insertBrokenBikeReport(com);		//TB_MTC_REPORT_DETL , ELB_05 (단말기) ,도난 
		}else{
			/**
			 * 장애신고가 있는 경우
			 * faultSeq 조회.
			 */
			com.setUserSeq(faultSeq);
			/**
			 * 고장상세가 등록되어 있으면 SKIP
			 */
			if(comm.isFaultDtl(com) == 0){
				comm.insertBrokenBikeDetailErr(com);
			}
			/**
			 * 동일 수리부품내역이 존재하면 SKIP
			 */
			if(comm.isBrokenReport(com) == 0 ){
				comm.insertBrokenBikeReport(com);
			}
		}
		
	}
	
	public void insertBrokenInvalidLocation(CommonVo com){
	//	comm.insertBrokenBikeDetailErr(com);
		comm.insertBrokenInvalidLocation(com);	//2018.10.11
		//
	}

	@Override
	public String getResMessage(CommonVo com) {
		return comm.getResMessage(com);
	}

	@Override
	public Map<String, Object> tempReservationCheck(CommonVo com) {
		return comm.tempReservationCheck(com);
	}
	
	@Override
	public boolean chkExistCard(CommonVo com){
		int cnt = comm.chkExistCard(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}

	/**
	 * 도난 보고
	 */
	@Override
	public void theftReport(TheftReportRequestVo vo) {
		comm.theftReport(vo);
		
	}

	/**
	 * 운휴기간 여부 확인.운휴기간이 아니면 true
	 */
	@Override
	public boolean chkDelayTime(CommonVo com) {
		int cnt = comm.chkDelayTime(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}

	/**
	 * 대여소 운영여부. 운영중이면 true
	 */
	@Override
	public boolean chkUseStation(CommonVo com) {
		int cnt = comm.chkUseStation(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}
	
	
	public boolean chkUseStationByRockId(String rockId){
		int cnt = comm.chkUseStationByRockId(rockId);
		if(cnt > 0){
			return true;
		}
		return false;
	}

	/**
	 * 운영시간 내 대여여부, 운영시간 이내면 true,
	 *  이전 또는 초과시 false
	 *   
	 */
	@Override
	public boolean chkUseTime(CommonVo com) {
		int cnt = comm.chkUseTime(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasNetFault(CommonVo com){
		boolean result = false;
		
		String faultSeq = comm.getFaultSeq(com);
		if(faultSeq == null){
			return false;
		}
		int cnt =	comm.hasNetFault(com);
		if(cnt >0){
			return true;
		}else{
			return false;
		}
	}
	
	public Map<String, Object> checkParkingRack(CommonVo com){
		return comm.checkParkingRack(com);
	}
	
	public int checkParkingCount(CommonVo com){
		return comm.checkParkingCount(com);
	}

	@Override
	public boolean checkAdminPwd(CommonVo com) {
		if(comm.checkAdminPwd(com)>0){
			return true;
		}else{
			return false;
		}
	}

	/* 비회원 일일권 오류수정(cardInfo.get("USR_SEQ")) == NULL POINT EXCEPTION)_20160704_JJH_START
	@Override
	public boolean checkUserPwd(CommonVo com) {
		if(comm.checkUserPwd(com)>0){
			return true;
		}else{
			return false;
		}
	}
	*/
	
	@Override
	public Map<String, Object> checkUserPwd(CommonVo com) {
		Map<String, Object> returnMap = comm.checkUserPwd(com);
		
		return returnMap;
	}
	// 비회원 일일권 오류수정(cardInfo.get("USR_SEQ")) == NULL POINT EXCEPTION)_20160704_JJH_END
	

	@Override
	public String getFaultSeq(CommonVo com) {
		return comm.getFaultSeq(com);
	}

	@Override
	public void insertBrokenBikeErr(CommonVo com) {
		comm.insertBrokenBikeErr(com);
		
	}
	
	@Override
	public void insertBrokenBikeErr_H(CommonVo com) {
		comm.insertBrokenBikeErr_H(com);
		
	}

	@Override
	public void insertBrokenBikeDetailErr(CommonVo com) {
		comm.insertBrokenBikeDetailErr(com);
	}

	@Override
	public int isFaultDtl(CommonVo com) {
		return comm.isFaultDtl(com);
	}

	@Override
	public boolean isInvalidLocationDtl(CommonVo com) {
		if(comm.isInvalidLocationDtl(com)>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int isBrokenReport(CommonVo com) {
		return comm.isBrokenReport(com);
	}

	@Override
	public void insertBrokenBikeReport(CommonVo com) {
		comm.insertBrokenBikeReport(com);
		
	}

	@Override
	public void changeBikeBreakDowon(CommonVo com) {
		comm.changeBikeBreakDowon(com);
		
	}

	@Override
	public void changeValidBike(CommonVo com) {
		comm.changeValidBike(com);
		
	}

	@Override
	public boolean chkExistAdminCard(CommonVo com) {
		if(comm.chkExistAdminCard(com)>0){
			return true;
		}else{
			return false;
		}
	}

	public void insertBrokenLocker(CommonVo com){
		comm.insertBrokenLocker(com);
	}
	
	public void insertBrokenThift(CommonVo com){
		comm.insertBrokenThift(com);
	}
	
	public void insertBrokenLowBattery(CommonVo com){
		comm.insertBrokenLowBattery(com);
	}
	
	public boolean isBrokenLocker(CommonVo com){
		if(comm.isBrokenLocker(com)>0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isBrokenThift(CommonVo com){
		if(comm.isBrokenThift(com)>0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isBrokenLowBattery(CommonVo com){
		if(comm.isBrokenLowBattery(com)>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean chkRentDoubleBooking(CommonVo com) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		int rentCnt = comm.chkRentDoubleBooking(com);
		
 		if(rentCnt > 1) {
 			//대여테이블에서 삭제.
 			comm.delRentDoubleBooking(com);
 		}
 		
		return rentCnt > 1 ? true : false;
	}

	@Override
	public void updateRegistCard(CommonVo com) {
		// TODO Auto-generated method stub
		
	}
	
	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH_START
	@Override
	public Map<String, Object> getBaseTime(RentHistVo rentHistVo){
		return comm.getBaseTime(rentHistVo);
		
	}
	
	/**
	 * 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE_20160808_JJH
	 * 대여대기를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
	 * 대여를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
	 * 관리자 이동을 통한 자전거 배터리 정보 UPDATE_20170208_JJH
	 * 관리자 설치를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
	 */
	@Override
	public void updateBatteryInfo(Map<String, String> pMap){
		comm.updateBatteryInfo(pMap);
		
	}
	
	@Override
	public void updateElecBatteryInfo(Map<String, String> pMap){
		comm.updateElecBatteryInfo(pMap);
		
	}
	
	@Override
	public Integer selectBatteryInfo(Map<String, String> pMap)
	{
		return comm.selectBatteryInfo(pMap);
	}
	
	@Override
	public Integer selectBatteryDetl(int FAULT_SEQ)
	{
		return comm.selectBatteryDetl(FAULT_SEQ);
	}
	
	@Override
	public void updateBeaconBatteryInfo(Map<String, String> pMap){
		comm.updateBeaconBatteryInfo(pMap);
		
	}
	
	@Override
	public void insertinsertPeriodInfo(CommonVo com, PeriodicStateReportsRequestVo vo){
		
		System.out.println("######################## 주기적인 상태보고 상태시간 History Start ########################");
		RentalRequestVo info = new RentalRequestVo();
		
		info.setBicycleId(com.getBicycleId());
		info.setMountsId(com.getRockId());
		info.setBicycleState("PRO_003");
		//info.setReturnForm(vo.getReturnForm());
		
		String sMaxTime = "070000";
		String sMinTime = "220000";
		
		String sCurTime = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.KOREA).format(new java.util.Date());
		String sCurDate = new SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA).format(new java.util.Date());
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		Calendar strCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		
		try {
			date = dateFormat.parse(sCurDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sCurTime.compareTo(String.valueOf(sCurDate + "000000")) >= 0 && sCurTime.compareTo(String.valueOf(sCurDate + "070000")) < 0){
			strCal.setTime(date);
			strCal.add(Calendar.DATE, -1);
			
			sMaxTime = sCurDate + sMaxTime;
			sMinTime = dateFormat.format(strCal.getTime()) + sMinTime;
			
			System.out.println("######################## if 시작시간, 종료시간 ######################## => " + String.valueOf(sMinTime) +  ", " + String.valueOf(sMaxTime));
		}else{
			endCal.setTime(date);
			endCal.add(Calendar.DATE, 1);
			
			sMaxTime = dateFormat.format(endCal.getTime()) + sMaxTime;
			sMinTime = sCurDate + sMinTime;
			System.out.println("######################## else 시작시간, 종료시간 ######################## => " + String.valueOf(sMinTime) +  ", " + String.valueOf(sMaxTime));
		}
		
		System.out.println("######################## 주기적인 상태보고 실시간 확인 ==> " + String.valueOf(sCurTime));
		
		if(sCurTime.compareTo(sMinTime) >= 0 && sCurTime.compareTo(sMaxTime) < 0){
			System.out.println("######################## 주기적인 상태보고 시간이 22:00 ~ 07:00 사이임 ==> " + String.valueOf(sCurTime));
			bicycleMapper.insertPeriodInfo(info);
		}else{
			System.out.println("######################## 주기적인 상태보고 시간이 조건 밖임~!! ==> ");
		}
        
		System.out.println("######################## 주기적인 상태보고 상태시간 History End ########################");
		
	}
	
	/**
	 * 블랙리스트 여부 확인.
	 * 블랙리스트 목록에 0 이면. 정상..
	 * 0 이상이면 블랙리스트.
	 */
	@Override
	public boolean isBlackListByUserSeq(CommonVo com){
		int cnt = comm.isBlackListByUserSeq(com);
		
		if(cnt > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 초과여부 확인.
	 * 0이상이면 초과요금 존재...
	 */
	@Override
	public boolean isUnpaidListByUserSeq(CommonVo com){
		int cnt = comm.isUnpaidListByUserSeq(com);
		if(cnt > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public String getRockId(AdminMoveRequestVo vo){
		return comm.getRockId(vo);
	}
	
	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
	public String getDayAndNightFlag(){
		return comm.getDayAndNightFlag();
	}
	
	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
	public String getPeriodSetInfo(){
		return comm.getPeriodSetInfo();
	}
	
	public HashMap<String, Object> CheckStation_ForGPS(Map<String, String> GPS_DATA){
		return comm.CheckStation_ForGPS(GPS_DATA);
	}
	
	public void InsertBikeGPS_Status(Map<String, String> GPS_DATA)
	{
		comm.InsertBikeGPS_Status(GPS_DATA);
	}
	
	
	public Map<String, Object> CheckBeaconID(Map<String, String> Info){
		return comm.CheckBeaconID(Info);
	}
	
	public Map<String, Object> CheckBeaconID_Shoot(Map<String, String> Info){
		return comm.CheckBeaconID_Shoot(Info);
	}
	
	public Map<String, String> CheckBeacon_Station(Map<String, String> Info){
		return comm.CheckBeacon_Station(Info);
	}
	
	public int CheckBeacon_RACK(Map<String, String> Info){
		return comm.CheckBeacon_RACK(Info);
	}
	
	public String CheckQRBIKE_Info(Map<String, String> Info){
		return comm.CheckQRBIKE_Info(Info);
	}
	
	public void InsertQR_RACK_0(Map<String, String> STATION_INFO)
	{
		comm.InsertQR_RACK_0(STATION_INFO);
	}

	public void InsertQR_RACK_99(Map<String, String> STATION_INFO)
	{
		comm.InsertQR_RACK_99(STATION_INFO);
	}
	
	public OverFeeVO getOverFeeRETURN(String USR_SEQ)
	{
		return comm.getOverFeeRETURN(USR_SEQ);
	}
	
	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int setOverFeePayComplete(OverFeeVO fee) {
		return comm.setOverFeePayComplete(fee);
	}

	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int addTicketPayment(OverFeeVO fee) {
		// TODO Auto-generated method stub
		return comm.addTicketPayment(fee);
	}
	
	public java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee){
		return comm.getPaymentInfoExist(fee);
	}
	
	public String getLockOffPay(Map<String, String> LockOff)
	{
		return comm.getLockOffPay(LockOff);
	}
	
	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int setOverFeePayReset(OverFeeVO fee) {
		return comm.setOverFeePayReset(fee);
	}
	
	
	public Map<String, String> CheckValidELEC_GPS()
	{
		return comm.CheckValidELEC_GPS();
	}
	/**
	 * 정차 자전거 자동확인 Proc_20170731_JJH
	 */
	public boolean bicycleStopChkProc(BicycleStopChkRequestVo vo){
		
		double latitude, longitude = 0.0d;
		String address = "";
		String bicycleId = "";
		bicycleId = vo.getBicycleId();
		
		boolean procFlag = false;
		
		SmsMessageVO smsVo = null;
		
		String [] smsMap = bikeRobAlarmMapper.getBikeRobSmsTarget().split(",");	// 도난경보 시 전송해야 할 관리자 정보와 같은 관리자에게 전송할 것인가?
		
		Map<String, String> bikeMap = new HashMap<String, String>();
		bikeMap = comm.getBikeInfo(bicycleId);
		
		List<SmsMessageVO> smsTarget = new ArrayList<SmsMessageVO>();
		List<TAPPMessageVO> TAPPTarget = new ArrayList<TAPPMessageVO>();
		
		if((vo.getLatitude() != null && vo.getLatitude() != "" && !vo.getLatitude().equals("FFFFFFFF")) && (vo.getLongitude() != null && vo.getLongitude() != "" && !vo.getLongitude().equals("FFFFFFFF"))){
			if(bikeMap != null){
				
				/**
				 * 정차 자전거 GPS 정보 위치정보 변환_20171010_JJH
				 */
				double latDouble = (Integer.parseInt(vo.getLatitude(), 16))*0.000001;
				double latip = latDouble - (latDouble - (int)latDouble) ;
			    double latfp = (latDouble - (int)latDouble)*100/60;
			    
			    //소숫점 6자리까지 계산.(버림)
			    latitude = Math.floor((latip+latfp)*1000000d)/1000000d;
			    
				double lonDouble = (Integer.parseInt(vo.getLongitude(), 16))*0.000001;
				double lonip = lonDouble - (lonDouble - (int)lonDouble) ;
			    double lonfp = (lonDouble - (int)lonDouble)*100/60;
			    
			    //소숫점 6자리까지 계산.(버림)
			    longitude= Math.floor((lonip+lonfp)*1000000d)/1000000d;

				/*
				latitude = Double.parseDouble(vo.getLatitude());
				longitude = Double.parseDouble(vo.getLongitude());
				*/
			    
			    System.out.println("##### bicycleStopChkProc  GPS INFO ##### => BIKE ID : " + bicycleId.toString() + ", Lat : " + String.valueOf(latitude) + ", Lon : " + String.valueOf(longitude));
			    
				
		//		SmsSendToAdmin(vo, latitude, longitude, smsMap, bikeMap, smsTarget);	// 관리자 SMS 전송
				
			    smsSendToUser(vo, bikeMap, smsTarget,TAPPTarget);	// 사용자 SMS 전송
				
				SmsSender.sender(smsTarget);
				
				for(TAPPMessageVO msg:TAPPTarget)
				{
					SmsSender.TAPPsender(msg); 
				}
				
				procFlag = true;
				
			}else{
				System.out.println("##### bicycleStopChkProc  정차 자전거 대여정보 없음~!! ##### => " + vo.getBicycleId());
			}
			
		}else{
			if(bikeMap != null)
			{
				System.out.println("##### bicycleStopChkProc GPS is NULL !! ##### => " + vo.getBicycleId());
				
				latitude = Double.parseDouble("00.000000");
				longitude = Double.parseDouble("000.000000");
				
				smsSendToUser(vo, bikeMap, smsTarget,TAPPTarget);	// 사용자 SMS 전송
				
				SmsSender.sender(smsTarget);
				
				for(TAPPMessageVO msg:TAPPTarget)
				{
					SmsSender.TAPPsender(msg); 
				}
				
				procFlag = true;
			}

		}
		
		return procFlag;
		
	}

	/**
	 * 관리자 SMS 전송로직 시작_20170731_JJH
	 * @param vo
	 * @param latitude
	 * @param longitude
	 * @param smsMap
	 * @param bikeMap
	 * @param smsTarget
	 */
	private void SmsSendToAdmin(BicycleStopChkRequestVo vo, double latitude, double longitude, String[] smsMap, Map<String, String> bikeMap, List<SmsMessageVO> smsTarget) {
		String address;
		SmsMessageVO smsVo;
		try {
			if(latitude != 0.0 && longitude != 0.0){
				GoogleAddressAPI gps = new GoogleAddressAPI(latitude, longitude);
				
				address = gps.getAddress();
				
				if(smsMap.length > 0){
					for(int i = 0; i < smsMap.length; i++){
						smsVo = new SmsMessageVO();
						smsVo.setDestno(smsMap[i]);
						smsVo.setMsg(SendType.SMS_024, String.valueOf(bikeMap.get("BIKE_NO")), address);
						
						smsTarget.add(smsVo);
					}
				}
				
				System.out.println("##### 정차 자전거 주소정보 변환완료~!! ##### => " + vo.getBicycleId());
			}else{
				System.out.println("##### 정차 자전거 위/경도 정보 없음~!! ##### => " + vo.getBicycleId());
				
				if(smsMap.length > 0){
					for(int i = 0; i < smsMap.length; i++){
						smsVo = new SmsMessageVO();
						smsVo.setDestno(smsMap[i]);
						smsVo.setMsg(SendType.SMS_025, String.valueOf(bikeMap.get("BIKE_NO")), String.valueOf(latitude), String.valueOf(longitude));
						
						System.out.println("aaaa" + bikeMap.toString());
						
						smsTarget.add(smsVo);
					}
				}
			}
		} catch (Exception e) {
			if(smsMap.length > 0){
				for(int i = 0; i < smsMap.length; i++){
					smsVo = new SmsMessageVO();
					smsVo.setDestno(smsMap[i]);
					smsVo.setMsg(SendType.SMS_025, String.valueOf(bikeMap.get("BIKE_NO")), String.valueOf(latitude), String.valueOf(longitude));
					
					smsTarget.add(smsVo);
				}
			}
			
			e.printStackTrace();
			System.out.println("##### 정차 자전거 주소정보 변환안됨~!! ##### => " + vo.getBicycleId());
		}
	}

	/**
	 * 사용자 SMS 전송로직 시작_20170731_JJH
	 * @param vo
	 * @param bikeMap
	 * @param smsTarget
	 */
	private void smsSendToUser(BicycleStopChkRequestVo vo,
			Map<String, String> bikeMap, List<SmsMessageVO> smsTarget, List<TAPPMessageVO> TAPPTarget)
	{
		SmsMessageVO smsVo;
		TAPPMessageVO TAPPVo;
		
		 if(bikeMap != null)
		 {	 	
				//		if(bikeMap.get("USR_MPN_NO") != null || bikeMap.get("BIKE_NO") != "")
			 if(bikeMap.get("USR_MPN_NO") == null || bikeMap.get("USR_MPN_NO").toString().equals("") 
		               || bikeMap.get("BIKE_NO") == null || bikeMap.get("BIKE_NO").toString().equals("")
		               || bikeMap.get("RENT_DTTM") == null)
			 {
				 System.out.println("##### smsSendToUser 정차 자전거 사용자 휴대폰정보 없음  NO_PHONENUMBER ##### => " + vo.getBicycleId());
				 
				
			 }
			 else
			 {
				 if(bikeMap.get("RENT_MTH_CD") == null || bikeMap.get("RENT_MTH_CD").equals("") || bikeMap.get("RENT_MTH_CD").equals("CHN_001"))
				 {
					 smsVo = new SmsMessageVO();
					 smsVo.setDestno(bikeMap.get("USR_MPN_NO"));
					 smsVo.setMsg(SendType.SMS_026,String.valueOf(bikeMap.get("RENT_DTTM")), String.valueOf(bikeMap.get("BIKE_NO")));
					 
					 smsTarget.add(smsVo);
					
					 System.out.println("##### smsSendToUser 정차 자전거 사용자 SMS 전송준비 완료  READY_SUCCESS ##### => " + vo.getBicycleId());
				 }
				 else if(bikeMap.get("RENT_MTH_CD").equals("CHN_002"))
				 {
					 TAPPVo = new TAPPMessageVO();
					 TAPPVo.setUsr_seq(bikeMap.get("USR_SEQ"));
					 TAPPVo.setBike_no(bikeMap.get("BIKE_NO"));
					 TAPPVo.setNotice_se(SendType.SMS_026.getCode());
					 TAPPVo.setMsg(SendType.SMS_026,String.valueOf(bikeMap.get("RENT_DTTM")), String.valueOf(bikeMap.get("BIKE_NO")));
					 TAPPTarget.add(TAPPVo);
				 }
			 }
		}//bikeMap
	}
}
