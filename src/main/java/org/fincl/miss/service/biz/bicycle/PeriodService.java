package org.fincl.miss.service.biz.bicycle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentMapper;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateService;
import org.fincl.miss.service.biz.bicycle.service.impl.BicycleRentServiceImpl;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicAutoReturnRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicAutoReturnResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "주기")
@Service
public class PeriodService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonService commonService;
	
	
	@Autowired
	private BicycleRentService bikeService;

	@Autowired
	private FileUpdateService fileService;
	
	@Autowired
    private BicycleRentMapper bicycleMapper;
	
	// 주기적인 상태 보고 
	 @RPCService(serviceId = "Bicycle_11", serviceName = "주기적인 상태보고 Request", description = "주기적인 상태보고 Request")
	 public PeriodicStateReportsResponseVo adminMove(PeriodicStateReportsRequestVo vo) {
		 
		 logger.debug("######################## Bicycle_11 git_modify");
		 logger.debug("PeriodicStateReportsRequestVo vo :::::::::::{} " , vo);
		 
		 
		 PeriodicStateReportsResponseVo responseVo = new PeriodicStateReportsResponseVo();
		 
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setRockId(vo.getMountsId());
		 com.setBikeId(vo.getBicycleId());
		 
		 String faultSeq = commonService.getFaultSeq(com);
     	 /**
     	  * 주기적인 상태보고시 미등록된 자전거가 수신을 요청한 경우,
     	  * 에러 메시지 전달.
     	  * 현재 상태는 장애로 변경.
     	  */
		 Map<String, Object> deviceInfo = commonService.checkBicycle(com);
		 if(deviceInfo == null){
			 logger.error("INVALID 자전거 ID ");
			 
			 com.setBikeStusCd("BKS_001");
			 
			 com.setBikeBrokenCd("ELB_006");
			 
		//	 callBrokenError(com);
			 
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 
			 return responseVo;
		 }
			 
		 logger.debug("deviceInfo : {} ",deviceInfo);
		 
		 //2019.05.30 해당 자전거 번호 하고 거치대 번호가 같을때 체크 함.
		 if(com.getBicycleId().equals(com.getRockId()))
		 {
			 
	//		 logger.debug("Modify Rockid : {} ", ReturnRackid);
		 }
		 
		 //
		 Map<String, String> ourBikeMap = new HashMap<String, String>();
		 
		 ourBikeMap = bikeService.chkOurBike(com);	//ENTRPS_CD : ENT_001 Y  --> vick  N --> witcom
		 
		 //add 자전거 번호 가져오기 2018.09.01
		 String  bikeNo = ourBikeMap.get("BIKE_NO");
		 int	 nBikeSerial = Integer.parseInt(bikeNo.substring(4,bikeNo.length()));
		 
		 //
		 if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			 logger.debug("BIKE VITEX COMPANY NO: {} ",nBikeSerial);	//추가함...
			 
			 com.setCompany_cd("CPN_001");
		 }else{
			 logger.debug("BIKE WITCOM COMPANY NO: {} ",nBikeSerial);	//추가함...
			 com.setCompany_cd("CPN_002");
		 }

		 /*// 자전거 상태 값 이상
		 if(!vo.getBicycleState().equals("02")){
			 System.out.println("INVALID 자전거 ID 자전거 상태값 이상" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }*/
		
		 // 방전 error 검출
     	if(vo.getErrorId().equals("E4") || vo.getBattery().equals("03")){
     		// 충전상태 이상 UPdate
     		commonService.updateBatteryDischarge(com);
     	}
     	
     	/**
     	 * 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE_20160808_JJH
     	 */
     	
     	if(!vo.getBattery().equals("") && !vo.getBattery().equals(null))
     	{
     		logger.debug("##### 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE 시작 #####");
     		commonService.updateBatteryInfo(vo);
     		logger.debug("##### 주기적인 상태보고를 통한 자전거 배터리 정보 UPDATE 시작 #####");
     	}
     	
     	
     	
	    /**
	     * 거치대 정보로 주차정보 조회하여 전송된 단말과 비교.
	     * Cascade의 경우 거치대 정보를 확인 한후 비교한다.
	     */
     	 if(vo.getReturnForm().equals("01")){
      		
      		/**
      		 * Cascade 거치된 거치대정보 조회
      		 */
      		Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
      		
      		if(rackInfo == null){
      			
      			com.setBikeStusCd("BKS_001");
      			com.setBikeId(vo.getBicycleId());
      			com.setBikeBrokenCd("ELB_007");
   			 
      			/**
      			 * 장애신고
      			 */
      	//		callBrokenError(com);
   				
      			logger.error("CASCADE 거치대 확인실패");
      			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
      			responseVo = setFaiiMsg(responseVo, vo);
      			 
      			 return responseVo;
      		}
      		
      		//조회된 거치대 ID로 거치대 정보 등록.
      		com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
      	}
      	
     	//거치대를 확인하기 위해, stationId를 조회함
     	try{ 
     		com.setStationId(commonService.checkMount(com).get("NOW_LOCATE_ID").toString());
     	}catch(Exception e){}
      	/**
      	 * 기존 주차정보를 조회...현재 전송된 데이타와 비교하기 위해서...
      	 */
     	
     	/* start 2019.05.23 강제 반납 관련 */
     	String bike_status = commonService.checkdBikeStateInfo(com);
     	
     	if(bike_status != null)
     	{
     		//BKE_016,BKS_017 추가  도난시고 추가 
       		if(bike_status.equals("BKS_012") || bike_status.equals("BKS_016") ||bike_status.equals("BKS_017"))	//고장신고 되어 있으면 완료 처리...
     		{
       			Integer enfrc_return_hist_seq = 0;
     		
     			logger.debug("START : TB_SVC_ENFRC_RETURN_PROCESSING is AUTO NORMAL ");
     			
     			enfrc_return_hist_seq = commonService.checkForcedReturnInfo(com);
     			
     		//	logger.debug("START : TB_SVC_ENFRC_RETURN_PROCESSING is AUTO NORMAL {}",enfrc_return_hist_seq.intValue());
     			
     			if( enfrc_return_hist_seq != null && enfrc_return_hist_seq.intValue() != 0)
     			{
     				commonService.updateForcedReturnState(enfrc_return_hist_seq.intValue());
     			}
     			else
     				logger.debug("START : TB_SVC_ENFRC_RETURN_PROCESSING is NULL");
     		}
     		
     	}
     	
     	/* end 2019.05.23 강제 반납 관련 */
   //  	logger.debug("START : checkParkingInfo !!!!!!!!! ");
     	
		Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
		 
		/**
		 * 자전거 장애신고 정보가 존재하면,
		 * 자전거 정보는 설치 후 고장상태로 변경 저장한다.
		 * 반드시 장애가 존재하는 자전거는 사전 수리완료 처리 후 설치 진행.
		 * 
		 * (신규) Locker 오류 (F1) 전달시.
		 * 고장으로 등록(ELB_008)
		 * 
		 * 네트워크 통신장애는 정상으로 복구
		 * 1. 장애신고 내역 변경
		 * 2. 장비 정상 (BKS_003) 으로 변경
		 * 
		 *  단, 네트워크 전송에러 외 다른 장애신고가 존재하는 경우 유지.
		 */
		if(faultSeq == null){
			if(vo.getErrorId().equals("F1") ){
				com.setBikeStusCd("BKS_001");
				com.setBikeBrokenCd("ELB_006");
				
				commonService.insertBrokenBikeErr(com);
				commonService.insertBrokenLocker(com);
				commonService.insertBrokenBikeReport(com);
				commonService.changeBikeBreakDowon(com);
	     	}else{
	     		commonService.changeValidBike(com);
	     	}
	    }else{
			com.setUserSeq(faultSeq);
			if(vo.getErrorId().equals("F1") ){
				/**
				 * 고장상세가 등록되어 있으면 SKIP
				 */
						
				//start 2019.08.29. 수정
				//if(com.getBikeBrokenCd().toString() == null) 
				if(com.getBikeBrokenCd() == null || com.getBikeBrokenCd().equals(""))
	            {
	               com.setBikeStusCd("BKS_001");
	               com.setBikeBrokenCd("ELB_006");
	            }
				//end.2019.03.22
				if(!commonService.isBrokenLocker(com)){
					commonService.insertBrokenLocker(com);
				}
				/**
				 * 동일 수리부품내역이 존재하면 SKIP
				 */
				if(commonService.isBrokenReport(com) == 0 ){
					commonService.insertBrokenBikeReport(com);
				}
			}else{
				/**
				 * 장애가 네트워크 장애만 있는 경우라면 장애정보 삭제..
				 * 
				 */
				if(commonService.hasNetFault(com)){
					commonService.deleteFaultInfo(com);
					commonService.changeValidBike(com);
				}
			}
	   }
		
		/**
		  * 주차정보가 존재하지 않은 경우, 새로이 등록한다.
		  * 1. 단독거치인 경우 기존 거치정보를 삭제한 후 등록한다.
		  * (기존 Cascade 로 하나 이상의 주차정보가 존재시에도 삭제)
		  * 2. 연결반납인 경우. 앞서 연결자전거가 거치대에 존재하는지 확인되었으므로,
		  * 연결자전거로 cascade가 정상적으로 저장되어 있는지 확인한다.
		  */
		
		/**
		 * 주차정보 변경.
		 */
		RentHistVo info = new RentHistVo();
		info.setRETURN_RACK_ID(com.getRockId());
		info.setRENT_BIKE_ID(com.getBicycleId());
		info.setUSE_DIST("0");
		
		if(vo.getReturnForm().equals("00")){
			info.setCASCADE_YN("N");
		}else{
			info.setCASCADE_YN("Y");
			info.setCASCADE_BIKE_ID(vo.getMountsId()); 
		}
		/**
		 * 이미 해당 거치대에 다른 자전거 주차정보가 존재하는 경우.삭제 후 주차정보 신규 추가.
		 */
		 if(parkingMap == null){
//			 logger.error("INVALID 자전거 ID & INVALID 거치대 ID ");
//			 responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
//			 responseVo = setFaiiMsg(responseVo, vo);
//			 
//			 return responseVo;
			 
			 if(vo.getReturnForm().equals("00")){

				 if(commonService.checkParkingCount(com) >0){
					bikeService.deleteParkingInfoOnly(info);
				 }
			 }else{
				 bikeService.deleteParkingInfoCascade(info);
			 }	 
		
			 // 자전거 주차 정보 INSERT PARKING
			 bikeService.insertPeriodParkingInfo(info);
			// 자전거 배치 이력 INSERT LOCATION_BIKE
		
			 //2019.03.20 update 추가 
			 bicycleMapper.rentBikeLocationUpdate(com);
             // 자전거 배치 이력 INSERT LOCATION_BIKE
             bicycleMapper.insertBikeLocation(info);
//        	 bikeService.insertPeriodBikeLocation(info);
				 
		 }else{
			 /**
			  * 주차정보가 존재하므로 전달받은 자전거 정보와 비교..
			  * 일치하면 Pass, 불일치시 업데이트.
			  */
			 if(vo.getReturnForm().equals("00")){
				 if(!com.getRockId().equals(parkingMap.get("RACK_ID"))){
					//다른 곳에 주차된 정보가 있다면 삭제.
					bikeService.deleteDuplicatedParkingInfo(info); 
					//해당 거치대에 다른 자전거가 있다면 삭제.
					bikeService.deleteParkingInfoOnly(info);
					
					bikeService.insertPeriodParkingInfo(info);
					// 자전거 배치 이력 INSERT LOCATION_BIKE
				
					//2019.03.20 update 추가 
					bicycleMapper.rentBikeLocationUpdate(com);
		             // 자전거 배치 이력 INSERT LOCATION_BIKE
		             bicycleMapper.insertBikeLocation(info);
//		         	bikeService.insertPeriodBikeLocation(info);
				 }
			 }else{
				 if(!com.getRockId().equals(parkingMap.get("CASCADE_BIKE_ID"))){
					bikeService.deleteDuplicatedCascadeParkingInfo(info);
					bikeService.deleteParkingInfoCascade(info);
					// 자전거 주차 정보 INSERT PARKING
					bikeService.insertPeriodParkingInfo(info);
					// 자전거 배치 이력 INSERT LOCATION_BIKE
			
					//2019.03.20 update 추가 
					bicycleMapper.rentBikeLocationUpdate(com);
				     // 자전거 배치 이력 INSERT LOCATION_BIKE
		             bicycleMapper.insertBikeLocation(info);
//		     		bikeService.insertPeriodBikeLocation(info);
				 }
					
			 }
			 
		 }
		 
		 
		 /**
		 * 자전거 대여정보가 존재하면 이력정보를 삭제한 후 반납 SMS 발송
		 */
		RentHistVo histInfo = bikeService.checkInvalidRentInfo(com);
		if(histInfo != null){
			histInfo.setRETURN_STATION_ID(com.getStationId());
			histInfo.setRETURN_RACK_ID(com.getRockId());
			histInfo.setTRANSFER_YN("N");
			//자전거 대여정보 삭제(설치시 반납되지 않은 자전거 대여이력정보 삭제)
			try
			 {
				 Date today = new Date();
				 bikeService.deleteRentInfo(histInfo);
				 bikeService.insertInvalidRentHist(histInfo);
				 String returnStationNo = String.valueOf(bicycleMapper.getStationNo(String.valueOf(info.getRETURN_RACK_ID())));
				 //SMS전송.
				 if(histInfo.getUSR_MPN_NO() != null && !histInfo.getUSR_MPN_NO().equals(""))
				 {
					 try
					 {
						 
						 SimpleDateFormat sdf;
						 sdf = new SimpleDateFormat("MM월dd일 HH시mm분");
							
						 SmsMessageVO smsVo = new SmsMessageVO();
						 smsVo.setDestno(histInfo.getUSR_MPN_NO());
						 smsVo.setMsg(SendType.SMS_002, histInfo.getBIKE_NO(), String.valueOf(sdf.format(today)), returnStationNo);
							        
						 SmsSender.sender(smsVo);
					 }
					 catch(Exception e)
					 {
						 
					 }
				 }
			 }
			 catch(Exception e)
			 {
			 
			 }
		}
		
		
		
			
		 logger.debug("상태보고 업데이트  ::::::::::::: ");
		 commonService.updatePeriodState(com);
		 
		 /* 상태보고 상태시간 HISTORY INSERT_20161220_JJH_START
		 commonService.insertinsertPeriodInfo(com, vo);
		 상태보고 상태시간 HISTORY INSERT_20161220_JJH_END */
		 
		
		 
		 
		 
		 Map<String, Object> serverVersion = fileService.getVersion(com);
		 
		 logger.debug("##### period status : firmware_udpate=> " + serverVersion.get("FIRMWARE_CAN_DOWN") + ", imgCanDown : " + serverVersion.get("IMAGE_CAN_DOWN") + ", sdCanDown : " + serverVersion.get("VOICE_CAN_DOWN"));
		 
		 if(serverVersion.get("FIRMWARE_CAN_DOWN") != null && serverVersion.get("IMAGE_CAN_DOWN") != null && serverVersion.get("VOICE_CAN_DOWN") != null){
			 double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_VER").toString());
			 double serverImg = Double.parseDouble(serverVersion.get("IMAGE_VER").toString());
			 double serverSd = Double.parseDouble(serverVersion.get("VOICE_VER").toString());
			 
			 boolean fwUseYn = serverVersion.get("FIRMWARE_USE_YN").equals("Y");
			 boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
			 boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
			 
			 boolean fwCanDown = serverVersion.get("FIRMWARE_CAN_DOWN").equals("Y");
			 boolean imgCanDown = serverVersion.get("IMAGE_CAN_DOWN").equals("Y");
			 boolean sdCanDown = serverVersion.get("VOICE_CAN_DOWN").equals("Y");
			 
			 double requsetFw  = Double.parseDouble(vo.getFirmwareVs().substring(0,2) + "." + vo.getFirmwareVs().substring(2, 4));
			 double requsetImg = Double.parseDouble(vo.getImageVs().substring(0,2) + "." + vo.getImageVs().substring(2, 4));
			 double requsetSd =  Double.parseDouble(vo.getSoundVs().substring(0,2) + "." + vo.getSoundVs().substring(2, 4));
			 boolean chkUseStation = commonService.chkUseStation(com);
			 
			 logger.debug("=====chkVersion====");
			 logger.debug("chkUseStation {}", chkUseStation);
			 logger.debug("canDown {},{},{}", fwCanDown,imgCanDown, sdCanDown);	//시간 체크
			 logger.debug("UseYn {},{},{}", fwUseYn,imgUseYn,sdUseYn);// 사용 여부 
			 //log 추가 
			 logger.debug("requsetVersion {},{},{} serverVersion {},{},{}", requsetFw,requsetImg,requsetSd,serverFw,serverImg,serverSd);
			 logger.debug("requsetVersion {},{},{}", (requsetFw <  serverFw ),(requsetImg <  serverImg),( requsetSd <  serverSd));
			 logger.debug("===================");
			 
			 /**
			  * 사용중지 대여소인 경우,다운로드 가능..
			  * 협의후 진행.. 
			  * FIRMWARE_DOWN_YN  회사 구분 인자 
			  */
			 if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){	// 펌웨어 업데이트 대상 자전거 선별 (7290 : 2100/3600/220/1090/280 CW BJ 확인)_20170329_JJH
				logger.debug("### YES : 주기적인 상태보고 펌웨어 업데이트 ###  해당 자전거는 펌웨어 Vitex_device!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
				  
				if(fwCanDown&&imgCanDown&&sdCanDown){
					if(fwUseYn&&imgUseYn&&sdUseYn){
						if(requsetFw <  serverFw || requsetImg <  serverImg || requsetSd <  serverSd){
							responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_01")); //  f/w 무선 업데이트 진행
						}else{
							responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
						}
					}
				}else{
					responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
				}
			 }else if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("N")){
				 logger.debug("###  YES : 주기적인 상태보고 펌웨어 업데이트 ### 해당 자전거는 Witcom_devcie !!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
				 
				 if(fwCanDown){
					if(fwUseYn)
					{
						
						if(requsetFw <  serverFw || requsetImg < serverImg)
						{
							//1.30 을 올리면 기존 1.29 은 받지 못함.
							if(nBikeSerial >= 20031)	//업데이트시 터치 들어간 단말기만 일단 먼저 패치 가능하게 커트롤 서버 패치 2019.07.24
							{
								logger.debug("###  YES_UPDATE : DEVICE is TOUCH_WITCOM : {}",nBikeSerial);
								responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_01")); //  f/w 무선 업데이트 진행
							}
							else
							{
								//기존 단말기가 펌웨어가 1.29 아래이면 
								
								
								logger.debug("###  YES_UPDATE_ADMIN : DEVICE is BUTTON_WITCOM BUT FIRMWARE IS below 1.29 :{}",nBikeSerial);
								responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_01")); // 업데이트 진행 안함
															
							}
						}
						else
						{
							responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
						}
					}else{
						logger.debug("###  NO : 주기적인 상태보고 위트콤 펌웨어 업데이트 가능여부 ### 자전거 업데이트 조건(fwUseYn)이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
						responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
					}
				}else{
					responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
					logger.debug("###  NO : 주기적인 상태보고 위트콤 펌웨어 업데이트 가능여부 ### 자전거 업데이트 조건(fwCanDown)이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
				} 
				 
			 }else{
				 responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
				 logger.debug("### NO : 주기적인 상태보고 펌웨어 업데이트 ### 해당 자전거는 펌웨어 업데이트 대상이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
			 }
		 }else{
			 
			 logger.debug("##### 관리자 설치 펌웨어 업데이트 : 펌웨어 버전정보(펌웨어가 없음#####");
	         responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
		 }
		 
		 String volumeFlag = commonService.getDayAndNightFlag();	// 단말기 주/야 구분 음성 플래그 추출_20170725_JJH
		 
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_REPORTOFBIKE);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 responseVo.setDayAndNight(volumeFlag);
		 
		 return responseVo;
		 
	 }
	 
	 // 주기적인 상태보고 실패 메세지
	 public PeriodicStateReportsResponseVo setFaiiMsg(PeriodicStateReportsResponseVo responseVo, PeriodicStateReportsRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_REPORTOFBIKE);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 public void callBrokenError( CommonVo com){
		 com.setBikeStusCd("BKS_001");
		 com.setBikeBrokenCd("ELB_006");
		 
		 String faultSeq = commonService.getFaultSeq(com);
			if(faultSeq == null){
				/**
				 * 위치정보 오류로 장애 등록.
				 */
				commonService.insertBrokenBikeErr(com);
				commonService.insertBrokenInvalidLocation(com);
				commonService.insertBrokenBikeReport(com);
		
			}else{
				/**
				 * 장애신고가 있는 경우
				 * faultSeq 조회.
				 */
				com.setUserSeq(faultSeq);
				/**
				 * 고장상세가 등록되어 있으면 SKIP
				 */
				if(!commonService.isInvalidLocationDtl(com)){
					commonService.insertBrokenInvalidLocation(com);
				}
				/**
				 * 동일 수리부품내역이 존재하면 SKIP
				 */
				if(commonService.isBrokenReport(com) == 0 ){
					commonService.insertBrokenBikeReport(com);
				}
				
				// 자전거 정보를 고장상태로 UPDATE BIKE
				commonService.changeBikeBreakDowon(com);
			}
	 }
	 
	 public void callBrokenLockerError( CommonVo com){
		 com.setBikeStusCd("BKS_001");
		 com.setBikeBrokenCd("ELB_006");
		 
		 String faultSeq = commonService.getFaultSeq(com);
			if(faultSeq == null){
				/**
				 * Locker 불량으로 장애 등록.
				 */
				commonService.insertBrokenBikeErr(com);
				commonService.insertBrokenLocker(com);
				commonService.insertBrokenBikeReport(com);
		
			}else{
				/**
				 * 장애신고가 있는 경우
				 * faultSeq 조회.
				 */
				com.setUserSeq(faultSeq);
				/**
				 * 고장상세가 등록되어 있으면 SKIP
				 */
				if(!commonService.isBrokenLocker(com)){
					commonService.insertBrokenLocker(com);
				}
				/**
				 * 동일 수리부품내역이 존재하면 SKIP
				 */
				if(commonService.isBrokenReport(com) == 0 ){
					commonService.insertBrokenBikeReport(com);
				}
				
				// 자전거 정보를 고장상태로 UPDATE BIKE
				commonService.changeBikeBreakDowon(com);
			}
	 }
	 
	 public int versionToInt(String version){
		 
		 if(version == null){
			 version = "0000";
		 }else if(version.equals("0")){
			 version = "0000";
		 }else{
			 String[] array = version.split("\\.");
			 if(array.length == 0){
				 version = Integer.parseInt(version)<10?"0"+version:version;
				 version = version + "00";
				 
			 }else if(array.length == 1){
				 
				 version = Integer.parseInt(array[0])<10?"0"+array[0]:array[0];
				 version += "00";
			 }else{
				 version = Integer.parseInt(array[0])<10?"0"+array[0]:array[0];
				 version += Integer.parseInt(array[1])<10?"0"+array[1]:array[1];
			 }
		 }
		 return Integer.parseInt(version);
	 }
	 
	 
	 public static void main(String[] args) {
		String str = "1.1";
		System.out.println(new PeriodService().versionToInt(str));
		
		str = "1.01";
		System.out.println(new PeriodService().versionToInt(str));
		
		str = "1";
		System.out.println(new PeriodService().versionToInt(str));
		
		str = "10.1";
		System.out.println(new PeriodService().versionToInt(str));
		
		str = "10.11";
		System.out.println(new PeriodService().versionToInt(str));
		
		
	}
	
	 
	 
	 // 주기적인 자동 반납 436F cmd : 0x12
	 @RPCService(serviceId = "Bicycle_12", serviceName = "주기적인 자동반납 Request", description = "주기적인 자동반납 Request")
	 public PeriodicAutoReturnResponseVo autoReturn(PeriodicAutoReturnRequestVo vo) {
		 
		logger.debug("################### 주기적인 자동 반납  Bicycle_12");
	    logger.debug("PeriodicAutoReturnRequestVo vo : {}" , vo);
	    	
		PeriodicAutoReturnResponseVo responseVo = new PeriodicAutoReturnResponseVo();
		CommonVo com = new CommonVo();
		com.setBicycleId(vo.getBicycleId());
		com.setRockId(vo.getMountsId());
	     
	    //Cascade 반납인 경우 입력된 mountid로 거치대 정보 조회.
     	if(vo.getReturnForm().equals("01")){
     		
     		/**
     		 * Cascade 거치된 거치대정보 조회
     		 */
     		Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
     		
     		if(rackInfo == null){
     			logger.error("CASCADE 반납거치대 확인실패");
     			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
     			responseVo = setFaiiMsg(responseVo, vo);
     			 
     			 return responseVo;
     		}
     		
     		/*조회된 거치대 ID로 거치대 정보 등록.NULL POINTER EXCEPTION 처리 
     		com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
     		*/
     		//2019.02.11 null 처리 처리 추가 
     		if(rackInfo.get("RETURN_RACK_ID") != null)
     		{
     			//조회된 거치대 ID로 거치대 정보 등록.
     			com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
     		}
     		else
     		{
     			logger.error("RETURN_RACK_ID CONFIRM_ERROR");
     			
     			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
     			responseVo = setFaiiMsg(responseVo, vo);
     			 
     			return responseVo;
     			 
     		}
     		
      	}else{
     		com.setRockId(vo.getMountsId());

     	}
	     
     	//대여소 정보 조회
     	/**
 		 * Cascade 거치된 거치대정보 조회
 		 */
 		Map<String, Object> stationInfo = commonService.checkMount(com);
 		
 		String stationId = null;
 		try {
 			stationId = stationInfo.get("NOW_LOCATE_ID").toString();
 		}catch(Exception e){
 			logger.error("반납거치대 확인실패");
 		}
 		
 		
 		if(stationId == null){
 			logger.error("반납거치대 확인실패");
 			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
 			responseVo = setFaiiMsg(responseVo, vo);
 			 
 			return responseVo;
 		}else{
 			com.setStationId(stationId);
 		}
 			 
		 //전문 ERRORID 02 (반납응답 ERROR) E3(주기적 자동반납 ERROR) 단말기가 올려주는 값.
		 if(vo.getErrorId().equals("02") || vo.getErrorId().equals("E3"))
		 {
			 
			 RentHistVo info = bikeService.getForReturnUse(com);	//대여정보확인.
			 int overPay = 0;
			 
			 //강제반납 처리 해야함..
			 //
			 if(info == null)	//대여 정보 없으면 바로 단망기 응답.처리.하도록 수정.
			 {
				 	
					 logger.error("반납 처리 자전거 ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_E3"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
			
			 }
			 else	//대여 정보 있음.
			 {
				 
		        	// 케스케이드 반납 확인
		        	if(vo.getReturnForm().equals("01")){
		        		
//		        		if(bikeService.getNoParkingRock(com) > 0){
//		        			logger.error("반납 가능한 거치대 있음 ");
//		                	responseVo.setErrorId(Constants.CODE.get("ERROR_E2"));
//		                	responseVo = setFaiiMsg(responseVo, vo);
//		                	
//		                	return responseVo;
//		        		}
		        		
						info.setCASCADE_YN("Y");
		        		info.setCASCADE_BIKE_ID(vo.getMountsId());
		        	}else{
		        		info.setCASCADE_YN("N");
		        	}
		        	
		        	// 방전 error 검출
		        	/**
		        	 * 반납시 전달되는 방전여부는 자가발전여부를 의미.실제로는 주기적인 상태보고에서만 확인하기로 함.
		        	 * 반납시 방전오류는 제거.
		        	 */
//		        	if(vo.getErrorId().equals("E4")){
//		        		// 충전상태 이상 UPdate
//		        		commonService.updateBatteryDischarge(com);
//		        	}
		        	
		        	
		        	
		        	//int overTime = Integer.parseInt(info.getUSE_MI().toString());
		        	int overTime = Integer.parseInt(info.getUSE_MI().toString());
		        	// 사용시간
		        	int hour = Integer.parseInt(vo.getRentTime().substring(0,2), 16);
		        	int min = Integer.parseInt(vo.getRentTime().substring(2,4), 16);
		        	int useMin = (hour*60)+min;
		        	
		        	logger.debug("사용시간 ::::::::: {} 시간 {} 분 , total = {}"   ,  hour , min , useMin);
		        	
		        	// 이동거리
		        	int km = Integer.parseInt(vo.getDistance().substring(0,2), 16);
		        	int me = Integer.parseInt(vo.getDistance().substring(2,4), 16);
		        	int distance = (km*1000)+(me*10);
		        	
		        	
		        	logger.debug("이동거리 ::::::::: {} km {} m , total = {}"   ,  km , me , distance);
		        	
		        	
		        	info.setRETURN_RACK_ID(com.getRockId());
		        	info.setRETURN_STATION_ID(com.getStationId());
		        	info.setTRANSFER_YN("N");
		        	info.setOVER_FEE_YN("N");
		        	info.setUSE_DIST(distance+"");
		        	info.setSYSTEM_MI(info.getUSE_MI());
		        	if(useMin == 0)
		        		info.setUSE_MI(overTime+"");
		        	else
		        		info.setUSE_MI(useMin+"");
		        		
		        	
		        	int weight = bikeService.getUserWeight(info.getUSR_SEQ());
		        	
		        	double co2 = (((double)distance/1000)*0.232);
		        	double cal = 5.94 * (weight==0?65:weight) *((double)distance/1000) / 15;
		        	
		        	
		        	info.setREDUCE_CO2(co2+"");
		        	info.setCONSUME_CAL(cal+"");
		        	
		        	
		        	// 자전거 기본 대여시간 분
		        	/*
		        	com.setComCd("MSI_011");
		        	Map<String, Object> baseRent = commonService.getComCd(com);
		        	int baseRentTime = Integer.parseInt(baseRent.get("ADD_VAL1").toString());
		        	*/
		        	int baseRentTime = Integer.parseInt((String)commonService.getBaseTime(info).get("BASE_TIME"));
		        	
		        	
		        	
		        	// 초과 요금 대상
		        	if(useMin > baseRentTime){
		        		Map<String, Object> fee = new HashMap<String, Object>();
		        		//T-APP PATCH
	        			if(info.getPAYMENT_CLS_CD().equals("BIL_021"))
	        			{
	        				fee.put("ADD_FEE_CLS", "D");
	        			}
	        			else
	        			{
	        				fee.put("ADD_FEE_CLS", info.getADD_FEE_CLS());
	        			}
		        		Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee);
		        		Map<String, Object> maxPolicy = bikeService.getOverFeeMaxPolicy(fee);
		        		
		        		overPay = new CommonUtil().getPay(minPolicy, maxPolicy, useMin);
		        		//if(!(info.getUSR_CLS_CD().equals("USR_002")))
		        	//	{
		        			info.setOVER_FEE_YN("Y");
		        			info.setOVER_FEE(overPay+"");
		        			info.setOVER_MI(String.valueOf(useMin-baseRentTime));
		        	//	}
		        	}
		        	
		        	//강제반납 처리.
		        	//강제 반납 신청 있으면 자동처리 함...추가 ..2019.08.07 위치 옮김.
		    	 	/* start 2019.05.23 강제 반납 관련 */
		         	String bike_status = commonService.checkdBikeStateInfo(com);
		         	
		         	if(bike_status != null)
		         	{
		           		if(bike_status.equals("BKS_012") || bike_status.equals("BKS_016") ||bike_status.equals("BKS_017"))//고장신고 되어 있으면 완료 처리...
		         		{
		           			Integer enfrc_return_hist_seq = 0;
		         		
		         			logger.debug("START_AUTO_RETURN : TB_SVC_ENFRC_RETURN_PROCESSING is AUTO NORMAL ");
		         			
		         			enfrc_return_hist_seq = commonService.checkForcedReturnInfo(com);
		         			
		         		//	logger.debug("START : TB_SVC_ENFRC_RETURN_PROCESSING is AUTO NORMAL {}",enfrc_return_hist_seq.intValue());
		         			
		         			if( enfrc_return_hist_seq != null && enfrc_return_hist_seq.intValue() != 0)
		         			{
		         				commonService.updateForcedReturnState(enfrc_return_hist_seq.intValue());
		         			}
		         			else
		         				logger.debug("START : TB_SVC_ENFRC_RETURN_PROCESSING is NULL");
		         		}
		         		
		         	}	
		        	
		        	
		        	//
		        	// 반납 프로세스 실행
		        	bikeService.procReturn(info);
		        	
				 
				 
			 }
			 
			 
		 }//error 03,02
		 
		 
		 
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTORETURNBIKE);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 주기적인 자동 반납 실패 
	 public PeriodicAutoReturnResponseVo setFaiiMsg(PeriodicAutoReturnResponseVo responseVo, PeriodicAutoReturnRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTORETURNBIKE);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 
	 
	 

}
