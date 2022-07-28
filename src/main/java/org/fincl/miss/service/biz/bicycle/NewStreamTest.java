package org.fincl.miss.service.biz.bicycle;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

@RPCServiceGroup(serviceGroupName = "테스트")
@Service
public class NewStreamTest{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonService commonService;
	
	
	@Autowired
	private BicycleRentService bikeService;

	@Autowired
	private FileUpdateService fileService;
	
	
	
	// 주기적인 상태 보고 
	 @RPCService(serviceId = "Bicycle_77", serviceName = "Stream Test Request", description = "Stream Test Request")
	 public PeriodicStateReportsResponseVo StreamTest(PeriodicStateReportsRequestVo vo) {
		 
		 logger.debug("######################## Test Stream ");
		 logger.debug("Test Stream vo :::::::::::{} " , vo);
		 PeriodicStateReportsResponseVo responseVo = new PeriodicStateReportsResponseVo();
		 
		 
		 
		 return responseVo;
		 
	 }
}
