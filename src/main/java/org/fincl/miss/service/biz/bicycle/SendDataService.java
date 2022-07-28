package org.fincl.miss.service.biz.bicycle;

import java.util.ArrayList;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.GpsSendDataRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.GpsSendDataResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "데이터 전송")
@Service
public class SendDataService  {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	BicycleRentService bikeService;
	
	
	 // GPS 데이터 전송
    @RPCService(serviceId = "Bicycle_15", serviceName = "GPS 데이터 전송 Request", description = "GPS 데이터 전송 Request")
    public GpsSendDataResponseVo waiting(GpsSendDataRequestVo vo) {
    	
    	logger.debug("#################### GPS 데이터 전송");
    	logger.debug("RentWaitingRequestVo vo :" + vo);
    	
    	MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m::" + m);
        
        GpsSendDataResponseVo responseVo = new GpsSendDataResponseVo();
        
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setRockId(vo.getMountsId());
        
    	
    	//-------------- 시퀀스가 0보다 클 경우 이전에 처리된 내역이 있는 지 확인-------------//
    	if(0 < Integer.parseInt(vo.getSeqNum())){

    		String tmp = vo.getReqMessage().substring(0,4)+ "0" + (Integer.parseInt(vo.getSeqNum())-1) + vo.getReqMessage().substring(6);
    		com.setReqMsg(tmp);
    		logger.debug(tmp);
    		logger.debug(vo.getReqMessage());
    		
    		String res_msg = commonService.getResMessage(com);
    		
    		if(res_msg!=null && res_msg.length() > 0){	// 이전 처리 내역이 있다면 해당 메세지를 그대로 셋팅하고 돌려보낸다.
    			responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
    	        responseVo.setSeqNum(vo.getSeqNum());
    	        responseVo.setCommandId(Constants.CID_RES_SENDGPSDATA);
    	        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
    	        responseVo.setBicycleId(vo.getBicycleId());
    	        
    	        return responseVo;
    		}
    	}
    	
    	
        
        
    	ArrayList<Integer> lat =  new ArrayList<Integer>();
    	ArrayList<Integer> lon =  new ArrayList<Integer>();
    	
    	for(int i=0;i <vo.getGpsData().length;i++){
    		String gps = vo.getGpsData()[i];
    		if(!gps.equals("FFFFFFFFFFFFFFFF")){
    			try{
	    			lat.add(Integer.parseInt(gps.substring(0,8),16));
	    			lon.add(Integer.parseInt(gps.substring(8,16),16));
    			}catch(Exception e){
    				logger.error("Number Format Exception ::: {}",gps);
    			}
	    	}
    	}
    	/*
    	for (int i = 0; i < vo.getLatitude().length; i++) {
    		String latitude = vo.getLatitude()[i];
    		if(!latitude.equals("FFFFFFFF")){
	    		if(i%2 == 0)
	    			lat.add((Integer.parseInt(latitude, 16)));
	    		else
	    			lon.add( Integer.parseInt(latitude, 16));
    		}
		}
    	
    	for (int i = 0; i < vo.getLongitude().length; i++) {
    		String logitude = vo.getLongitude()[i];
    		if(!logitude.equals("FFFFFFFF")){
	    		if(i%2 == 0)
	    			lat.add((Integer.parseInt(vo.getLongitude()[i], 16)));
	    		else
	    			lon.add( Integer.parseInt(vo.getLongitude()[i], 16));
    		}
    	}
    	*/

        
        Map<String, Object> hist = bikeService.getRentHist(com);
        
        System.out.println(hist);
        
        if(hist == null){
        	logger.error("INVALID 자전거 ID & INVALID 거치대 ID ");
        	responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
        	responseVo = setFaiiMsg(responseVo, vo);
        	
        	return responseVo;
        }else{
        	int packNum = Integer.parseInt(vo.getPacketNumber(),16);
        	try{
        		bikeService.insertRentMoveInfo(hist, lat, lon, packNum);
        	}catch(Exception e){}
        }
        
        
        responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_SENDGPSDATA);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }
    
    
    
    // gps 데이터 전송 실패 메세지
    public GpsSendDataResponseVo setFaiiMsg(GpsSendDataResponseVo responseVo, GpsSendDataRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_DATA_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_SENDGPSDATA);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
    	
    	return responseVo;
    }

}
