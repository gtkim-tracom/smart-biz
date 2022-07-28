package org.fincl.miss.service.biz.bicycle;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.vo.GPSSendRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.GPSSendResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "GPS")
@Service
public class GPSService {
    
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @RPCService(serviceId = "Bicycle_31", serviceName = "GPS 데이터 전송", description = "GPS 데이터 전송")
    public GPSSendResponseVo auth(GPSSendRequestVo vo) {
        
        logger.debug("mk:nw length : {}" , vo.getNw().length);
        for (String nw : vo.getNw()) {
            logger.debug("mk:nw  {} " , nw);
        }
        logger.debug("mk:es length : {} " , vo.getEs().length);
        for (String es : vo.getEs()) {
            logger.debug("mk:es  {} " , es);
        }
        
        GPSSendResponseVo aa = new GPSSendResponseVo();
        
        return aa;
    }
}
