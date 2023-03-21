package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.QREventReportRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.QREventReportResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@RPCServiceGroup(serviceGroupName = "고장점검")
@Service
public class BreakDownService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	BicycleRentService bikeService;
	
	
	// 점검/ 고장신고//이벤트 보고
	 @RPCService(serviceId = "Bicycle_06", serviceName = "점검/고장신고 Request", description = "점검/고장신고 Request")
	 public QREventReportResponseVo adminMove(QREventReportRequestVo vo) {
		 
		 logger.debug("#################### QR_Event_Report ");
		 logger.debug("QREventReportRequestVo vo :::::::::::{}" , vo);
		 
		 //double latitude, longitude = 0.0d;
		 
		 QREventReportResponseVo responseVo = new QREventReportResponseVo();
		
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 
		 com.setBikeId(vo.getBicycleId());
		 
		 Map<String, String> ourBikeMap = new HashMap<String, String>();
		 ourBikeMap = bikeService.chkOurBike(com);	//ENTRPS_CD : ENT_001(vick) , ENT_002(witcom)  , ENT_003(atech)
			 
		 String	 nBikeSerial;
		 if(ourBikeMap != null)
		 {
			//add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
			nBikeSerial = bikeNo.substring(2,bikeNo.length());
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 	
		 	logger.debug("QR_4370 ##### => bike {} ,state {} , company {} ",vo.getBicycleId(),vo.getBicycleState(),com.getCompany_cd());
		 	 
		 	Map<String, String> qrlog = new HashMap<String, String>();
		 	
		 	QRLogVo QRLog = new QRLogVo();
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 	QRLog.setLock(vo.getLockState());
		 	
		 	QRLog.setDev_BATT(String.valueOf(Integer.parseInt(vo.getBattery(), 16)));
		 	
		 	//QRLog.setBeacon_BATT(String.valueOf(Integer.parseInt(vo.getBeaconBattery(), 16)));
		 	//QRLog.setBike_BATT(String.valueOf(Integer.parseInt(vo.getBikeBattery(), 16)));

		 	
		 	if(!vo.getGps_Latitude().equals("00000000") && !vo.getGps_Latitude().substring(0,6).equals("FFFFFF")
					 && !vo.getGps_Longitude().equals("00000000") && !vo.getGps_Longitude().subSequence(0, 6).equals("FFFFFF"))
		 	{
		 		QRLog.setXpos(new CommonUtil().GetGPS(vo.getGps_Latitude()));
		 		QRLog.setYpos(new CommonUtil().GetGPS(vo.getGps_Longitude()));
		 		com.setGPS_X(new CommonUtil().GetGPS(vo.getGps_Latitude()));
		 		com.setGPS_Y(new CommonUtil().GetGPS(vo.getGps_Longitude()));
		 	}
		 	else
            {
		 		
		 		QRLog.setXpos("00000000");
		 		QRLog.setYpos("00000000"); 
		 		
		 	//	QRLog.setXpos(vo.getGps_Latitude());
		 		//QRLog.setYpos(vo.getGps_Longitude());        
            }

		 	
		 	
		 	QRLog.setTimeStamp(vo.getRegDttm());
			QRLog.setMessage(vo.getReqMessage());
			QRLog.setQr_frame("이벤트 보고");
		 	
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
		 
		 
		 int battery = Integer.parseInt(vo.getBattery(), 16);
		 String battery_info = null;
  		
		 battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
  		
		 Map<String, String> battery_map = new HashMap<String, String>();
		 battery_map.put("BATTERY", String.valueOf(battery));
		 battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
		 battery_map.put("BICYCLE_ID", vo.getBicycleId());
  		
		 commonService.updateBatteryInfo(battery_map);
		 
		 
		 if(!vo.getGps_Latitude().equals("00000000") && !vo.getGps_Latitude().substring(0,6).equals("FFFFFF")
				 && !vo.getGps_Longitude().equals("00000000") && !vo.getGps_Longitude().subSequence(0, 6).equals("FFFFFF"))
		 {
			 String latitude = new CommonUtil().GetGPS(vo.getGps_Latitude());
			 String longitude = new CommonUtil().GetGPS(vo.getGps_Longitude());

		 	logger.debug("EVENT GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
		 }
		 
		 if(vo.getEventType().equals("F3"))	//락상태 이상...
		 {
			 
			 logger.debug("#################### QR_Event_Report: Don't callBrokenLockerError ");
		//	 callBrokenLockerError(com); //ERB_008 LOCKER 불량
			 
		 }
		 else if(vo.getEventType().equals("F4")) //도난보고 의심...
		 {
			 logger.debug("#################### QR_Event_Report : Don't callBrokenThiftError ");
			// callBrokenThiftError(com);//ERB_005: 도난 보고 lockoff
		 }
		 else if(vo.getEventType().equals("F5")) //배터리...
		 {
			 logger.debug("#################### QR_Event_Report : Don't callBrokenBikeLowBattery ");
		//	 callBrokenBatteryError(com);
			 
			 
			// callBrokenThiftError(com);//ERB_003: LOW BATTERY
		 }
		 else if(vo.getEventType().equals("F6")) //넘어짐...
		 {
			 logger.debug("#################### QR_Event_Report : callBikeFallingError ");
			 
			 //callBrokenThiftError(com);
		 }
		 else
		 {
			 logger.debug("#################### UNKNOWN_QR_Event_Report {} ",vo.getEventType());
		 }
		 
		 responseVo.setBicycleState(vo.getBicycleState());
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_JAMDECLARATION);
		 responseVo.setBicycleId(vo.getBicycleId());
	
		 return responseVo;
		 
	 }
	 
	 // 점검 고장신고 실패
	 public QREventReportResponseVo setFaiiMsg(QREventReportResponseVo responseVo, QREventReportRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_JAMDECLARATION);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 public void callBrokenLockerError( CommonVo com)
	 {
		 com.setBikeStusCd("BKS_001");
		 com.setBikeBrokenCd("ELB_005");
		 
		 String faultSeq = commonService.getFaultSeq(com);
		 if(faultSeq == null)
		 {		
				commonService.insertBrokenBikeErr(com);
				commonService.insertBrokenLocker(com);
				commonService.insertBrokenBikeReport(com);
		 }
		 else
		 {
				com.setUserSeq(faultSeq);
							
				//start 2019.08.29. 수정
				//if(com.getBikeBrokenCd().toString() == null) 
				//end.2019.03.22
				if(!commonService.isBrokenLocker(com))
				{
					commonService.insertBrokenLocker(com);
				}
				if(commonService.isBrokenReport(com) == 0 )
				{
					commonService.insertBrokenBikeReport(com);
				}
		 }
		 commonService.changeBikeBreakDowon(com);
	 }
	 
	 
	 
	 public void callBrokenThiftError( CommonVo com)
	 {
		 com.setBikeStusCd("BKS_001");
		 com.setBikeBrokenCd("ELB_006");
		 
		 String faultSeq = commonService.getFaultSeq(com);
		 if(faultSeq == null)
		 {
			 /**
			  * Locker 불량으로 장애 등록.
			  */
			 commonService.insertBrokenBikeErr(com);
			 commonService.insertBrokenThift(com);
			 commonService.insertBrokenBikeReport(com);
		 }
		 else
		 {
				/**
				 * 장애신고가 있는 경우
				 * faultSeq 조회.
				 */
				com.setUserSeq(faultSeq);
				/**
				 * 고장상세가 등록되어 있으면 SKIP
				 */
				if(!commonService.isBrokenThift(com)){
					commonService.insertBrokenThift(com);
				}
				/**
				 * 동일 수리부품내역이 존재하면 SKIP
				 */
				if(commonService.isBrokenReport(com) == 0 ){
					commonService.insertBrokenBikeReport(com);
				}
		 }
		 // 자전거 정보를 고장상태로 UPDATE BIKE
		 commonService.changeBikeBreakDowon(com);
	 }
	 
	 
	 public void callBrokenBatteryError( CommonVo com)
	 {
		 com.setBikeStusCd("BKS_001");
		 com.setBikeBrokenCd("ELB_005");
			 
		 String faultSeq = commonService.getFaultSeq(com);
		 if(faultSeq == null)
		 {
				 /**
				  * Locker 불량으로 장애 등록.
				  */
			 commonService.insertBrokenBikeErr(com);
			 commonService.insertBrokenLowBattery(com);
			 commonService.insertBrokenBikeReport(com);
			
		 }
		 else
		 {
			 /**
			  * 장애신고가 있는 경우
			  * faultSeq 조회.
			  */
			 com.setUserSeq(faultSeq);
			 /**
			  * 고장상세가 등록되어 있으면 SKIP
			  */
			 if(!commonService.isBrokenLowBattery(com))
			 {
				 commonService.insertBrokenLowBattery(com);
			 }
			 /**
			  * 동일 수리부품내역이 존재하면 SKIP
			  */
			 if(commonService.isBrokenReport(com) == 0 )
			 {
				 commonService.insertBrokenBikeReport(com);
			 }
				
		 }
		 // 자전거 정보를 고장상태로 UPDATE BIKE
		 commonService.changeBikeBreakDowon(com);
	 }
}
