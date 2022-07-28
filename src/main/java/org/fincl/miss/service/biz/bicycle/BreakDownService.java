package org.fincl.miss.service.biz.bicycle;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.CheckBreakdownReportRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.CheckBreakdownReportResponseVo;
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
	
	
	// 점검/ 고장신고
	 @RPCService(serviceId = "Bicycle_06", serviceName = "점검/고장신고 Request", description = "점검/고장신고 Request")
	 public CheckBreakdownReportResponseVo adminMove(CheckBreakdownReportRequestVo vo) {
		 
		 logger.debug("#################### 고장신고");
		 logger.debug("CheckBreakdownReportRequestVo vo :::::::::::{}" , vo);
		 
		 CheckBreakdownReportResponseVo responseVo = new CheckBreakdownReportResponseVo();
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setRockId(vo.getMountsId());
		 
		 if(vo.getBrokenType().equals("00")){
			 // 점검 완료 
			 // 자전거 상태 업데이트
			 com.setBikeStusCd("BKS_003");
			 commonService.updateCheckBike(com);
			 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
			 
		 }else{
			 // 고장 신고
			 // 자전거 고장 상태로 업데이트
			 com.setBikeStusCd("BKS_001");
			 com.setBikeId(vo.getBicycleId());
			 StringBuffer sb = new StringBuffer();
			 sb.append("ELB_0").append(vo.getBrokenType());
			 com.setBikeBrokenCd(sb.toString());
			 commonService.updateBrokenBike(com);
			 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
			 
		 }
		 
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_JAMDECLARATION);
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 점검 고장신고 실패
	 public CheckBreakdownReportResponseVo setFaiiMsg(CheckBreakdownReportResponseVo responseVo, CheckBreakdownReportRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setFrameControl(Constants.CID_RES_JAMDECLARATION);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }

}
