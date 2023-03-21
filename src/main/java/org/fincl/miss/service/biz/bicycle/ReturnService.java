package org.fincl.miss.service.biz.bicycle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentMapper;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.QRReturnResultRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.QRReturnDistResultRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.QRReturnResponseVo;
import org.fincl.miss.service.util.NaverGPSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import	com.google.gson.Gson;

@RPCServiceGroup(serviceGroupName = "반납")
@Service
public class ReturnService  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	BicycleRentService bikeService;
	
	@Autowired
    private BicycleRentMapper bicycleMapper;

    // 반납 
    @RPCService(serviceId = "Bicycle_03", serviceName = "반납 Request", description = "반납 Request")
    public QRReturnResponseVo waiting(QRReturnResultRequestVo vo)
    {
    	//double latitude, longitude = 0.0d;
    								  
    	logger.debug("################### QR BIKE_Return_RESULT_SUCCESS");
    	logger.debug("QRReturnResultRequestVo vo : {}" , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        QRReturnResponseVo responseVo = new QRReturnResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setBikeId(vo.getBicycleId());
        //MOUNT ID 가 어떻게 될지 몰라 비워둠
        com.setRockId(vo.getBeaconId());	//거치대 번호
       
        Map<String, String> ourBikeMap = new HashMap<String, String>();
        ourBikeMap = bikeService.chkOurBike(com);	//ENTRPS_CD : ENT_001(vick) , ENT_002(witcom)  , ENT_003(atech) ,BIKE_SE_CD
		
        boolean stationYn=false;
        boolean	gpsYn = false;
        String Data_Result =null;
        
        String	 nBikeSerial;
        QRLogVo QRLog = new QRLogVo();
        if(ourBikeMap != null)
        {
			//add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
		 	nBikeSerial = bikeNo.substring(2,bikeNo.length());
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		// 	com.setBikeStusCd(bike_SE_CD);
		 	
		 	logger.debug("QR_436F ##### => bike {} ,state {} , company {} ,bike_se_cd {}  ",vo.getBicycleId(),vo.getBicycleState(),com.getCompany_cd(),BIKE_SE_CD);
		 	
		 	//QRLogVo QRLog = new QRLogVo();
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 	QRLog.setBeaconid(vo.getBeaconId());
		 	QRLog.setLock(vo.getLockState());
		 	
		 	QRLog.setBiketype(vo.getElecbicycle());
		 	
		 	QRLog.setDev_BATT(String.valueOf(Integer.parseInt(vo.getBattery(), 16)));
		 	QRLog.setBeacon_BATT(String.valueOf(Integer.parseInt(vo.getBeaconBattery(), 16)));
		 	QRLog.setBike_BATT(String.valueOf(Integer.parseInt(vo.getElectbatt(), 16)));
		 	
		 	if(!vo.getLatitude().equals("00000000") && !vo.getLatitude().substring(0,6).equals("FFFFFF")
					 && !vo.getLongitude().equals("00000000") && !vo.getLongitude().subSequence(0, 6).equals("FFFFFF"))
		 	{
		 		QRLog.setXpos(new CommonUtil().GetGPS(vo.getLatitude()));
		 		QRLog.setYpos(new CommonUtil().GetGPS(vo.getLongitude()));
		 		
		 		gpsYn = true;
		 		String NAVER_API_KEY ;
			    String NAVER_SECRET_KEY;	

     			//zgyaca9y6t    , JbaXg9LvYDkHHPmeGi2r0DsL20KhhvOIkfJMfEOd
     			// 양재영 과장  : a88veypaz2 , kg5bLoJkxE1KcFUURqgjdyf5x0ztBMKRY46y4Med
     			NAVER_API_KEY 			= "fz4629oc58";     			// 가맹점 코드
     			NAVER_SECRET_KEY			= "UabPltufcHwQtbpmaiSaM94YGXMmayjbCpxSV33E";  // billing Key
     			
     			ObjectMapper mapper = new ObjectMapper(); 		  			//jackson json object
     			NaverGPSUtil util = new NaverGPSUtil();  
     			

     			String XPos = new CommonUtil().GetGPS(vo.getLatitude());
     			String YPos = new CommonUtil().GetGPS(vo.getLongitude());
     			
     			
     			  			
     			String info_String = "coords="+ YPos + "," + XPos +"&output=json&orders=legalcode";
     			
     			logger.debug("NAVER API_BASIC " + info_String);
     			
     			
     			String info_result = util.GPSSend(info_String, NAVER_API_KEY, NAVER_SECRET_KEY);
     			
     			String  Data_Detail = null;
     			
     			JsonNode node;
     			try 
     			{	
     				node = mapper.readTree(info_result);
     				logger.debug("KRRI_GPS CHECK POINT 1 " + info_result);
     				if(node.has("results"))
     				{
     					ArrayNode  memberArray = (ArrayNode) node.get("results");
     					
     					if(memberArray.isArray())
     					{
     						for(JsonNode jsonNode : memberArray)
     						{
     							logger.debug("KRRI NODE " + jsonNode);
     							
     							logger.debug("KRRI GPS CHECK POINT 2 = " + jsonNode.get("region").get("area2").get("name").asText().toString());
     							Data_Result = jsonNode.get("region").get("area2").get("name").asText().toString();
     							Data_Detail = jsonNode.get("region").get("area3").get("name").asText().toString();
     						}
     					}
     					//logger.debug("NAVER GPS CHECK POINT 2 = " + jsonNode.get("region").get("area2").get("name").toString());
     				}
     				else 
     				{
     					logger.debug("KRRI_GPS CHECK POINT 3");
     				}
     			} 
     			catch (JsonProcessingException e) 
     			{
     				logger.debug("KRRI_GPS JsonProcessingException");
     			} 
     			catch (IOException e) 
     			{
     				logger.debug("KRRI_GPS IOException");
     			}
     			catch (Exception e)
     			{
     				logger.debug("KRRI_GPS Exception");
     				e.printStackTrace();
     			}
     			
     			//봉화군		창원시 마산회원구 
     		//	if(!Data_Result.equals("여수시")|| Data_Result.length() > 0)	//데이타 있음 성공
     			if(Data_Result.equals("여수시")|| Data_Result.equals("봉화군") || Data_Result.equals("창원시 마산회원구"))	//데이타 있음 성공
     			{
     				/*Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
    		 		UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS02");
    		 		UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
    		 		bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);*/
     				stationYn = true;
     				
     				logger.debug("KRRI_GPS FIND AREA : {} !!!!! Don't not update F_GPS02 !!!!!!!! ", Data_Detail);
     			}
     			else if(Data_Result.length() > 0)	//서울 이면서 
     			{
     				//서비스 지역이 아닐때 반납 불가 하도록 추가 !!!!!!
     				if(vo.getBeaconId().equals("0000000000"))	//거치대 정보 가 없을때 !!!
     				{
	     				Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
	     				 
	     				UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS02");
	    		 		UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
	    		 		
	     				logger.debug("KRRI_GPS NO_SERVICE_AREA {}!!!!!  update F_GPS02 !!!!!!!! ",Data_Detail);
	     				bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);
	     				
	     				 
     				}
     			}
		 	}
		 	else			//GPS 정보 없음...
            {
		 		
		 	//	02181D1B 07A69BCD 	
		 	//	QRLog.setXpos(vo.getLatitude());
		 	//	QRLog.setYpos(vo.getLongitude());    
		 		QRLog.setXpos("00000000");
		 		QRLog.setYpos("00000000");
		 		
		 		/* GPS 없을때 F_GPS01 을 막음
		 		if(vo.getBeaconId().equals("0000000000"))
		 		{	
		 			Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
		 			UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS01");
		 			UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
		 			bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);
		 		}
		 		*/
            }
		 	
		 	
		 	QRLog.setTimeStamp(vo.getRegDttm());
			QRLog.setMessage(vo.getReqMessage());
			QRLog.setQr_frame("반납 완료");
		 	
		 	bikeService.insertQRLog(QRLog);
		 	
        }
        else
        {
        	
        	//반납 시간 초기화 
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
			 
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
        }
        
        
        //밧데리....beacon 밧데리는 필요없음.
        if(!vo.getBattery().equals(null) && !vo.getBattery().equals("")
				&& !vo.getBeaconBattery().equals(null) && !vo.getBeaconBattery().equals(""))
		{
			logger.debug("##### BIKE RETURN ## BATTERY UPDATE START #####");
			
			int battery = Integer.parseInt(vo.getBattery(), 16);
     		String battery_info = null;
     		
     		battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
     		Map<String, String> battery_map = new HashMap<String, String>();
     		battery_map.put("BATTERY", String.valueOf(battery));
     		battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
     		battery_map.put("BICYCLE_ID", vo.getBicycleId());
     		
			commonService.updateBatteryInfo(battery_map);
			
			logger.debug("##### BIKE RETURN ## BATTERY UPDATE END #####");
		}
        //GPS
        if(gpsYn)
		{
			
        	String latitude = new CommonUtil().GetGPS(vo.getLatitude());
        	String longitude = new CommonUtil().GetGPS(vo.getLongitude());
			 
			//logger.debug("GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
			logger.debug("UPDATE GPS_INFO ##### => : {} , {} ",latitude,longitude);
			 
			RentHistVo gps = new RentHistVo();
			gps.setRETURN_RACK_ID(com.getRockId());
			gps.setRENT_BIKE_ID(vo.getBicycleId());
			gps.setGPS_X(latitude);
			gps.setGPS_Y(longitude);
			 
			bicycleMapper.updateBikeGPS(gps);
			 
		 }
        //
        
        // GPS 가 서비스 지역이 아닐때 반납 실패 (여수,분천,
        if(gpsYn && !stationYn && vo.getBeaconId().equals("0000000000"))	//서비스 지역이 아님 !!! gps 있으면서 스테이션은 타지역인경우 
        {
        		logger.error("!!!!!!!!!!!!!! GPS => NO_RETURN_PLACE {}!!!!!!!!!!!!!!!!",Data_Result);
        		
        		 QRLog.setResAck("NOAREA");
     	    	 bikeService.updateQRLog(QRLog);
	    		 
				com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
				com.setBikeBrokenCd("ELB_006");
				responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
				responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
				 
				responseVo = setFaiiMsg(responseVo, vo);
				 
				return responseVo;
              	
        }//GPS 있으면서 서비스 지역이고 ,반납 실패시 !!!
        else if(gpsYn && stationYn && vo.getBeaconId().equals("0000000000"))
        {
        	if(Data_Result.equals("여수시"))
        	{
        		//거리 체크 !!!!!
        		logger.debug("!!!!!!!!!!!!!! GPS => [YEOSU_PLACE] {} !!!!!!!!!!!!!!!!",Data_Result);
        		
        		//정거장 하고의 거리 체크
        //		info.setGPS_X(new CommonUtil().GetGPS(vo.getLatitude()));
	    //		info.setGPS_Y(new CommonUtil().GetGPS(vo.getLongitude()));
				String latitude = new CommonUtil().GetGPS(vo.getLatitude());
		        String longitude = new CommonUtil().GetGPS(vo.getLongitude());
				
			//	logger.debug("[GPS_CHECK] GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
				 
				Map<String, String> GPS = new HashMap<String, String>();
				GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
				GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
				GPS.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
				
				logger.debug("[GPS_CHECK] GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
				
				Map<String, Object> stinfo = null;
				//GPS.get("BIKE_LATITUDE") , GPS.get("BIKE_LONGITUDE") , GPS.get("BIKE_ID") 
				stinfo = commonService.CheckStation_ForGPS(GPS);
				
				if( Double.parseDouble(String.valueOf(stinfo.get("DISTANCE_DATA")))   > Double.parseDouble(String.valueOf(stinfo.get("DSTNC_LT"))))
				{
					
					logger.debug("[GPS_CHECK] RETURN is NO_STATION : Don't impossbile return bike : distance {} @@@@@@@@@@@@",String.valueOf(stinfo.get("DISTANCE_DATA")));
					 QRLog.setResAck("NODIST");
	     	    	 bikeService.updateQRLog(QRLog);
        		
					Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
					
					UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS02");
					UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
		 		
					logger.debug("KRRI_GPS_FARWARY_AREA {}!!!!!  update F_GPS02 !!!!!!!! ");
					bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);
 				
					com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
					com.setBikeBrokenCd("ELB_006");
					responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
					responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
				 
					responseVo = setFaiiMsg(responseVo, vo);
				 
					return responseVo;
				}
        	}
        	
        	
        }//GPS 없으면서 비콘이 없을경우 !!!!! 사용자 앱 GPS 로 판단 !!! 아래 부분에서 체크  rent_seq 필요함
  /*      else if(!gpsYn && vo.getBeaconId().equals("0000000000"))
        {
        	
        	
        	
        }*/
        
        
        
        
        
        // 자전거 대여정보 확인
        // 자전거 STATION 정보 죄회 수정 필요 BEACON ID로 
        RentHistVo info = bikeService.getForReturnUse(com);
        int overPay = 0;
        
        
        if(info != null)
        {	
    		Map<String, Object> stationInfo = null;
    		
    	
    		//헬멧 상태가 Y 이면 헬멧 있고 , N 이면 헬멧 없음 
    		logger.debug("KICK_RETURN ## KICK IS  KICK_DEVICE {} unit {} ,END_DTTM {} HELMET_STATUS {} , HELMET_YN {} , HELMNET_CNT {}",vo.getBicycleId(),vo.getBeaconId(),info.getEND_DTTM(),info.getHELMET_STATUS_YN(),info.getHELMET_YN(),info.getHELMET_CNT());

    		if(info.getEND_DTTM() == null || info.getEND_DTTM() =="")
    		{
    			logger.debug("@@@@@@@@ return_button is not touched !!!!!!!");
    			
    		}
    		else
    		{
    			logger.debug("return_button is usr touched !!!!!!!");
    		}
    		
    		//거치대 추가 함. 
    		if(!vo.getBeaconId().equals("0000000000")) //거치대 반납 
    		{
    			 stationInfo = commonService.checkMount(com);	//SELECT * FROM TB_OPR_RACK WHERE RACK_ID = ? 
    			
    			 if(stationInfo != null)
    			 {
    				
    				 
	    			 com.setStationId((String)stationInfo.get("NOW_LOCATE_ID"));
	         		 info.setRETURN_STATION_ID((String)stationInfo.get("NOW_LOCATE_ID"));
	         		 
	         		 info.setRETURN_RACK_ID(com.getRockId());
    			 }
    			 else	//거치대 ID 잘못올라와서 예외 처리 !!!
    			 {
    				 logger.debug("UNIT {} IS NOT MATTING_STATION : stationYn {} !!!!!!",vo.getBeaconId(),stationYn);
    				 
    				 if(stationYn)	//GPS 가 해당 스테이션으로 올라옴.
    				 {
    					 info.setGPS_X(new CommonUtil().GetGPS(vo.getLatitude()));
    			    	 info.setGPS_Y(new CommonUtil().GetGPS(vo.getLongitude()));
    					 String latitude = new CommonUtil().GetGPS(vo.getLatitude());
    				     String longitude = new CommonUtil().GetGPS(vo.getLongitude());
    						
    					 logger.debug("NO_UNIT GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
    						 
    					Map<String, String> GPS = new HashMap<String, String>();
    					GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
    					GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
    					GPS.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
    					stationInfo = commonService.CheckStation_ForGPS(GPS);
    						
    					 
    					String	dist_station,dstnc_lt,st_id;
    					st_id = (String)stationInfo.get("STATION_ID");
    					com.setRockId(stationInfo.get("RACK_ID").toString());
    					
    					info.setRETURN_STATION_ID((String)stationInfo.get("NOW_LOCATE_ID"));
    	         		 
    	         		info.setRETURN_RACK_ID(com.getRockId());
    					
    				 }
    				 else
    				 {
    					 QRLog.setResAck("NOUNIT");
    		     		 bikeService.updateQRLog(QRLog);
    		     			
    		     		 responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));	//invalid 비콘
    		    		 responseVo = setFaiiMsg(responseVo, vo);
    		    		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
    		    	  		
    		    		 return responseVo;
    					 
    				 }
    			 }
         		
    		}	//보관대 ID 있을대 
    		else if(!vo.getLatitude().equals("00000000") && !vo.getLatitude().substring(0,6).equals("FFFFFF")
					 && !vo.getLongitude().equals("00000000") && !vo.getLongitude().subSequence(0, 6).equals("FFFFFF"))
			{
				info.setGPS_X(new CommonUtil().GetGPS(vo.getLatitude()));
	    		info.setGPS_Y(new CommonUtil().GetGPS(vo.getLongitude()));
				String latitude = new CommonUtil().GetGPS(vo.getLatitude());
		        String longitude = new CommonUtil().GetGPS(vo.getLongitude());
				
				logger.debug("GPS INFO ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
				 
				Map<String, String> GPS = new HashMap<String, String>();
				GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
				GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
				GPS.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
				stationInfo = commonService.CheckStation_ForGPS(GPS);
				
				//거리 계산 !!!! 2022.11.02 추가 함.
				//정거장 번위 보다 작으면 반납 가능으로 처리...
				//GPS 로 계산 된 거리가   정거장에 정의돈  거리 안에 있으면 
				
				 if( Double.parseDouble(String.valueOf(stationInfo.get("DISTANCE_DATA")))   <= Double.parseDouble(String.valueOf(stationInfo.get("DSTNC_LT"))))
				 {
					// 반납 처리로 처리 해야함...
					 logger.debug("[KICK_BOARD]return stations is possbile : DISTNACE {}  DIST_LT {}!!!!!",String.valueOf(stationInfo.get("DISTANCE_DATA")),String.valueOf(stationInfo.get("DSTNC_LT")));
				 }
				 else
				 {
					 //F_GPS02 로 업데이터 해야함 !!!
					 logger.debug("[KICK_BOARD]return stations is not possbile : DISTANCE {} DIST_LT {} !!!!!",String.valueOf(stationInfo.get("DISTANCE_DATA")),String.valueOf(stationInfo.get("DSTNC_LT")));
					 
				 }
				 
				if(stationInfo == null )
				{
					
				}
				else
				{
					String	dist_station,dstnc_lt,st_id;
					st_id = (String)stationInfo.get("STATION_ID");
				//	dist_station = (String)stationInfo.get("DISTANCE_DATA");	//exception 에러
				//	dstnc_lt = (String)stationInfo.get("DSTNC_LT");
					
					
					logger.debug("##### BIKE RETURN ## GPS STATION ID[" + String.valueOf(stationInfo.get("STATION_ID")) + "] ENTERED");;	
				//	logger.debug("##### BIKE RETURN ## GPS STATION FIND START! st_id {}, distance {} , dstnc_lt {}  #####",st_id,dist_station,dstnc_lt);
				}
			}
			else
			{
				logger.debug("KICK_RETURN ## KICK IS NO_GPS : USR_PHONE GPS_CHECK !!!!!");
				
				//핸드폰으로 잡은 gps 위치..!!!!!!
				Map<String, String> return_GPS = bikeService.getBikeRETURN_GPS(info.getRENT_SEQ());
				
				if(return_GPS != null)
				{
					String latitude = String.valueOf(return_GPS.get("BIKE_LATITUDE"));
			        String longitude = String.valueOf(return_GPS.get("BIKE_LONGITUDE"));
			        
			        info.setGPS_X(latitude);
		    		info.setGPS_Y(longitude);
			        
			        logger.debug("GPS INFO2 ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
			        
			        
			        Map<String, String> GPS = new HashMap<String, String>();
					GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
					GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
					GPS.put("BIKE_ID", String.valueOf(vo.getBicycleId()));
					stationInfo = commonService.CheckStation_ForGPS(GPS);
					
					if( Double.parseDouble(String.valueOf(stationInfo.get("DISTANCE_DATA")))   <= Double.parseDouble(String.valueOf(stationInfo.get("DSTNC_LT"))))
					{
						// 반납 처리로 처리 해야함...
						logger.debug("[APP_GPS]return station possible DISTNACE {}  DIST_LT {}!!!!!",String.valueOf(stationInfo.get("DISTANCE_DATA")),String.valueOf(stationInfo.get("DSTNC_LT")));
					}
					else
					{
						//단말기 GPS 없고 , 사용자 GPS 으로 거리 가 넘었을대 반납 불가 !!!!
						
						 QRLog.setResAck("NOHDIST");
		     	    	 bikeService.updateQRLog(QRLog);
		     	    	 
						logger.debug("[APP_GPS]return stations is not possbile DISTNACE {}  DIST_LT {}!!!!!",String.valueOf(stationInfo.get("DISTANCE_DATA")),String.valueOf(stationInfo.get("DSTNC_LT")));
						
						//statrt 2022.12.15
						Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
						
						UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS02");
						UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
			 		
						logger.debug("KRRI_APP_GPS_FARWARY_AREA {}!!!!!  update F_GPS02 !!!!!!!! ");
						bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);
	 				
						com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
						com.setBikeBrokenCd("ELB_006");
						responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
						responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
					 
						responseVo = setFaiiMsg(responseVo, vo);
					 
						return responseVo;
						
					}
					
				}
				else	//NO GPS  : 단말기, 핸드폰 NO GPS 일대 
				{
					QRLog.setResAck("NOGPS");
	     	    	bikeService.updateQRLog(QRLog);
					
					logger.debug("####### [KICK_GPS APP_GPS IS NO_GPS_DATA ################ ");
							
					Map<String, String> UPDATE_BIKE_CLS_CD = new HashMap<String, String>();
		 			UPDATE_BIKE_CLS_CD.put("GPS_CLS_CD", "F_GPS01");
		 			UPDATE_BIKE_CLS_CD.put("RENT_BIKE_ID", vo.getBicycleId());
		 			bikeService.updateRENTGPS_CLS_CD(UPDATE_BIKE_CLS_CD);
					
				}
				
				if(stationInfo == null )
				{
				}
				else
				{
					logger.debug("##### BIKE RETURN ## GPS STATION FIND START! #####");
					logger.debug("##### BIKE RETURN ## GPS STATION ID[" + String.valueOf(stationInfo.get("STATION_ID")) + "] ENTERED");;
		 
				}
			}

     		
		
     		if(stationInfo != null)
			{
     			logger.error("stationInfo : set station_ID  beacon_id {}",vo.getBeaconId().toString());
     					
     			if(vo.getBeaconId().equals("0000000000"))
     			{
	     			com.setStationId(stationInfo.get("STATION_ID").toString());
	     			com.setRockId(stationInfo.get("RACK_ID").toString());
	     			info.setRETURN_STATION_ID(com.getStationId());
	     			info.setRETURN_RACK_ID(com.getRockId());
     			}
     			
     			if(Integer.parseInt(info.getHELMET_CNT()) > 0)
     			{
     				logger.debug("##### KICK HELMET_USE_TRY CHECK {} #############",info.getHELMET_YN());
    
     				
	     			if(info.getHELMET_YN().equals("Y"))	//헬멧 사용 한고객 !!!!
	     			{
	     				if(info.getHELMET_STATUS_YN().equals("N"))	//
	     				{
	     					
	     					logger.debug("##### KICK HELMET_USE : HELMET IS NOT CONNECTED : #############",info.getHELMET_YN());
	     					//반납 불가 !!!!!!!!
	     					
	     				}
	     				
	     			}
	     			
     			}
     	
			}
     		else
     		{
     			
//     			logger.error("stationInfo is NULL : beconID is not GPS : DEFAULT STATION INSERT" );
     			logger.error("stationInfo is NULL : beconID is not GPS : RETURN_FAILED" );
     			
     			/* NO_GPS, 비콘 ID 도 없으면 
	     			com.setStationId("ST-1099");
	     			if(ourBikeMap.get("BIKE_SE_CD").equals("BIK_001"))
	     			{
	     				com.setRockId("45800099900000");
	     			}
	     			else
	     				com.setRockId("45800099900099");
	     			
	     			info.setRETURN_STATION_ID(com.getStationId());
	     			info.setRETURN_RACK_ID(com.getRockId());
     			*/
     			
     			//반납 실패 시 
     			QRLog.setResAck("FAIL");
     			bikeService.updateQRLog(QRLog);
     			
     			responseVo.setErrorId(Constants.CODE.get("ERROR_FD"));	//invalid 비콘
    			responseVo = setFaiiMsg(responseVo, vo);
    			responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
    	  		
    			return responseVo;
    			
     		}

     		// 케스케이드 반납 확인
     		info.setCASCADE_YN("N");
     		    	
     		// 반납 시간
     		//20191016111015
     		SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmmss");
     		Date Return_dttm = null;
     		try {
				Return_dttm = transFormat.parse(vo.getRegDttm());
			} catch (ParseException e) {
				e.printStackTrace();
			}
     		
     		SimpleDateFormat RENTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     		Date Rent_Dttm = null;
     		try {
				Rent_Dttm = RENTFormat.parse(info.getRENT_DTTM());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	int sysTime = Integer.parseInt(info.getUSE_MI().toString());
        	
        	info.setRETURN_STATION_ID(com.getStationId());
        	info.setTRANSFER_YN("N");
        	info.setOVER_FEE_YN("N");
        	
         	Integer tem_USE_SEQ = 0;
         	
         	Map<String, String> GPS_DATA = new HashMap<String, String>();
         	GPS_DATA.put("RENT_SEQ", info.getRENT_SEQ());
         	
         	double ACC_DIST = 0.0;
         	
         	/* 2021.09.06 막음
         	Map<String, Object> bikeData = bikeService.getBikeMoveDist_COUNT(GPS_DATA);
         	
         	
			if(bikeData != null)
			{
				tem_USE_SEQ = Integer.valueOf(String.valueOf(bikeData.get("USE_SEQ")));
				int USE_SEQ = tem_USE_SEQ;
				ACC_DIST = new Double(bikeData.get("ACC_DIST").toString()); //현재까지 누적 데이터 db에서 추출
        		logger.debug("getBikeMoveDist_COUNT BICYCLE_ID {}, USE_SEQ {}" ,vo.getBicycleId(), USE_SEQ);
        		
        		//String latp = new CommonUtil().GetGPS(vo.getLatitude());
        		//String lonp = new CommonUtil().GetGPS(vo.getLongitude());
        		GPS_DATA.put("BIKE_LATITUDE", String.valueOf(info.getGPS_X()));
	         	GPS_DATA.put("BIKE_LONGITUDE", String.valueOf(info.getGPS_Y()));
        		
	         	double dlatp =  Double.parseDouble( info.getGPS_X());
	         	double dlonp = Double.parseDouble( info.getGPS_Y());
        		if(dlatp != 0.0 && dlonp != 0.0)
        		{
        			double USE_DIST = bikeService.getBikeMoveDist_Last(GPS_DATA);  //db 데이터 최종과 현재 첫번째 데이터와 비교
        			ACC_DIST += USE_DIST;
        		}
        		logger.debug("GPS DISTANCE LAST DB ACC_DIST {} ", ACC_DIST);
        		
        		info.setUSE_DIST(ACC_DIST + "");
        	}
			else
				info.setUSE_DIST("0");	//gps 로 거리 하는 함수 막음.
         	*/
         	info.setUSE_DIST("0");

        	info.setUSE_MI(sysTime+"");
        	
        	int weight = bikeService.getUserWeight(info.getUSR_SEQ());
        	
        	double co2 = (((double)ACC_DIST/1000)*0.232);
        	double cal = 5.94 * (weight==0?65:weight) *((double)ACC_DIST/1000) / 15;
        	

        	info.setREDUCE_CO2(co2+"");
        	info.setCONSUME_CAL(cal+"");
        		
        	//프리미엄 이용권 초과요금 적용 시간 가져오기
        	//int baseRentTime = Integer.parseInt((String)commonService.getBaseTime(info).get("BASE_TIME"));
        	// 프리미엄 이용권 자전거 기본대여시간 가져오기 (일반권 포함)_20160630_JJH_END
        	
        	int baseRentTime = 0;
    		
    		
			Map<String, Object> fee = new HashMap<String, Object>();
			
			fee.put("ADD_FEE_CLS", info.getPAYMENT_CLS_CD());
			fee.put("BIKE_SE_CD", info.getBIKE_SE_CD());
			
			Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee);	//TB_SVC_ADD_FEE  
			//Map<String, Object> maxPolicy = bikeService.getOverFeeMaxPolicy(fee);
			//추가요금 기본 시간 11분 부터 
			baseRentTime = Integer.parseInt(minPolicy.get("OVER_STR_MI").toString());
			logger.debug(" #####  PAY_CLS_CD {} use_time is baseRentTime {}  usr_Time {} "  , info.getPAYMENT_CLS_CD(),baseRentTime, info.getUSE_MI());
			
			if(info.getPAYMENT_CLS_CD().equals("BIL_007"))
			{
				if(sysTime - baseRentTime > 1 )	//2시간 초과 , 고객 !!!!!!!! 122 분부터 초과과금 한다 !!
				{
					info.setOVER_FEE_YN("Y");
					overPay = new CommonUtil().getPay(minPolicy, (sysTime));
					//overPay = new CommonUtil().getPay(minPolicy, (sysTime- baseRentTime));
					//overPay = new CommonUtil().getPay(minPolicy, maxPolicy, (sysTime - baseRentTime));
					info.setOVER_FEE(overPay+"");
					baseRentTime = Integer.parseInt(minPolicy.get("OVER_STR_MI").toString());
	
					
					info.setOVER_MI(String.valueOf(sysTime - baseRentTime));
				}
				else if(sysTime > 61) //62분 부터 한다 !!!
				{
					info.setOVER_FEE_YN("B");
					info.setOVER_FEE("1000");
					info.setOVER_MI(String.valueOf(sysTime - (baseRentTime-60)));
					
					logger.debug("NO_OVER_TIME : base USE TIME OVER {} : ADD 1000 won !!!!!!!!!",info.getUSE_MI());
	
				}
			}
			else{	//서비스 있는 사람들 !!!!
				
				/* test 회원은 추가과금 없음
					if(sysTime > 60){
						info.setOVER_FEE_YN("B");
						info.setOVER_FEE("1000");
					}
				*/		
				logger.debug("##### MEMBER_USR PAYMENT {}  is NO_PAY !!!!!!!!!!!!!!!!!!!!",info.getPAYMENT_CLS_CD());
			}
			// 반납 프로세스 실행
		//	logger.debug("NO_RETURN PROCESS");
			
			
			bikeService.parkingInfoDelete(com);
			bikeService.procReturn(info);
        	
		}
        else
        {
        	logger.error("RentHistVo is NOT EXIST : no_getForReturnUse BIKE_SE {} ",ourBikeMap.get("BIKE_SE_CD"));
        	//거치대 
        	
        	if(!vo.getBeaconId().equals("0000000000")) //거치대 반납 
        	{
	        	Map<String, Object> stationInfo = commonService.checkMount(com);
	        	
	        	try{
	        		logger.debug("stationid = {}",stationInfo.get("NOW_LOCATE_ID").toString());
	        		
	        	}catch(Exception e)
	        	{
	        		
	        	}
        	}
        	//
        	RentHistVo info1 = new RentHistVo();
        	
        	if(ourBikeMap.get("BIKE_SE_CD").equals("BIK_002"))
        	{
        		logger.error("RentHistVo is NOT EXIST : BIKE_TEST SUCCESS");
        		responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
                responseVo.setSeqNum(vo.getSeqNum());
                responseVo.setCommandId(Constants.CID_RES_RETURNBIKE);
                responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
                responseVo.setBicycleId(vo.getBicycleId());
                
                
                List<HashMap<String, String>> PeriodList = commonService.CheckPeriodTime();
         		 
         		if(PeriodList.size() == 0 )
         		{
        	  			 logger.error("Period Information Find Error");
        	  			 responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
        	  			 responseVo = setFaiiMsg(responseVo, vo);
        	           	
        	  			 return responseVo;
         		}
         		else
         		{
         			for(HashMap<String, String> PeriodMap : PeriodList)
         			{
         				if(String.valueOf(PeriodMap.get("COM_CD")).equals("MSI_036"))
         				{
          					 logger.debug("######################## MSI_036 " + String.valueOf(PeriodMap.get("ADD_VAL1")));
          					 //responseVo.setReturnPeriod(String.valueOf(PeriodMap.get("ADD_VAL1")));
          					 int Hour = Integer.parseInt(PeriodMap.get("ADD_VAL1"))/60;
          					 int Minute = Integer.parseInt(PeriodMap.get("ADD_VAL1"))%60;
          					 responseVo.setPeriodHour(getToString(String.valueOf(Hour),2));
          					 responseVo.setPeriodMinute(getToString(String.valueOf(Minute),2));
         				}
         			}
         		}
                
         		 QRLog.setResAck("EN:RTN");
    	    	
	    		 bikeService.updateQRLog(QRLog);
	    		
         		 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
	        	
         	//	 info.setBIKE_NO(vo.getBicycleId());
         		 
         		logger.error("info : set bike ID {} , {} !!! ",vo.getBicycleId(),com.getBicycleId());
         		
         		info1.setRENT_BIKE_ID(com.getBicycleId());
         	//	info.setUSE_DIST("0");
         		commonService.changeValidBike(com);
         		
        		 if(parkingMap == null)	//주기 보고 시 파깈정보 없으면 ...
        		 {
        			 com.setStationId("ST-999");
          			 com.setRockId("45800099900000");
          			          			         			
          			info1.setRETURN_STATION_ID(com.getStationId());
          			info1.setRETURN_RACK_ID(com.getRockId());
          			
        			 // 자전거 주차 정보 INSERT PARKING
        			 bikeService.insertPeriodParkingInfo(info1);
        			 
        			 // 자전거 정보 UPDATE BIKE
        		//	 bicycleMapper.updateBike(info);
        			 
        		 }
        	//	bicycleMapper.updateBike(info);
                return responseVo;
        		
        	}
        	else
        	{
	    		responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_03"));
	    	
	    		responseVo.setErrorId(Constants.CODE.get("ERROR_F6"));
	    		responseVo = setFaiiMsg(responseVo, vo);
	    	
	    		QRLog.setResAck("NORENT");
	    	
	    		bikeService.updateQRLog(QRLog);
	        	
	        	return responseVo;
        	}
        }
        
        
        QRLog.setResAck("SUC");
        bikeService.updateQRLog(QRLog);
        
        responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_RES_RETURNBIKE);
        responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        List<HashMap<String, String>> PeriodList = commonService.CheckPeriodTime();
 		 
 		if(PeriodList.size() == 0 )
 		{
	  			 logger.error("Period Information Find Error");
	  			 responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
	  			 responseVo = setFaiiMsg(responseVo, vo);
	           	
	  			 return responseVo;
 		}
 		else
 		{
 			for(HashMap<String, String> PeriodMap : PeriodList)
 			{
 				if(String.valueOf(PeriodMap.get("COM_CD")).equals("MSI_036"))
 				{
  					 logger.debug("######################## MSI_036 " + String.valueOf(PeriodMap.get("ADD_VAL1")));
  					 //responseVo.setReturnPeriod(String.valueOf(PeriodMap.get("ADD_VAL1")));
  					 int Hour = Integer.parseInt(PeriodMap.get("ADD_VAL1"))/60;
  					 int Minute = Integer.parseInt(PeriodMap.get("ADD_VAL1"))%60;
  					 responseVo.setPeriodHour(getToString(String.valueOf(Hour),2));
  					 responseVo.setPeriodMinute(getToString(String.valueOf(Minute),2));
 				}
 			}
 		}
        
        
        return responseVo;
    }
    
    
    private String getToString(String str , int length)
	 {
		 int temp = 0;
		 if(str.length() < length)
		 {
			 temp = length - str.length();
			 for (int i = 0; i <  temp; i++)
			 {
				 str = "0"+str;
			 }
		 }
		 return str;
	}
    
    
    


	/*private void rentSuccessPushMsg(PushTargetVO pushVo) {
		
		if(pushVo.getUsrDeviceType().equals(IConstants.PUSH_TYPE_GCM)) {
			
		} else {
			
			
		}
		
	}*/
    
    private String getNaverApitoRegiong(String xpos,String ypos)
    {
    		String NAVER_API_KEY ;
    		String NAVER_SECRET_KEY;	

			//zgyaca9y6t    , JbaXg9LvYDkHHPmeGi2r0DsL20KhhvOIkfJMfEOd
			// 양재영 과장  : a88veypaz2 , kg5bLoJkxE1KcFUURqgjdyf5x0ztBMKRY46y4Med
			NAVER_API_KEY 			= "fz4629oc58";     			// 가맹점 코드
			NAVER_SECRET_KEY			= "UabPltufcHwQtbpmaiSaM94YGXMmayjbCpxSV33E";  // billing Key
			
			ObjectMapper mapper = new ObjectMapper(); 		  			//jackson json object
			NaverGPSUtil util = new NaverGPSUtil();  
			
			String XPOS="";
			String YPOS="";
			  			
			String info_String = "coords="+ XPOS + "," + YPOS +"&output=json&orders=legalcode";
			
			logger.debug("NAVER API_BASIC " + info_String);
			
			
			String info_result = util.GPSSend(info_String, NAVER_API_KEY, NAVER_SECRET_KEY);
			
			String  Data_Detail = null;
			
			JsonNode node;
			
			
			return "";
    }

	// 반납 실패 메세지
    public QRReturnResponseVo setFaiiMsg(QRReturnResponseVo responseVo, QRReturnResultRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RETURNBIKE);
    	responseVo.setBicycleId(vo.getBicycleId());
   // 	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
    	
    	return responseVo;
    }
    
}
