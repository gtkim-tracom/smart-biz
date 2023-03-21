package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateService;
import org.fincl.miss.service.biz.bicycle.vo.QRAdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.QRAdminMountingResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveCancleRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveCancleResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "관리자")
@Service
public class AdministratorService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	BicycleRentService bikeService;
	
	@Autowired
	CommonService commonService;
    
	@Autowired
	private FileUpdateService fileService;
	
	 @RPCService(serviceId = "Bicycle_05", serviceName = "관리자 이동 Request", description = "관리자 이동 Request")
	 public AdminMoveResponseVo adminMove(AdminMoveRequestVo vo) {
		 
		 
		 logger.debug("######################## Bicycle_05 ");
		 logger.debug("AdminMoveRequestVo vo :::::::::::{} " , vo);
		 
		 String card = vo.getCardNum().substring(0,4)+"-"+vo.getCardNum().substring(4,8)+"-"+vo.getCardNum().substring(8,12)+"-"+vo.getCardNum().substring(12,16);
			
		 AdminMoveResponseVo responseVo = new AdminMoveResponseVo();
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 
		 if(String.valueOf(vo.getMountsId()).substring(0, 3).equals("19B")){
			 String rock_Id = "";
			 
			 rock_Id = commonService.getRockId(vo);
			 
			 com.setRockId(rock_Id);
		 }else{
			 com.setRockId(vo.getMountsId());
		 }
		 
		 com.setCardNum(card);
		 
		/**
		 * 관리자 이동을 통한 자전거 배터리 정보 UPDATE_20170208_JJH
		 */
     	
		if(!vo.getBattery().equals(null) && !vo.getBattery().equals(""))
		{
			logger.debug("##### 관리자 이동을 통한 자전거 배터리 정보 UPDATE 시작 #####");
			
			int battery = Integer.parseInt(vo.getBattery(), 16);
     		String battery_info = null;
     		battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
     		Map<String, String> battery_map = new HashMap<String, String>();
     		battery_map.put("BATTERY", String.valueOf(battery));
     		battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
     		battery_map.put("BICYCLE_ID", vo.getBicycleId());
     		
			commonService.updateBatteryInfo(battery_map);
			logger.debug("##### 관리자 이동을 통한 자전거 배터리 정보 UPDATE 종료 #####");
		}
		 
		 // 자전거 상태 값 이상
		 if(!vo.getBicycleState().equals(Constants.CODE.get("BIKE_STATE_02"))){
			 logger.error("INVALID 자전거 ID 자전거 상태값 이상" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E9"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 // 자전거와 거치대 정보가 일치하지 않으면 잘못된 정보로 처리
		 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
		 if(parkingMap == null){
			 logger.error("INVALID 자전거 ID & INVALID 거치대 ID ");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 bikeService.procAdminMove(com);
		 
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_MOVEADMIN);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_04"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 관리자 이동 실패 메세지
	 public AdminMoveResponseVo setFaiiMsg(AdminMoveResponseVo responseVo, AdminMoveRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_MOVEADMIN);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 
	 
	 
	 /// 관리자 이동 취소 
	 @RPCService(serviceId = "Bicycle_09", serviceName = "관리자 이동 취소 Request", description = "관리자 이동 취소 Request")
	 public AdminMoveCancleResponseVo adminMoveCancle(AdminMoveCancleRequestVo vo) {
		 
		 AdminMoveCancleResponseVo responseVo = new AdminMoveCancleResponseVo();
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setRockId(vo.getMountsId());
		 
		 bikeService.removeRelocateHist(com);
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_MOVEADMINCANCLE);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_04"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 관리자 이동 실패 메세지
	 public AdminMoveCancleResponseVo setFaiiMsg(AdminMoveCancleResponseVo responseVo, AdminMoveCancleRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.CID_RES_MOVEADMIN);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 
	 
	 
	 /// 관리자 설치
	 @RPCService(serviceId = "Bicycle_01", serviceName = "관리자 설치 Request", description = "관리자 설치 Request")
	 public QRAdminMountingResponseVo adminMounting(QRAdminMountingRequestVo vo) {
		 
		 //double latitude, longitude = 0.0d;
		 logger.debug("######################## QR_admin_install Bicycle_01");
		 logger.debug("QRAdminMountingRequestVo vo ::::::::::: {}" , vo);
		 
		 boolean mountingFlag = true;
		 
		 QRAdminMountingResponseVo responseVo = new QRAdminMountingResponseVo();
		 
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setBikeBrokenCd(vo.getBicycleId());
		 com.setRockId(vo.getBeaconId());
		 
		 Map<String, String> ourBikeMap = new HashMap<String, String>();
		 ourBikeMap = bikeService.chkOurBike(com);	//ENTRPS_CD : ENT_001(vick) , ENT_002(witcom)  , ENT_003(atech)
		 
		 String	 nBikeSerial;
				 
		 QRLogVo QRLog = new QRLogVo();
		 
		 if(ourBikeMap != null)
		 {
			//add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
		 	nBikeSerial = bikeNo.substring(2,bikeNo.length());
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
	//	 	Integer.parseInt(str);
		 	Integer nVer = Integer.parseInt(vo.getModem_firmwareVersion().substring(2, 4));
		 	
		 	logger.debug("QR_437C ##### => bike {} ,state {} , company {} , VER {} ",vo.getBicycleId(),vo.getBicycleState(),com.getCompany_cd(),nVer);
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 	QRLog.setBeaconid(vo.getBeaconId());
		 	QRLog.setLock(vo.getLockState());
		 	
		 	if(!vo.getUsrseq().equals(("FFFFFFFFFF")))
		 	{
		 		//QRLog.setUserSeq(new CommonUtil().GetUSRSeq(vo.getUsrseq()));
		 		QRLog.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
		 		
		 	}
		 	
		 	QRLog.setDev_BATT(String.valueOf(Integer.parseInt(vo.getBattery(), 16)));
		 	//QRLog.setBeacon_BATT(String.valueOf(Integer.parseInt(vo.getBeaconBattery(), 16)));
		 	QRLog.setBike_BATT(String.valueOf(Integer.parseInt(vo.getBikeBattery(), 16)));
		 	QRLog.setFirm_fw(vo.getBle_firmwareVersion());
		 	QRLog.setModem_fw(vo.getModem_firmwareVersion());
		 	
		 	
		 	
		 	if(!vo.getGps_Latitude().equals("00000000") && !vo.getGps_Latitude().substring(0,6).equals("FFFFFF")
					 && !vo.getGps_Longitude().equals("00000000") && !vo.getGps_Longitude().subSequence(0, 6).equals("FFFFFF"))
		 	{
		 		QRLog.setXpos(new CommonUtil().GetGPS(vo.getGps_Latitude()));
		 		QRLog.setYpos(new CommonUtil().GetGPS(vo.getGps_Longitude()));
		 	}
		 	else
            {
		 		
		 		QRLog.setXpos("00000000");
		 		QRLog.setYpos("00000000"); 
		 		
		 //		QRLog.setXpos(vo.getGps_Latitude());
		 //		QRLog.setYpos(vo.getGps_Longitude());
                    
            }

		 	
		 	QRLog.setTimeStamp(vo.getRegDttm());
			QRLog.setMessage(vo.getReqMessage());
			QRLog.setQr_frame("관리자 설치");
			
			if(nVer >= 3 && ourBikeMap.get("BIKE_SE_CD").toString().equals("BIK_001") )
			{
				logger.debug("bike firmware is Only BLE_CMD !!!!!!!");
				commonService.updateblebike(com);
			}
			else if(nVer < 3 && ourBikeMap.get("BIKE_SE_CD").toString().equals("BIK_002"))
			{
				logger.debug("bike firmware is Only SMS_CMD !!!!!!!");
			}
			
		 	bikeService.insertQRLog(QRLog);
		 }
		 else
		 {
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
	
		//Beacon id 조회
		 Map<String, String> ReqInfo = new HashMap<String, String>();
		 ReqInfo.put("BEACON_ID", String.valueOf(vo.getBeaconId()));
		 ReqInfo.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
		 
		 /*
		 if(vo.getBeaconId().equals("00000000000000"))
		 {
			 
			 logger.debug("QRAdminMounting : beacon_id is NULL : auth failed {}",vo.getBeaconId());
			 
			 QRLog.setResAck("00000");
	 		 bikeService.updateQRLog(QRLog);
	 			
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
	  		 responseVo = setFaiiMsg(responseVo, vo);
	  		 responseVo.setBicycleState(vo.getBicycleState());
	  		 
	  		
	  		 return responseVo;
			 
		 }
		 */
		 	 
		 
		 Map<String, Object> stationInfo = null;
		 
		 
		 if(!vo.getBeaconId().equals("0000000000")) //거치대 반납 
		 {
		 		
			 logger.debug("STATION_UNIT INFO ##### {}",vo.getBeaconId());
			 
			 stationInfo = commonService.checkMount(com);	//SELECT * FROM TB_OPR_RACK WHERE RACK_ID = ? 
 			
			 com.setStationId((String)stationInfo.get("NOW_LOCATE_ID"));
			 
			 
		 }
		 else if(!vo.getGps_Latitude().equals("00000000") && !vo.getGps_Latitude().substring(0,6).equals("FFFFFF")
				 && !vo.getGps_Longitude().equals("00000000") && !vo.getGps_Longitude().subSequence(0, 6).equals("FFFFFF"))
		 {
								
			String latitude = new CommonUtil().GetGPS(vo.getGps_Latitude());
			String longitude = new CommonUtil().GetGPS(vo.getGps_Longitude());
	        
			logger.debug("GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
			 
			Map<String, String> GPS = new HashMap<String, String>();
			GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
			GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
			GPS.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
			stationInfo = commonService.CheckStation_ForGPS(GPS);
			
			if(stationInfo == null)
			{
				logger.error("Station List Find Error");
				responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
				responseVo = setFaiiMsg(responseVo, vo);
		  		
				return responseVo;
			}
			else
			{
				logger.debug("##### BIKE RETURN ## GPS STATION FIND START! #####");
				logger.debug("##### BIKE RETURN ## GPS STATION ID[" + String.valueOf(stationInfo.get("STATION_ID")) + "] ENTERED");
	
			}	 
		 }
		 
		 
		
		 if(stationInfo != null)
		 {
			 //GPS 로 했을경우 
			 if(vo.getBeaconId().equals("0000000000"))
				 com.setStationId(stationInfo.get("STATION_ID").toString());
			 
			 com.setRockId(stationInfo.get("RACK_ID").toString());
		 }
		 else
		 {
			 com.setStationId("ST-1099");
			 
			 if(ourBikeMap.get("BIKE_SE_CD").toString().equals("BIK_001"))
			 {
				 com.setRockId("45800099900000");
			 }
			 else
			 {
				 com.setRockId("45800099900099");
			 }
			 
						 
			 logger.error("QRAdminMounting_BEACON : staionInfo is NOT FOUNT BUT DEFAULT LOCATE SET {}",com.getStationId());
			 
			 /*
			 
			 if(ourBikeMap.get("BIKE_SE_CD").toString().equals("BIK_002"))
				 QRLog.setResAck("RAFA");
			 else
				 QRLog.setResAck("SRAFA");
			 
  			 bikeService.updateQRLog(QRLog);
  			 
			//    임시로 막음..
			 logger.error("QRAdminMounting_BEACON : staionInfo is NOT FOUNT AREA ERROR : beconid ,{}",vo.getBeaconId());
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
			 responseVo = setFaiiMsg(responseVo, vo);
	  		
			 responseVo.setBicycleState(vo.getBicycleState());
			 
			 return responseVo;
			 //GPS 반납 거절 전송
			  */
		 }

		 //com.setUserSeq(new CommonUtil().GetUSRSeq(vo.getUsrseq()));
		 com.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
		 
		
		
		/**
		* 관리자 설치를 통한 자전거 배터리 정보 UPDATE_20170208_JJH 
		* */
		
		
		if(!vo.getBattery().equals(null) && !vo.getBattery().equals(""))
		{
			logger.debug("##### ADMIN MOUNT ## BATTERY UPDATE START #####");
			
			/*
			PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
			periodicStateReportsRequestVo.setBattery(vo.getBattery());
			periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
			*/
			
			int battery = Integer.parseInt(vo.getBattery(), 16);
     		String battery_info = null;
     		battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
     		Map<String, String> battery_map = new HashMap<String, String>();
     		battery_map.put("BATTERY", String.valueOf(battery));
     		battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
     		battery_map.put("BICYCLE_ID", String.valueOf(vo.getBicycleId()));
     		
			commonService.updateBatteryInfo(battery_map);
			/*
			if(!vo.getBeaconBattery().equals("") && !vo.getBeaconBattery().equals(null)
					&& !vo.getBeaconId().equals("") && !vo.getBeaconId().equals(null))
     		{
				int beacon_battery = Integer.parseInt(vo.getBeaconBattery(), 16);
         		Map<String, String> beacon_battery_map = new HashMap<String, String>();
         		beacon_battery_map.put("BATTERY", String.valueOf(beacon_battery));
         		beacon_battery_map.put("BEACON_ID", String.valueOf(vo.getBeaconId()));

				commonService.updateBeaconBatteryInfo(beacon_battery_map);
     		}
			*/
			
			
			//전기 자전거 UPDATE?
			
			logger.debug("##### ADMIN MOUNT ## BATTERY UPDATE END #####");
		}
		 
		//강제 반납 신청 있으면 자동처리 함...추가 ..2019.08.07 위치 옮김.
	 	/* start 2019.05.23 강제 반납 관련 */
     	String bike_status = commonService.checkdBikeStateInfo(com);
     	
     	if(bike_status != null)
     	{
       		if(bike_status.equals("BKS_012"))	//고장신고 되어 있으면 완료 처리...
     		{
       			Integer enfrc_return_hist_seq = 0;
     		
     			logger.debug("START_ADMIN_INSTALL : TB_SVC_ENFRC_RETURN_PROCESSING is AUTO NORMAL ");
     			
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
     	
     	
		 /**
		  * CASCADE 관리자 설치_20170725_JJH_START
		  */
		 try {
			 if(vo.getBicycleState().equals("04")){
				 logger.debug("관리자 이동에 의한 설치");
				 // 관리자 이동에 의한 설치 프로세스 실행
				 mountingFlag = bikeService.procAdminMounting(com, vo);
				 QRLog.setResAck("ASUC");
	  	//		 bikeService.updateQRLog(QRLog);
				 
			 }else if(vo.getBicycleState().equals("01")){
				 logger.debug("관리자 설치");
				 mountingFlag = bikeService.procAdminMounting(com, vo);
				 QRLog.setResAck("ISUC");
			 }
			 
			 if(!mountingFlag){
				 logger.error("CASCADE 관리자 설치 오류");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 QRLog.setResAck("AFAIL");
				 bikeService.updateQRLog(QRLog);
				 return responseVo;
			 }
			 bikeService.updateQRLog(QRLog);
		 /**
		  * CASCADE 관리자 설치_20170725_JJH_END
		  */
			 
		 } catch (Exception e) {
			 logger.error("관리자 설치 오류 ");
			 QRLog.setResAck("ERROR");
			 bikeService.updateQRLog(QRLog);
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 // 버전 체크
		 Map<String, Object> serverVersion = fileService.getVersion(com);

		 /*
		 logger.debug("##### Admin Mount : BLE firmware_time_update=> " + serverVersion.get("FIRMWARE_BLE_TIME_CAN_DOWN") + " firmware_bike_update=> " + serverVersion.get("FIRMWARE_BLE_BIKE_CAN_DOWN"));
		 
		 if(serverVersion.get("FIRMWARE_BLE_TIME_CAN_DOWN") != null )
		 {
			 double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_BLE_VER").toString());
			 
			 boolean fwUseYn = serverVersion.get("FIRMWARE_BLE_USE_YN").equals("Y");
			 
			 boolean fwTimeCanDown = serverVersion.get("FIRMWARE_BLE_TIME_CAN_DOWN").equals("Y");
			 boolean fwBikeCanDown = serverVersion.get("FIRMWARE_BLE_BIKE_CAN_DOWN").equals("Y");
			 double requsetFw  = Double.parseDouble(vo.getBle_firmwareVersion().substring(0,2) + "." + vo.getBle_firmwareVersion().substring(2, 4));
			 boolean chkUseStation = commonService.chkUseStation(com);
		
			 if(fwTimeCanDown && fwBikeCanDown)
			 {
				 if(fwUseYn)
				 {
					 if(requsetFw <  serverFw )
					 {
						 logger.debug("### YES : Admin Mount BLE FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
						 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_01")); //  f/w 무선 업데이트 진행
						 responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); //  f/w 무선 업데이트 진행
					 }
					 else
					 {
						 logger.debug("### NO1 : Admin Mount BLE FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
						 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
						 
					 }
				 }
				 else	//add 2019.12.19 추가..
				 {
					 logger.debug("### NO2 : Admin Mount BLE FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
					 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
					 
				 }
			 }
			 else
			 {
				 logger.debug("### NO3 : Admin Mount BLE FIRMWARE UPDATE2 ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
				 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
			 }
		 }
		 else
		 { 
			 logger.debug("##### Admin Mount BLE FIRMWARE : FIRM VERSION IS NO GET#####");
			 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
		 }
		 */
		 responseVo.setBle_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00"));
		 if(responseVo.getBle_firmwareUpdate().equals(Constants.CODE.get("WIFI_UPDATE_00")))  //BLE UPDATE 대상이므로 MODEM 진행 안함
		 {
			 logger.debug("##### Admin Mount : MODEL firmware_time_update=> " + serverVersion.get("FIRMWARE_MODEM_TIME_CAN_DOWN") + " firmware_bike_update=> " + serverVersion.get("FIRMWARE_MODEM_BIKE_CAN_DOWN"));
			 
			 if(serverVersion.get("FIRMWARE_MODEM_TIME_CAN_DOWN") != null )
			 {
				 double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_MODEM_VER").toString());
				 
				 boolean fwUseYn = serverVersion.get("FIRMWARE_MODEM_USE_YN").equals("Y");
				 
				 boolean fwTimeCanDown = serverVersion.get("FIRMWARE_MODEM_TIME_CAN_DOWN").equals("Y");
				 boolean fwBikeCanDown = serverVersion.get("FIRMWARE_MODEM_BIKE_CAN_DOWN").equals("Y");
				 double requsetFw  = Double.parseDouble(vo.getModem_firmwareVersion().substring(0,2) + "." + vo.getModem_firmwareVersion().substring(2, 4));
				 boolean chkUseStation = commonService.chkUseStation(com);
				 		 
	
				if(fwTimeCanDown && fwBikeCanDown)
				{
					if(fwUseYn)
					{
						if(requsetFw <  serverFw )
						{
							logger.debug("### YES : Admin Mount MODEL FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
							responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_01")); //  f/w 무선 업데이트 진행
						}
						else
						{
							logger.debug("### NO2 : Admin Mount MODEL FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
							responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
						}
					}
					else	//2019.12.18 추가 
					{
						logger.debug("### NO3 : Admin Mount MODEL FIRMWARE UPDATE ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
						responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
					}
				}
				else
				{
					logger.debug("### NO4 : Admin Mount MODEL FIRMWARE UPDATE2 ###  BIKE IS  " + com.getCompany_cd() + "!! BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID"))); 
					responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
				}
			 }
			 else
			 {	 
				 logger.debug("##### Admin Mount MODEM FIRMWARE UPDATE : FIRM VERSION IS NO GET#####");
		         responseVo.setModem_firmwareUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
			 }
		 }
		 
		 responseVo.setConfigChn("00");
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_RESTOREBYADMIN);
		 responseVo.setBicycleState("02");
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 
		 List<HashMap<String, String>> PeriodList = commonService.CheckPeriodTime();
  		 
		 if(PeriodList.size() == 0 )
		 {
	  			 logger.error("Period Information Find Error");
	  			 responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
	  			 responseVo = setFaiiMsg(responseVo, vo);
	           	
	  			 return responseVo;
		 }
 		else
 		{
 			for(HashMap<String, String> PeriodMap : PeriodList)
 			{
 				if(String.valueOf(PeriodMap.get("COM_CD")).equals("MSI_038"))
				 {
					 logger.debug("######################## MSI_038 " + String.valueOf(PeriodMap.get("ADD_VAL1")));
					 int Hour = Integer.parseInt(PeriodMap.get("ADD_VAL1"))/60;
					 int Minute = Integer.parseInt(PeriodMap.get("ADD_VAL1"))%60;
					 responseVo.setPeriodHour(getToString(String.valueOf(Hour),2));
					 responseVo.setPeriodMinute(getToString(String.valueOf(Minute),2));
				 }
 			}
 		}
		 return responseVo;
		 
	 }
	 
	 // 관리자 설치 실패 메세지
	 public QRAdminMountingResponseVo setFaiiMsg(QRAdminMountingResponseVo responseVo, QRAdminMountingRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_RESTOREBYADMIN);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 private String getToString(String str , int length)
	 {
		 int temp = 0;
		 if(str.length() < length)
		 {
			 temp = length - str.length();
			 for (int i = 0; i <  temp; i++)
			 {
				 str = "0"+str;
			 }
		 }
		 return str;
	}
	 
  
	 
}
