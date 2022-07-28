package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.ReturnRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.ReturnResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "반납")
@Service
public class ReturnService  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	BicycleRentService bikeService;

    // 반납 
    @RPCService(serviceId = "Bicycle_03", serviceName = "반납 Request", description = "반납 Request")
    public ReturnResponseVo waiting(ReturnRequestVo vo) {
    	
    	logger.debug("################### ReturnResponseVo_Bicycle_03");	//수정함...
    	logger.debug("ReturnWaitingRequestVo vo : {}" , vo);				//수정함...
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        ReturnResponseVo responseVo = new ReturnResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setRockId(vo.getMountsId());
        
        
        // 자전거 대여정보 확인
        RentHistVo info = bikeService.getForReturnUse(com);	//USE_MI
        
               
        int overPay = 0;
        
        
        if(info != null)
        {
        	
        	//거지대 정보 등록.
        	if(vo.getReturnForm().equals("01")){
        		
        		/**
        		 * Cascade 거치된 거치대정보 조회
        		 */
        		Map<String, Object> rackInfo = bikeService.selectCascadParkingRock(com);
        		
        		if(rackInfo == null){
        			logger.error("CASCADE selectCascadParkingRock failed 반납거치대 확인실패");
        			// responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));	CASCADE 반납거치 확인실패 정정_20170518_JJH
        			responseVo.setErrorId(Constants.CODE.get("ERROR_E2"));
        			responseVo = setFaiiMsg(responseVo, vo);
        			 
        			 return responseVo;
        		}
        		
        		//조회된 거치대 ID로 거치대 정보 등록.
        		com.setRockId((String)rackInfo.get("RETURN_RACK_ID"));
        		info.setRETURN_RACK_ID((String)rackInfo.get("RETURN_RACK_ID"));
        		
        	}else{
        		info.setRETURN_RACK_ID(vo.getMountsId());
        	}
        	
        	//반납가능한 거치대를 확인하기 위해, stationId를 조회함
        	//대여소 정보 조회
         	/**
     		 * Cascade 거치된 거치대정보 조회
     		 */
     		Map<String, Object> stationInfo = commonService.checkMount(com);	//SELECT * FROM TB_OPR_RACK WHERE RACK_ID = ? 
     		
     		String stationId = null;
     		try {
     			stationId = stationInfo.get("NOW_LOCATE_ID").toString();
     		}catch(Exception e){
     			
     		}
     		
     		if(stationId == null){
     			logger.error("return StationId_IS_NULL 반납거치대 확인실패");
     			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));
     			responseVo = setFaiiMsg(responseVo, vo);
     			 
     			return responseVo;
     		}else{
     			com.setStationId(stationId);
     			info.setRETURN_STATION_ID(stationId);
            	
     		}
  	// 케스케이드 반납 확인
        	if(vo.getReturnForm().equals("01")){
//        		if(bikeService.getNoParkingRock(com) > 0){
//        			logger.error("반납 가능한 거치대 있음 ");
//                	responseVo.setErrorId(Constants.CODE.get("ERROR_E2"));
//                	responseVo = setFaiiMsg(responseVo, vo);
//                	
//                	return responseVo;
//        		}
        		
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
//        	if(vo.getErrorId().equals("E4")){
//        		// 충전상태 이상 UPdate
//        		commonService.updateBatteryDischarge(com);
//        	}
        	
        	
        	
        	//int overTime = Integer.parseInt(info.getUSE_MI().toString());
        	int overTime = Integer.parseInt(info.getUSE_MI().toString());
        	// 사용시간
        	int hour = Integer.parseInt(vo.getRentTime().substring(0,2), 16);
        	int min = Integer.parseInt(vo.getRentTime().substring(2,4), 16);
        	int useMin = (hour*60)+min;
        	
        	logger.debug("return_use_time :::::::::  {} hour {} min , total : {} sysuse : {}"  , hour , min , useMin,overTime);
        	
        	// 이동거리
        	int km = Integer.parseInt(vo.getDistance().substring(0,2), 16);
        	int me = Integer.parseInt(vo.getDistance().substring(2,4), 16);
        	int distance = (km*1000)+(me*10);
        	
        	logger.debug("return_use_distance :::::::::  {} km {} m , total : {}"  , km , me , distance);
        	
        	
        	info.setRETURN_STATION_ID(com.getStationId());
        	info.setTRANSFER_YN("N");
        	info.setOVER_FEE_YN("N");
        	info.setUSE_DIST(distance+"");
        	info.setSYSTEM_MI(info.getUSE_MI());
        	info.setUSE_MI(useMin+"");
        	
        	int weight = bikeService.getUserWeight(info.getUSR_SEQ());
        	
        	double co2 = (((double)distance/1000)*0.232);
        	double cal = 5.94 * (weight==0?65:weight) *((double)distance/1000) / 15;
        	
        	
        	info.setREDUCE_CO2(co2+"");
        	info.setCONSUME_CAL(cal+"");
        	
        	
        	// 자전거 기본 대여시간 분
        	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH_START
        	/* 
        	com.setComCd("MSI_011");
        	Map<String, Object> baseRent = commonService.getComCd(com);
        	int baseRentTime = Integer.parseInt(baseRent.get("ADD_VAL1").toString());
        	*/
        	//프리미엄 이용권 초과요금 적용 시간 가져오기
        	int baseRentTime = Integer.parseInt((String)commonService.getBaseTime(info).get("BASE_TIME"));
        	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH_END
        	
        	//info.setSYSTEM_MI(overTime);
        	// 초과 요금 대상
        //	if(useMin > baseRentTime)
        	if(useMin >= overTime)	//단말기 시간이 시스템 시간보다 크거나 같은때는 시스템 시간
        	{
        		if(overTime > baseRentTime)
        		{
        			logger.debug(" ##### device_time over_time BUT server_time is baseRentTime useMin {} overTime {}"  , useMin , overTime );
        		
        		
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
        			
        			Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee);	//TB_SVC_ADD_FEE  
        			Map<String, Object> maxPolicy = bikeService.getOverFeeMaxPolicy(fee);
        		
        			overPay = new CommonUtil().getPay(minPolicy, maxPolicy, overTime);
        		
        		//	if(!(info.getUSR_CLS_CD().equals("USR_002")))
	        //		{
        				info.setOVER_FEE_YN("Y");
        				info.setOVER_FEE(overPay+"");
        				info.setOVER_MI(String.valueOf(overTime-baseRentTime));
	        //		}
        		}
        		else
        		{
        			logger.debug(" ##### systime_time is baseRentTime {}", overTime );
        		}
        	}
        	else	//서버시간이 더 많음...
        	{
        		if(useMin > baseRentTime)
        		{
        			logger.debug(" ##### server_time over_time BUT device_time is baseRentTime useMin {} overTime {}"  , useMin , overTime );
            		
            		
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
        			Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee);	//TB_SVC_ADD_FEE  
        			Map<String, Object> maxPolicy = bikeService.getOverFeeMaxPolicy(fee);
        		
        			overPay = new CommonUtil().getPay(minPolicy, maxPolicy, useMin);
        		
        			//if(!(info.getUSR_CLS_CD().equals("USR_002")))
	        	//	{
        				info.setOVER_FEE_YN("Y");
        				info.setOVER_FEE(overPay+"");
        				info.setOVER_MI(String.valueOf(useMin-baseRentTime));
	        	//	}
        			
        		}
        		else
        		{
        			logger.debug(" ##### useMin is baseRentTime {}", useMin );
        		}
        		
        	}
        	
        	// 반납 프로세스 실행
        	bikeService.procReturn(info);	//BicycleRentServiceImpl.java 의 함수 호출 후 DB 처리  
        	
        }else{
        	logger.error("NO RETURN_RENT_HISTORY BIKE_ID {}",vo.getBicycleId());	//2018.03.26  로그 수정함.
        	
        	responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
        	responseVo = setFaiiMsg(responseVo, vo);
        	
        	return responseVo;
        }
        
        //TODO:반납 push
        //app 설치 사용자 한정.
        if(info.getUSR_DEVICE_TYPE() != null) {
       
        	//this.rentSuccessPushMsg(pushVo);
        }
        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_RETURNBIKE);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }
    
    
    


	/*private void rentSuccessPushMsg(PushTargetVO pushVo) {
		
		if(pushVo.getUsrDeviceType().equals(IConstants.PUSH_TYPE_GCM)) {
			
		} else {
			
			
		}
		
	}*/





	// 반납 실패 메세지
    public ReturnResponseVo setFaiiMsg(ReturnResponseVo responseVo, ReturnRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RETURNBIKE);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
    	
    	return responseVo;
    }
    
    
    
}
