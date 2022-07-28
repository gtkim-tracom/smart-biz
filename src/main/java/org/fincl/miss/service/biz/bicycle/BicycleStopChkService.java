package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.BicycleStopChkRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BicycleStopChkResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "정차 자전거 확인")
@Service
public class BicycleStopChkService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonService commonService;
	
	
	
	// 자전거 정차 자동확인 
	 @RPCService(serviceId = "Bicycle_16", serviceName = "정차 자전거 자동확인 Request", description = "정차 자전거 자동확인 Request")
	 public BicycleStopChkResponseVo bicycleStopChkProc(BicycleStopChkRequestVo vo) {
		 
		 logger.debug("######################## serviceId Bicycle_16");
		 logger.debug("BicycleStopChkRequestVo vo :::::::::::{} " , vo);
		 
		 
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 
		 BicycleStopChkResponseVo responseVo = new BicycleStopChkResponseVo();
		 
		 Map<String, Object> deviceInfo = commonService.checkBicycle(com);
		 
		 if(deviceInfo == null){
			 logger.error("##### 정차 자전거 자동확인 : INVALID 자전거 ID #####");
			 
			 responseVo.setErrorId(Constants.CODE.get("ERROR_CB"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }else{
			 if(commonService.bicycleStopChkProc(vo)){		// // 정차 자전거 자동확인 Proc_20170731_JJH
				 logger.error("##### 정차 자전거 자동확인 처리완료 #####");
				 
			 }else{
				 logger.error("##### 정차 자전거 자동확인 : 전송된 위/경도 정보가 없음 #####");
				 
				 responseVo.setErrorId(Constants.CODE.get("ERROR_CB"));	// 위/경도 정보 없음 Error_ID 정의필요_20170914_황동욱
				 responseVo = setFaiiMsg(responseVo, vo);
			 }
		 }
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_BICYCLESTOPCHK);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 
		 logger.debug("######################## 정차 자전거 자동확인 Response");
		 logger.debug("BicycleStopChkResponse :::::::::::{} " , responseVo);
		 
		 return responseVo;
		 
	 }
	 
	 // 자전거 정차 자동확인 실패 메세지
	 public BicycleStopChkResponseVo setFaiiMsg(BicycleStopChkResponseVo responseVo, BicycleStopChkRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_BICYCLESTOPCHK);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }

}
