package org.fincl.miss.service.biz.bicycle;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.TheftReportRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.TheftReportResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "보고")
@Service
public class ReportService  {


	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommonService commonService;
	
    // 도난 보고
    @RPCService(serviceId = "Bicycle_07", serviceName = "도난보고 Request", description = "도난보고 Request")
    public TheftReportResponseVo waiting(TheftReportRequestVo vo) {
        
        logger.debug("TheftReportResponseVo vo : {}" , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        
        commonService.theftReport(vo);
        
        TheftReportResponseVo responseVo = new TheftReportResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        
        	
        	
        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_LOSTWARNING);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }
    
    
    // 도난 보고  메세지
    public TheftReportResponseVo setFaiiMsg(TheftReportResponseVo responseVo, TheftReportRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_LOSTWARNING);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
    	
    	return responseVo;
    }
    
    
	
	
}
