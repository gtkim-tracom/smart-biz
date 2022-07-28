package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateService;
import org.fincl.miss.service.biz.bicycle.vo.AdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMountingResponseVo;
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
     	
		if(!vo.getBattery().equals("") && !vo.getBattery().equals(null))
		{
			logger.debug("##### 관리자 이동을 통한 자전거 배터리 정보 UPDATE 시작 #####");
			
			PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
			periodicStateReportsRequestVo.setBattery(vo.getBattery());
			periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
			
			commonService.updateBatteryInfo(periodicStateReportsRequestVo);
			
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
	 public AdminMountingResponseVo adminMounting(AdminMountingRequestVo vo) {
		 
		 logger.debug("######################## admin_install Bicycle_01");
		 logger.debug("AdminMountingRequestVo vo ::::::::::: {}" , vo);
		 
		 String card = vo.getCardNum().substring(0,4)+"-"+vo.getCardNum().substring(4,8)+"-"+vo.getCardNum().substring(8,12)+"-"+vo.getCardNum().substring(12,16);
			
		 boolean mountingFlag = true;
		 
		 AdminMountingResponseVo responseVo = new AdminMountingResponseVo();
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setBikeBrokenCd(vo.getBicycleId());
		 com.setRockId(vo.getMountsId());
		 com.setCardNum(card); 
		 
		 Map<String, String> ourBikeMap = new HashMap<String, String>();
		 ourBikeMap = bikeService.chkOurBike(com);
		
		 //add 자전거 번호 가져오기 2018.09.01
		 String  bikeNo = ourBikeMap.get("BIKE_NO");
		 int	 nBikeSerial = Integer.parseInt(bikeNo.substring(4,bikeNo.length()));
		 
		 if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			 logger.debug("BIKE VITEX COMPANY NO: {} ",nBikeSerial);	//추가함...
			 com.setCompany_cd("CPN_001");
		 }else{
			 logger.debug("BIKE WITCOM COMPANY NO: {} ",nBikeSerial);	//추가함...
			 com.setCompany_cd("CPN_002");
		 }
		 
		/**
		* 관리자 설치를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
		*/
		
		if(!vo.getBattery().equals("") && !vo.getBattery().equals(null))
		{
			logger.debug("##### 관리자 설치를 통한 자전거 배터리 정보 UPDATE 시작 #####");
			
			PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
			periodicStateReportsRequestVo.setBattery(vo.getBattery());
			periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
			
			commonService.updateBatteryInfo(periodicStateReportsRequestVo);
			
			logger.debug("##### 관리자 설치를 통한 자전거 배터리 정보 UPDATE 종료 #####");
		}
		 
		//강제 반납 신청 있으면 자동처리 함...추가 ..2019.08.07 위치 옮김.
	 	/* start 2019.05.23 강제 반납 관련 */
     	String bike_status = commonService.checkdBikeStateInfo(com);
     	
     	if(bike_status != null)
     	{
       		if(bike_status.equals("BKS_012") || bike_status.equals("BKS_016") ||bike_status.equals("BKS_017"))	//고장신고 되어 있으면 완료 처리...
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
				 
				 
			 }else if(vo.getBicycleState().equals("01")){
				 logger.debug("관리자 설치");
				 mountingFlag = bikeService.procAdminMounting(com, vo);
			 }
			 
			 if(!mountingFlag){
				 logger.error("CASCADE 관리자 설치 오류");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }
			 
		 /**
		  * CASCADE 관리자 설치_20170725_JJH_END
		  */
			 
		 } catch (Exception e) {
			 logger.error("관리자 설치 오류 ");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 
	     	
	     	
		    
	        // 버전 체크
	        Map<String, Object> serverVersion = fileService.getVersion(com);
	        
	        logger.debug("##### 관리자 설치 : 펌웨어 다운로드 가능여부 => " + serverVersion.get("FIRMWARE_CAN_DOWN") + ", imgCanDown : " + serverVersion.get("IMAGE_CAN_DOWN") + ", sdCanDown : " + serverVersion.get("VOICE_CAN_DOWN"));
	        
	        if(serverVersion.get("FIRMWARE_CAN_DOWN") != null && serverVersion.get("IMAGE_CAN_DOWN") != null && serverVersion.get("VOICE_CAN_DOWN") != null){
		        double requsetFw  = Double.parseDouble(vo.getFirmwareVs().substring(0,2) + "." + vo.getFirmwareVs().substring(2, 4));
				double requsetImg = Double.parseDouble(vo.getImageVs().substring(0,2) + "." + vo.getImageVs().substring(2, 4));
				double requsetSd =  Double.parseDouble(vo.getSoundVs().substring(0,2) + "." + vo.getSoundVs().substring(2, 4));
				
				 
		        double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_VER").toString());
		        double serverImg = Double.parseDouble(serverVersion.get("IMAGE_VER").toString());
		        double serverSd = Double.parseDouble(serverVersion.get("VOICE_VER").toString());
		        
		        boolean fwUseYn = serverVersion.get("FIRMWARE_USE_YN").equals("Y");
				boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
				boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
				 
				boolean fwCanDown = serverVersion.get("FIRMWARE_CAN_DOWN").equals("Y");
				boolean imgCanDown = serverVersion.get("IMAGE_CAN_DOWN").equals("Y");
				boolean sdCanDown = serverVersion.get("VOICE_CAN_DOWN").equals("Y");
				 
				if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){	// 펌웨어 업데이트 대상 자전거 선별 (7290 : 2100/3600/220/1090/280 CW BJ 확인)_20170329_JJH
					logger.debug("### YES : 관리자 설치 펌웨어 업데이트 ###  해당 자전거는 펌웨어 업데이트 대상임~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
					 
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
					logger.debug("###  YES_ADMIN : 펌웨어 업데이트 ### 해당 자전거는 위트콤 펌웨어 업데이트 대상임  witcom_device!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
					 
					if(fwCanDown){
						if(fwUseYn){
							if(requsetFw <  serverFw){
								
								//1.30 을 올리면 기존 1.29 은 받지 못함.
								if(nBikeSerial >= 20031)	//업데이트시 터치 들어간 단말기만 일단 먼저 패치 가능하게 커트롤 서버 패치 2019.07.24
								{
									logger.debug("###  YES_UPDATE_ADMIN : DEVICE is TOUCH_WITCOM :{}",nBikeSerial);
									responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_01")); //  f/w 무선 업데이트 진행
								}
								else
								{
									
									logger.debug("###  YES_UPDATE_ADMIN : DEVICE is BUTTON_WITCOM BUT FIRMWARE IS below 1.29 :{}",nBikeSerial);
										responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_01")); // 업데이트 진행 안함
																	}
							}else{
								responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
							}
						}else{
							responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
							logger.debug("### NO WITCOM 펌웨어 업데이트 가능여부 ### 자전거 업데이트 조건(fwUseYn)이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
						}
					}else{
						responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
						logger.debug("### NO WITCOM 펌웨어 업데이트 가능여부 ### 자전거 업데이트 조건(fwCanDown)이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
					}
				
				}else{
					responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
					logger.debug("### NO : 관리자 설치 펌웨어 업데이트 ### 해당 자전거는 펌웨어 업데이트 대상이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
				}
	        }else{
	        	logger.debug("##### 관리자 설치 펌웨어 업데이트 : 펌웨어 버전정보(펌웨어가 없음)#####");
	        	responseVo.setUpdate(Constants.CODE.get("WIFI_UPDATE_00")); // 업데이트 진행 안함
	        }
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_RESTOREBYADMIN);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_04"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 관리자 설치 실패 메세지
	 public AdminMountingResponseVo setFaiiMsg(AdminMountingResponseVo responseVo, AdminMountingRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_RESTOREBYADMIN);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
  
	 
}
