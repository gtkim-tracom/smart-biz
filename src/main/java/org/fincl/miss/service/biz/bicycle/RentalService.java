package org.fincl.miss.service.biz.bicycle;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentWaitingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentWaitingResponseVo;
//import org.fincl.miss.service.biz.bicycle.vo.RentWaitingResponseVer2Vo;
import org.fincl.miss.service.biz.bicycle.vo.RentalCancleRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalCancleResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "대여")
@Service
public class RentalService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	BicycleRentService bikeService;
	
	
    // 대여대기 4372
    @RPCService(serviceId = "Bicycle_14", serviceName = "대여 대기 Request", description = "대여 대기 Request")
    public RentWaitingResponseVo waiting(RentWaitingRequestVo vo) {
        
		 logger.debug("######################## Rent_waiting_Bicycle_14 ");		//수정함..
		 logger.debug("RentWaitingRequestVo vo :::::::::::{} " , vo);			//수정함.
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        RentWaitingResponseVo responseVo = new RentWaitingResponseVo();
        /**
         * 정상인 경우 LangClsCd를 사용하고,
         * 에러가 있는 경우 해당 에러코드로 담아 전달한다.
         * 기본 값으로 사용자 기본 언어코드인 00 을 세팅한다.
         * 기본적으로 예약정보를 확인할 수 있는 운휴안내/자전거/거치대 오류 안내 는 한글로 안내.
         * 예약정보 확인후 대여대기 정보 안내만 사용자 언어로 제공.
         */
        responseVo.setLangCd("00");
    	
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setRockId(vo.getMountsId());
        
        logger.debug("RentWaitingRequestVo ADD_LAST_CONN_DTTM : {} " , vo.getBicycleId());			//수정함.
        
        commonService.updatePeriodState(com);
        
     	batteryInfoProc(vo);	// 대여대기를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
        
        
        // 자전거 상태 값 이상
    	/*if(!vo.getBicycleState().equals(Constants.CODE.get("BIKE_STATE_02"))){
    		System.out.println("INVALID 자전거 ID 자전거 상태값 이상" );
    		responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
    		responseVo = setFaiiMsg(responseVo, vo);
    		
    		return responseVo;
    	}*/
     	
     	/*
     	//자전거 버젼 갖고 오는 부분 버젼에 따라 비밀번호  시나리오가 없는 단말기 여부 판단. 2019.03.06
     	Map<String, String> BikeFirmware = new HashMap<String, String>();
        BikeFirmware = bikeService.getBikeFirmwareVersion(com);
        
        if(BikeFirmware != null)
        {
        	 if(String.valueOf(BikeFirmware.get("ENTRPS_CD")).equals("ENT_001")){
                 logger.debug("BIKE VITEX COMPANY NO: {} {} ");   //추가함...
              }else{
                 logger.debug("BIKE WITCOM COMPANY NO :{} {} ");   //추가함...
              }
        	
             double Firmware = Double.parseDouble(String.valueOf(BikeFirmware.get("FIRMWARE_VER")));
             
             logger.debug("BIKE Firmware Version {} ",Firmware);

        }
        //
    	//운휴기간 여부 확인.
        */
     	
    //    boolean chkDelayTime = commonService.chkDelayTime(com);
     	boolean chkDelayTime = true;
     	
        boolean chkUseStation = false;
        if(chkDelayTime){
        	// 이용중지 대여소 여부 확인
        	
        	if(vo.getReturnForm().equals(Constants.CODE.get("RETURN_LOCK_01"))){
        		Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
        		
        		//add 2019.08.29
        		if(rackInfo == null || rackInfo.get("RETURN_RACK_ID") == null || rackInfo.get("RETURN_RACK_ID").toString().equals(""))
        		{
        			logger.error(" rackInfo is null : RETURN_RACK_ID_IS_NULL ");
        			
        			 logger.error("[RentWaitingResponseVo 4372] : selectCascadParkingRock_NULL Error {} ",vo.getBicycleId());
        			 
        			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
                    responseVo = setFaiiMsg(responseVo, vo);
                      
                    return responseVo;
        		}
        		else
        		{
        			chkUseStation = commonService.chkUseStationByRockId(String.valueOf(rackInfo.get("RETURN_RACK_ID")));
        		
        			logger.debug("##### CASCADE 거치 자전거정보 ##### => 거치대 역할 자전거 : " + String.valueOf(com.getRockId()) + ", 거치대 ID 추출 : " + String.valueOf(rackInfo.get("RETURN_RACK_ID")));
        		}
        	}else{
        		chkUseStation = commonService.chkUseStation(com);
        	}
        	
        	
        	if(chkUseStation){
        		//서비스 이용시간 내 대여요청 여부 확인
        		if(!commonService.chkUseTime(com)){
        			logger.error("서비스 이용시간 초과");
	   	   			 responseVo.setErrorId(Constants.CODE.get("ERROR_CE"));
	   	   			 responseVo = setFaiiMsg(responseVo, vo);
	   	   			 
	   	   			 return responseVo;
        		}
        	}else{
        		logger.error("이용중지 대여소 내 대여불가");
	   			 responseVo.setErrorId(Constants.CODE.get("ERROR_CD"));
	   			 responseVo = setFaiiMsg(responseVo, vo);
	   			 
	   			 return responseVo;
        	}
        }else{
        	logger.error("운휴기간 내 대여불가");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_CF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
        }
        
        
        // 해당하는 자전거 정보가 없는 경우
		 Map<String, Object> deviceInfo = commonService.checkBicycle(com);
		 if(deviceInfo == null){
		//	 logger.error("INVALID 자전거 ID ");
			 logger.error("[RentWaitingResponseVo 4372] : checkBicycle Error {} ",vo.getBicycleId());
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
			 
//		 logger.debug("deviceInfo : {}" ,deviceInfo);
		 
		 //bikeService.updateLockTEst(com);
		 
		// Cascade 상태(거치된 자전거 중 맨 끝 확인)
		 if(!commonService.isLastCascade(com)){
			 logger.error("[RentWaitingResponseVo 4372] : isLastCascade Error {} ",vo.getBicycleId());
			 
		//	 logger.error("Cascade 대여오류" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E8"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		// Cascade 확인
		 if(vo.getReturnForm().equals(Constants.CODE.get("RETURN_LOCK_01"))){
			 
			 /**
     		 * Cascade 거치된 거치대정보 조회
     		 */
     		Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
     		
     		if(rackInfo == null){
     			logger.error("[RentWaitingResponseVo 4372] : selectCascadParkingRock Error {} ",vo.getBicycleId());
     			
     	//		logger.error("CASCADE 반납거치대 확인실패");
     			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
     			responseVo = setFaiiMsg(responseVo, vo);
     			 
     			 return responseVo;
     		}
     		
     		//조회된 거치대 ID로 거치대 정보 등록.
     		com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
     	 }
				 
		 
		 // 해당하는 거치대 정보가 없는 경우
		 Map<String, Object> mountInfo = commonService.checkMount(com);
		 if(mountInfo == null){
		//	 logger.error("INVALID 거치대 ID ");
			 logger.error("[RentWaitingResponseVo 4372] : checkMount Error {} ",vo.getBicycleId());
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 
		 // 자전거와 거치대 정보가 일치하지 않으면 잘못된 정보로 처리
		 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
		 if(parkingMap == null){
			 logger.error("[RentWaitingResponseVo 4372] : checkParkingInfo Error {} ",vo.getBicycleId());
			 responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 com.setBikeId(String.valueOf(parkingMap.get("BIKE_ID")));
        
        	
		 // 고장 여부 확인
		 if(commonService.checkBreakDown(com) > 0){
			 logger.error("[RentWaitingResponseVo 4372] : checkBreakDown Error {}",vo.getBicycleId() );			//고장 신고 로그 추가 
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E9"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 
        	
        
        
        // 대여대기(예약) 확인
        Map<String, Object>  reservation = commonService.reservationCheck(com);
        
        if(reservation == null){
        	responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_00"));   // 대여대기 없음
        	responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 카드 등록 진행 안함
        	
        	// 임시 예약 여부 확인
        	Map<String, Object>  temp = commonService.tempReservationCheck(com);
        	if(temp == null){
        		// 거치대 임시 예약 저장
        		//commonService.tempReservation(com);
        	}else{
        		responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_01"));   // 대여대기 있음
        		responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 카드 등록 진행 안함
        	}
        	
        	
        	
        }else{
        	responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_01"));   // 대여대기 있음
        	/**
        	 * 대여대기가 존재하는 경우, 언어코드 등록.
        	 */
        	responseVo.setLangCd(Constants.CODE.get(reservation.get("LANG_CLS_CD")));
        	if(reservation.get("TERMINAL_CARD_REG_YN") == null){
        		responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 진행 안함
        	}else{
        		if(reservation.get("TERMINAL_CARD_REG_YN").equals("Y")){
        			responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_01"));	// 카드 등록
        		}else{
        			responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 진행 안함
        		}
        	}
        }
        
        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }




	/**
	 * @param vo
	 */
	private void batteryInfoProc(RentWaitingRequestVo vo) {
		if(!vo.getBattery().equals("") && !vo.getBattery().equals(null))
     	{
     		logger.debug("##### 대여대기를 통한 자전거 배터리 정보 UPDATE 시작 #####");
     		
     		PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
     		periodicStateReportsRequestVo.setBattery(vo.getBattery());
     		periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
     		
     		commonService.updateBatteryInfo(periodicStateReportsRequestVo);
     		
     		logger.debug("##### 대여대기를 통한 자전거 배터리 정보 UPDATE 종료 #####");
     	}
	}
    
	/*
    //2019.03.06
@RPCService(serviceId = "Bicycle_78", serviceName = "대여 대기.Ver2 Request", description = "대여 대기.Ver2 Request")
public RentWaitingResponseVer2Vo waiting_ver2(RentWaitingRequestVo vo) {
	        
	       logger.debug("######################## waiting_ver2. Bicycle_78 ");
	       logger.debug("RentWaitingRequestVer2 vo :::::::::::{} " , vo);
	        
	        
	        MessageHeader m = vo.getMessageHeader();
	        logger.debug(" MessageHeader m:: {}" , m);
	        
	        RentWaitingResponseVer2Vo responseVo = new RentWaitingResponseVer2Vo();
	       
	        responseVo.setLangCd("00");
	       
	        CommonVo com = new CommonVo();
	        com.setBicycleId(vo.getBicycleId());
	        com.setRockId(vo.getMountsId());
	        
	        
	        batteryInfoProc(vo);   // 대여대기를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
	        
	        
	        // 자전거 상태 값 이상
	      
	        
	        
	       //운휴기간 여부 확인.
	        
	        
	             
	        
	        
	        boolean chkDelayTime = commonService.chkDelayTime(com);
	        boolean chkUseStation = false;
	        if(chkDelayTime){
	           // 이용중지 대여소 여부 확인
	           
	           if(vo.getReturnForm().equals(Constants.CODE.get("RETURN_LOCK_01"))){
	              Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
	              chkUseStation = commonService.chkUseStationByRockId(String.valueOf(rackInfo.get("RETURN_RACK_ID")));
	              
	              logger.debug("##### CASCADE 거치 자전거정보 ##### => 거치대 역할 자전거 : " + String.valueOf(com.getRockId()) + ", 거치대 ID 추출 : " + String.valueOf(rackInfo.get("RETURN_RACK_ID")));
	           }else{
	              chkUseStation = commonService.chkUseStation(com);
	           }
	           
	           
	           if(chkUseStation){
	              //서비스 이용시간 내 대여요청 여부 확인
	              if(!commonService.chkUseTime(com)){
	                 logger.error("서비스 이용시간 초과");
	                      responseVo.setErrorId(Constants.CODE.get("ERROR_CE"));
	                      responseVo = setFaiiMsgVer2(responseVo, vo);
	                      
	                      return responseVo;
	              }
	           }else{
	              logger.error("이용중지 대여소 내 대여불가");
	                responseVo.setErrorId(Constants.CODE.get("ERROR_CD"));
	                responseVo = setFaiiMsgVer2(responseVo, vo);
	                
	                return responseVo;
	           }
	        }else{
	           logger.error("운휴기간 내 대여불가");
	          responseVo.setErrorId(Constants.CODE.get("ERROR_CF"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	        }
	        
	        
	        // 해당하는 자전거 정보가 없는 경우
	       Map<String, Object> deviceInfo = commonService.checkBicycle(com);
	       if(deviceInfo == null){
	          logger.error("INVALID 자전거 ID ");
	          responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	       }
	          
	       logger.debug("deviceInfo : {}" ,deviceInfo);
	       
	       //bikeService.updateLockTEst(com);
	       
	      // Cascade 상태(거치된 자전거 중 맨 끝 확인)
	       if(!commonService.isLastCascade(com)){
	          logger.error("Cascade 대여오류" );
	          responseVo.setErrorId(Constants.CODE.get("ERROR_E8"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	       }
	       
	      // Cascade 확인
	       if(vo.getReturnForm().equals(Constants.CODE.get("RETURN_LOCK_01"))){
	          
	         
	           Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
	           
	           if(rackInfo == null){
	              logger.error("CASCADE 반납거치대 확인실패");
	              responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
	              responseVo = setFaiiMsgVer2(responseVo, vo);
	               
	               return responseVo;
	           }
	           
	           //조회된 거치대 ID로 거치대 정보 등록.
	           com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
	         }
	             
	       
	       // 해당하는 거치대 정보가 없는 경우
	       Map<String, Object> mountInfo = commonService.checkMount(com);
	       if(mountInfo == null){
	          logger.error("INVALID 거치대 ID ");
	          responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	       }
	       
	       
	       // 자전거와 거치대 정보가 일치하지 않으면 잘못된 정보로 처리
	       Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
	       if(parkingMap == null){
	          logger.error("INVALID 자전거 ID & INVALID 거치대 ID ");
	          responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	       }
	       
	       com.setBikeId(String.valueOf(parkingMap.get("BIKE_ID")));
	        
	           
	       // 고장 여부 확인
	       if(commonService.checkBreakDown(com) > 0){
	          logger.error("자전거 고장" );
	          responseVo.setErrorId(Constants.CODE.get("ERROR_E9"));
	          responseVo = setFaiiMsgVer2(responseVo, vo);
	          
	          return responseVo;
	       }
	       
	       
	           
	        
	        
	        // 대여대기(예약) 확인
	        Map<String, Object>  reservation = commonService.reservationCheck(com);
	        
	        if(reservation == null){
	           responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_00"));   // 대여대기 없음
	           responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 카드 등록 진행 안함
	           
	           // 임시 예약 여부 확인
	           Map<String, Object>  temp = commonService.tempReservationCheck(com);
	           if(temp == null){
	              // 거치대 임시 예약 저장
	              //commonService.tempReservation(com);
	           }else{
	              responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_01"));   // 대여대기 있음
	              responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 카드 등록 진행 안함
	           }
	           
	           
	           
	        }else{
	           responseVo.setRentWait(Constants.CODE.get("RENT_WAIT_01"));   // 대여대기 있음
	          
	           responseVo.setLangCd(Constants.CODE.get(reservation.get("LANG_CLS_CD")));
	           if(reservation.get("TERMINAL_CARD_REG_YN") == null){
	              responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 진행 안함
	           }else{
	              if(reservation.get("TERMINAL_CARD_REG_YN").equals("Y")){
	                 responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_01"));   // 카드 등록
	              }else{
	                 responseVo.setUserCardRegist(Constants.CODE.get("INPUT_CARD_00"));  // 진행 안함
	              }
	           }
	        }
	        
	        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
	        responseVo.setSeqNum(vo.getSeqNum());
	        responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
	        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
	        responseVo.setBicycleId(vo.getBicycleId());
	        responseVo.setPassword("1234");
	        
	        
	        return responseVo;
	    }
*/
	// 대여대기 실패 메세지
    public RentWaitingResponseVo setFaiiMsg(RentWaitingResponseVo responseVo, RentWaitingRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
    	
    	return responseVo;
    }
    
    /*
    public RentWaitingResponseVer2Vo setFaiiMsgVer2(RentWaitingResponseVer2Vo responseVo, RentWaitingRequestVo vo ){
        
        responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
        responseVo.setBicycleId(vo.getBicycleId());
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
        
        return responseVo;
     }
    */
    
    
    // 대여요청 	 4370 
    @RPCService(serviceId = "Bicycle_02", serviceName = "대여 Request", description = "대여 Request")
    public RentalResponseVo rentRequest(RentalRequestVo vo) {
        
    	System.out.println("######################## Rent_Request_Bicycle_02");		//수정함..
        System.out.println("RentalResponseVo  vo :" + vo);
        
        MessageHeader m = vo.getMessageHeader();
        System.out.println(" MessageHeader m::" + m);
        
        RentalResponseVo responseVo = new RentalResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setRockId(vo.getMountsId());
        
        String pass = String.valueOf(Integer.parseInt( vo.getRentPassword().substring(0,2) , 16)) + String.valueOf(Integer.parseInt( vo.getRentPassword().substring(2,4) , 16))+ String.valueOf(Integer.parseInt( vo.getRentPassword().substring(4,6) , 16)) +String.valueOf(Integer.parseInt( vo.getRentPassword().substring(6,8) , 16));
        com.setPassword(pass);
        
        
        /**
     	 * 대여를 통한 자전거 배터리 정보 UPDATE_20170208_JJH
     	 */
     	
     	if(!vo.getBattery().equals("") && !vo.getBattery().equals(null))
     	{
     		logger.debug("##### RentalResponseVo  updateBatteryInfo start #####");	//수정함.
     		
     		PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
     		periodicStateReportsRequestVo.setBattery(vo.getBattery());
     		periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
     		
     		commonService.updateBatteryInfo(periodicStateReportsRequestVo);
     		
     		logger.debug("##### RentalResponseVo updateBatteryInfo end #####");
     	}
        
        // 자전거 상태 값 이상
    	/*if(!vo.getBicycleState().equals(Constants.CODE.get("BIKE_STATE_07"))){
    		System.out.println("INVALID 자전거 ID 자전거 상태값 이상" );
    		responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
    		responseVo = setFaiiMsg(responseVo, vo);
    		
    		return responseVo;
    	}
    	*/
        
        // Cascade 상태(거치된 자전거 중 맨 끝 확인) 거치 상태와는 상관없이 체크사항.
         if(!commonService.isLastCascade(com)){
			 logger.error("Cascade 대여오류" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E8"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
	    // Cascade 확인
		if(vo.getReturnForm().equals(Constants.CODE.get("RETURN_LOCK_01"))){
			 
			 
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
	  		
	  		//조회된 거치대 ID로 거치대 정보 등록.
	  		com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
	  	} 
		
		
		// 대여 정보 
		Map<String, Object> rentInfo = commonService.reservationCheck(com);
		
		if(rentInfo == null){
			System.out.println("대여정보 없음 예약 확인 실패" );
    		responseVo.setErrorId(Constants.CODE.get("ERROR_EF"));
    		responseVo = setFaiiMsg(responseVo, vo);
    		
    		return responseVo;
		}
		
		// 대여정보 업데이트
		if(!bikeService.rentProcUpdate(com, rentInfo)){
		//	System.out.println("유효한 이용권 없음");
			logger.error("rentProcUpdate: invalid voucher:ERROR_E5" );
			
			responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
			responseVo = setFaiiMsg(responseVo, vo);
    		
			return responseVo;
		}else{
			/**
			 * 대여정보가 정상적으로 저장된 경우, SMS발송
			 */
			com.setStationId(String.valueOf(rentInfo.get("RENT_STATION_ID")));
			com.setUserSeq(String.valueOf(rentInfo.get("USR_SEQ")));
			Map<String, Object> msgInfo = bikeService.getRentMsgInfo(com);
			
			SmsMessageVO sms = new SmsMessageVO();
			sms.setTitle("대여안내");
			sms.setType("S");
			
			//String destno = (String)msgInfo.get("DEST_NO");
			String destno = String.valueOf(msgInfo.get("DEST_NO"));
			if(destno != null && !destno.equals(""))
			{
				//T-APP PATCH
				if(msgInfo.get("RENT_MTH_CD") == null || msgInfo.get("RENT_MTH_CD").equals("") || msgInfo.get("RENT_MTH_CD").equals("CHN_001"))
				{
					sms.setDestno(destno);
					SmsSender.sender(sms, SendType.SMS_083, 	//SMS_001 -> SMS_084
						String.valueOf(msgInfo.get("BIKE_NO")),
						String.valueOf(msgInfo.get("STATION_NAME")),
						String.valueOf(msgInfo.get("HOUR")),
						String.valueOf(msgInfo.get("MINUTES")));
				}
				else if(msgInfo.get("RENT_MTH_CD").equals("CHN_002"))
				{ 
					//T-APP MESSAGE SEND  2019.12.05
					TAPPMessageVO TAPPVo = new TAPPMessageVO();
					TAPPVo.setUsr_seq(String.valueOf(rentInfo.get("USR_SEQ")));
					TAPPVo.setBike_no(String.valueOf(msgInfo.get("BIKE_NO")));
					TAPPVo.setStation_name(String.valueOf(msgInfo.get("STATION_NAME")));
					TAPPVo.setNotice_se(SendType.SMS_001.getCode());
					TAPPVo.setMsg(SendType.SMS_130, 
							String.valueOf(msgInfo.get("BIKE_NO")),
							String.valueOf(msgInfo.get("STATION_NAME")),
							String.valueOf(msgInfo.get("HOUR")),
							String.valueOf(msgInfo.get("MINUTES")));
					SmsSender.TAPPsender(TAPPVo);
				}
			}
			
			logger.debug("##### 자전거 대여 완료 후 마지막 점검시간 최신화 ##### => " + vo.getBicycleId());
			bikeService.setLastChkTime(vo);
			
			
			conditonPeriodProc(vo);	// 시간 조건 별 주기적인 상태보고 시간 업데이트_20161220_JJH
			
			String tmpLangCd = bikeService.getLanguageCode(com);
			if(tmpLangCd != null){
				int n_LangCd = Integer.parseInt(tmpLangCd.substring(tmpLangCd.length()-1, tmpLangCd.length()));
				
				logger.debug("##### victekTEST ==> " + n_LangCd + ", " + String.valueOf(n_LangCd));
				
				String langCd = "";
				
				switch(n_LangCd) {
				case 1 : langCd = "00";
						  break;
				case 2 : langCd = "01";
				  		  break;
				case 3 : langCd = "02";
				  		  break;
				case 4 : langCd = "03";
				  		  break;
				default : langCd = "00";
						   break;
				}	
				
				logger.debug("##### 대여 : 언어코드 ##### ==> " + langCd + ", 자전거 ID : " + com.getBicycleId());
				responseVo.setLangCode(langCd);
			}else{
				logger.debug("##### 대여 : 언어코드 is null #####" + ", 자전거 ID : " + com.getBicycleId());
				responseVo.setLangCode("00");
			}
	
		}
		 
        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_RENTCOMPLETE);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }




	/**
	 * @param vo
	 */
	private void conditonPeriodProc(RentalRequestVo vo) {
		// 대여 상태시간 HISTORY INSERT_20161220_JJH_START
		System.out.println("######################## 대여 상태시간 History Start ########################");
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
		
		System.out.println("######################## 대여 실시간 확인 ==> " + String.valueOf(sCurTime));
		
		if(sCurTime.compareTo(sMinTime) >= 0 && sCurTime.compareTo(sMaxTime) < 0){
			System.out.println("######################## 대여시간이 22:00 ~ 07:00 사이임 ==> " + String.valueOf(sCurTime));
			bikeService.insertPeriodInfo(vo);
		}else{
			System.out.println("######################## 대여시간이 조건 밖임~!! ==> ");
		}
		
		System.out.println("######################## 대여 상태시간 History End ########################");
		// 대여 상태시간 HISTORY INSERT_20161220_JJH_END
	}
    

    // 대여 실패 메세지
    public RentalResponseVo setFaiiMsg(RentalResponseVo responseVo, RentalRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RENTCOMPLETE);
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	return responseVo;
    }
    
    
    
    
    
    // 대여 취소 요청
    @RPCService(serviceId = "Bicycle_08", serviceName = "대여 취소 Request", description = "대여 취소 Request")
    public RentalCancleResponseVo rentCancleRequest(RentalCancleRequestVo  vo) {
    	
    	System.out.println("RentalCancleResponseVo_Bicycle_08  vo :" + vo);
    	
    	MessageHeader m = vo.getMessageHeader();
    	System.out.println(" MessageHeader m::" + m);
    	
    	RentalCancleResponseVo responseVo = new RentalCancleResponseVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	com.setRockId(vo.getMountsId());
    	
    	
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RENTCANCEL);
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    
    // 대여 취소 실패 메세지
    public RentalCancleResponseVo setFaiiMsg(RentalCancleResponseVo responseVo, RentalCancleRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RENTCANCEL);
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	return responseVo;
    }

}
