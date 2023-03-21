package org.fincl.miss.service.biz.bicycle;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentMapper;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonMapper;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.PeriodicStateReportsRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentWaitingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentWaitingResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalCancleRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalCancleResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.fincl.miss.server.sms.vo.SendSMSVo;
import org.fincl.miss.server.util.MainPayUtil;

import com.mainpay.sdk.utils.ParseUtils;

@RPCServiceGroup(serviceGroupName = "대여")
@Service
public class RentalService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	private CommonMapper comm;
	
	@Autowired
	BicycleRentService bikeService;
	
	
	@Autowired
    private BicycleRentMapper bicycleMapper;
	
    // 대여대기
    @RPCService(serviceId = "Bicycle_14", serviceName = "대여 대기 Request", description = "대여 대기 Request")
    public RentWaitingResponseVo waiting(RentWaitingRequestVo vo) {
        
		 logger.debug("######################## KICK LOCK OPEN/CLOSE EVENT : 4372 : 0x14  ");
		 logger.debug("SMART DEVICE OPEN EVENT vo :::::::::::{} " , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
    //    logger.debug(" MessageHeader m:: {}" , m);
        
        RentWaitingResponseVo responseVo = new RentWaitingResponseVo();
        /**
         * 정상인 경우 LangClsCd를 사용하고,
         * 에러가 있는 경우 해당 에러코드로 담아 전달한다.
         * 기본 값으로 사용자 기본 언어코드인 00 을 세팅한다.
         * 기본적으로 예약정보를 확인할 수 있는 운휴안내/자전거/거치대 오류 안내 는 한글로 안내.
         * 예약정보 확인후 대여대기 정보 안내만 사용자 언어로 제공.
         */
        
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        com.setTimeStamp(vo.getTimestamp());	//추가 
        com.setBikeId(vo.getBicycleId());
        com.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
        
        Map<String, String> ourBikeMap = new HashMap<String, String>();
        
        ourBikeMap = bikeService.chkOurBike(com);	//ENTRPS_CD : ENT_001(vick) , ENT_002(witcom)  , ENT_003(atech)
		 
        QRLogVo QRLog = new QRLogVo();
		 
        String	 nBikeSerial;
		 
        if(ourBikeMap != null)
        {
			//add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
		 	nBikeSerial = bikeNo.substring(2,bikeNo.length());
		 	
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");	//ENT_003   DB :002
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,ENTRPS_CD.length()));
		 	
		 	logger.debug("QR_4372 ##### => bike {} ,state {} ,usrType {}, company {} ,lock {}",vo.getBicycleId(),vo.getBicycleState(),vo.getUsrType(),com.getCompany_cd(),vo.getLockState());
		 	 
		 	//Map<String, String> qrlog = new HashMap<String, String>();
		 	
	//	 	QRLogVo QRLog = new QRLogVo();
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 	QRLog.setBeaconid(vo.getBeaconId());
		 	
		 	//추가..
		 	//if(!vo.getUsrseq().equals(("FFFFFFFFFF")))
		 	//{
		 		//QRLog.setUserSeq(new CommonUtil().GetUSRSeq(vo.getUsrseq()));
		 		
		 		//String.valueOf(Integer.parseInt(vo.getUsrseq()));
		 	QRLog.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
		 	//}
		 	
		 	
		 	QRLog.setUserType(vo.getUsrType());
		 	QRLog.setLock(vo.getLockState());
		 	QRLog.setBiketype(vo.getBikeType());
		 	
		 	QRLog.setDev_BATT(String.valueOf(Integer.parseInt(vo.getBattery(), 16)));
		 	QRLog.setBeacon_BATT(String.valueOf(Integer.parseInt(vo.getBeaconbatt(), 16)));
		 	QRLog.setBike_BATT(String.valueOf(Integer.parseInt(vo.getElecbatt(), 16)));
		 	QRLog.setFirm_fw(vo.getBle_firmwareVs());
		 	QRLog.setModem_fw(vo.getModem_firmwareVs());
		 	
		 	if(!vo.getLatitude().equals("00000000") && !vo.getLatitude().substring(0,6).equals("FFFFFF")
					 && !vo.getLongitude().equals("00000000") && !vo.getLongitude().subSequence(0, 6).equals("FFFFFF"))
		 	{
		 		QRLog.setXpos(new CommonUtil().GetGPS(vo.getLatitude()));
		 		QRLog.setYpos(new CommonUtil().GetGPS(vo.getLongitude()));
		 	}
		 	else
           {
		 		
		 		QRLog.setXpos("00000000");
		 		QRLog.setYpos("00000000"); 
		 		
	//	 		QRLog.setXpos(vo.getLatitude());
	//	 		QRLog.setYpos(vo.getLongitude());        
           }
		 	
		 	
		 	QRLog.setTimeStamp(vo.getTimestamp());
			QRLog.setMessage(vo.getReqMessage());
			
			QRLog.setQr_frame("대여완료보고");
			
		 	bikeService.insertQRLog(QRLog);
		 	
		 }
		 else
		 {
			 QRLog.setBicycleNo("FFFFFFFF");
			 
			 QRLog.setBicycleId(vo.getBicycleId());
			 QRLog.setLock(vo.getLockState());
			 QRLog.setBicycleState(vo.getBicycleState());
			 QRLog.setBeaconid(vo.getBeaconId());
			 QRLog.setDev_BATT(String.valueOf(Integer.parseInt(vo.getBattery(), 16)));
			 QRLog.setBeacon_BATT(String.valueOf(Integer.parseInt(vo.getBeaconbatt(), 16)));
			 QRLog.setBike_BATT(String.valueOf(Integer.parseInt(vo.getElecbatt(), 16)));
			 QRLog.setFirm_fw(vo.getBle_firmwareVs());
			 QRLog.setModem_fw(vo.getModem_firmwareVs());
			 QRLog.setMessage(vo.getReqMessage());
			 /*
			 if(vo.getLockState().equals("01"))
			 {
				 QRLog.setQr_frame("반납 이벤트");
			 }
			 else
			 */
			 
			 QRLog.setQr_frame("대여완료보고");
				
			 bikeService.insertQRLog(QRLog);
			 
			 logger.error("INVALID 자전거 ID {}",vo.getBicycleId() );
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 
        if(vo.getLockState().equals("00") ||vo.getLockState().equals("01") ||vo.getLockState().equals("02") )
        {
        	//킥보드 는 거치정보 보고 
        	//대여 완료 이면 
        	BikeRentInfoVo bikeInfo = bicycleMapper.getBikeInfo(com);	//거치정보 체크 
  			if(bikeInfo == null)
  			{
  				QRLog.setResAck("APP");
  				//QRLog.setResAck("NOPARK");
				bikeService.updateQRLog(QRLog);
				logger.error("THIS PACKET RENTING APP" );
				//logger.error("TB_SVC_RENT_BIKE_PARKING DATA NOT FOUND" );
				//responseVo.setErrorId(Constants.CODE.get("ERROR_F4"));
				//responseVo = setFaiiMsg(responseVo, vo);
     	    		
				//return responseVo;
				 
  			}
  			else	//거치 정보 있다고 판단되면 
  			{
  				/*
  				Map<String, Object> fee1 = new HashMap<String, Object>();
  				
  				fee1.put("BIKE_SE_CD", ourBikeMap.get("BIKE_SE_CD"));
  				fee1.put("ADD_FEE_CLS","BIL_007");
  				
  				Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee1);	//TB_SVC_ADD_FEE  
  				*/
  				
  				
  				
  				
  				if(vo.getUsrType().equals("02"))
  				{
  					 QRLog.setResAck("AMOVE");
  					 bikeService.updateQRLog(QRLog);
  					 
  					logger.debug("QR_BIKE IS RENTAL_EVENT AND USER TYPE IS ADMIN");
  					//com.setUserSeq(new CommonUtil().GetUSRSeq(vo.getUsrseq()));
  					com.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
  					com.setRockId(bikeInfo.getRent_rack_id());
  					bikeService.procAdminMove(com);
  				}
  				else
  				{
  					com.setUserSeq(String.valueOf(Integer.parseInt(vo.getUsrseq())));
  					com.setRockId(bikeInfo.getRent_rack_id());
	  				 
  					// 로그 추가..2020.04.12 
  					logger.debug("QR_BIKE IS RENTAL_EVENT  usr_seq: {} is request ",com.getUserSeq());
  					/*
  					BikeRentInfoVo voucher = bikeService.getUseVoucherInfo(com);	//2020.02. 단체권 포함되도록 수정...
		  				 
  					if(voucher == null)
  					{
  						/*
  						if(ourBikeMap.get("BIKE_SE_CD").equals("BIK_001"))
  						{
  							
  							logger.debug("QR_BIKE IS RENTAL_EVENT  BIKE_TEST  ");
  							
  							responseVo.setBle_fwupdate(Constants.CODE.get("WIFI_UPDATE_00")); //  f/w 무선 업데이트 진행
  							 
  							responseVo.setModem_fwupdate(Constants.CODE.get("WIFI_UPDATE_00")); //  f/w 무선 업데이트 진행
  						
  							responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
  							responseVo.setSeqNum(vo.getSeqNum());
  							responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
  							responseVo.setBicycleState(vo.getBicycleState());
  							responseVo.setBicycleId(vo.getBicycleId());
  							responseVo.setDayAndNight("00");
  							
  					
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
  					 				if(String.valueOf(PeriodMap.get("COM_CD")).equals("MSI_039"))	//2020.04.14
  					 				{
  					  					 logger.debug("######################## MSI_039 " + String.valueOf(PeriodMap.get("ADD_VAL1")));
  					  					 int Hour = Integer.parseInt(PeriodMap.get("ADD_VAL1"))/60;
  					  					 int Minute = Integer.parseInt(PeriodMap.get("ADD_VAL1"))%60;
  					  					 responseVo.setPeriodHour(getToString(String.valueOf(Hour),2));
  					  					 responseVo.setPeriodMinute(getToString(String.valueOf(Minute),2));
  					 				}
  					 			}
  					 		}

  					        return responseVo;
  					  							
  						}
  						
  						
  						
	  						logger.error("USR_SEQ[" + com.getUserSeq() + "] HAS NO RENT POSSIBLE VOUCHER");
	  						QRLog.setResAck("VOUNO");
	  						bikeService.updateQRLog(QRLog);
	  						responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
	  						responseVo = setFaiiMsg(responseVo, vo);
									 
	  						return responseVo;
  						
  					}
  					*/
	 
					Map<String, Object> useBike = bikeService.getUseBikeInfoFULL(com);
					if(useBike != null)
					{
						logger.error("RENTAL_EVENT USR_SEQ[" + com.getUserSeq() + "] HAS RENT INFO NOT COMPLETE");
							
						//add
						String	rack_id = String.valueOf(useBike.get("RENT_RACK_ID"));
						String	bike_id = String.valueOf(useBike.get("RENT_BIKE_ID"));
						String  rentYn = String.valueOf(useBike.get("RENT_YN"));
						
						logger.debug("RENT_EVENT  getUseBikeInfoFULL  usr_seq {} ,{},{},{} ",com.getUserSeq(),rack_id,bike_id,rentYn);
						
							
						if(rack_id == null || rack_id.equals("") || bike_id == null || bike_id.equals(""))
						{
							logger.error("RENTAL_EVENT  USR_SEQ set rentTableUpdate2 ");
								
							bicycleMapper.rentTableUpdate2(com);
							//BikeRentInfoVo bikeInfo = bicycleMapper.getBikeInfo(com);
						}
						//TB_SVC_RNET 에 없는 경우 있는 이런경우 업데이타 해서 reservation 하도록 해줘야할듯..	
					}
					else
					{
						// 대여 주기시 이벤트시 대여 이력 없으면 대여 완료 넣어주기.
						// 킥보드는 이력 안넣어주고 에러 로 표시  , 자전거는 bluetooh 로 넣어주기...2021.05.18
						/*
						if(ourBikeMap.get("BIKE_SE_CD").equals("BIK_002"))	//킥보드
						{
						*/
							
							logger.error("RENT_INFO is NULL  USR_SEQ {} is rent failed ",com.getUserSeq());
							
							QRLog.setResAck("RFAIL");
	  						bikeService.updateQRLog(QRLog);
	  						responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
	  						responseVo = setFaiiMsg(responseVo, vo);
									 
	  						return responseVo;
	  					/*	
						}
						else
						{
							if(ourBikeMap.get("BIKE_SE_CD").equals("BIK_002"))
							{
								if(voucher.getRent_cls_cd().equals("RCC_001"))
								{
									voucher.setRent_cls_cd("RCC_002");
								}
							}
							bikeService.reservationInsert(com, voucher);
								
							logger.error("RENTAL_EVENT USR_SEQ[" + com.getUserSeq() + "] HAS NO RENT INFO : INSERT_RESERVATION ");
						}
						*/
					}
		     			 
					// 대여 정보 
					Map<String, Object> rentInfo = commonService.reservationCheck(com);
		     			
					if(rentInfo == null)
					{
						QRLog.setResAck("RVRNO");
						bikeService.updateQRLog(QRLog);
						logger.error("reservationCheck (TB_SVC_RENT) is NOT_FIND_INFO !!!!!!!!!!!!!!!!!!!!");
						
						responseVo.setErrorId(Constants.CODE.get("ERROR_EF"));
						responseVo = setFaiiMsg(responseVo, vo);
		     	    		
						return responseVo;
					}
					
					if(rentInfo.get("PARTCLR_MATTER").toString() == "" ||rentInfo.get("PARTCLR_MATTER").toString() == null )
					{
						logger.debug("PARTCLR_MATTER : KICK_USER is MANAGER !!!!!");
						
					}
					
					if(rentInfo.get("BIKE_SE_CD").equals("BIK_001"))
					{
						if((rentInfo.get("PARTCLR_MATTER").equals("PAY")) &&  ((rentInfo.get("PAYMENT_CLS_CD").equals("BIL_006"))))
						{
							MainPayUtil MainPayutil = new MainPayUtil();
							HashMap<String, String> parameters = new HashMap<String, String>();
							String billkey = (String) rentInfo.get("BILLING_KEY") ;
				    		if( billkey != null && !"".equals(billkey)) 
				    		{	// 빌링키 없음 실패		
				    			parameters.put("billkey", billkey);	// 정기결제 인증 키
				    		}
				    		parameters.put("goodsId", "BIL_006");
				    		parameters.put("goodsName", "일회 자전거 잠금해제");
				    		String rent_fee = comm.getLockOffPay(parameters);
				    		//parameters.put("amount", "100");
				    		parameters.put("amount", rent_fee);
				        	
				        	
				    		String responseJson = MainPayutil.approve(parameters,"Y");
				    		
				    		
				    		
				    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
							String resultCode = (String) responseMap.get("resultCode");
							String resultMessage = (String) responseMap.get("resultMessage");
						    
							if(!"200".equals(resultCode)) {	// API 호출 실패
								logger.debug("Return Pay Fail-->> ["+resultMessage + "]");
								//comm.setOverFeePayReset(fee);
							}
							else
							{	// API 호출 성공
								try 
								{
									OverFeeVO fee = new OverFeeVO();
									fee.setPaymentMethodCd("BIM_001");
									fee.setResultCD("0000");
									fee.setPaymentStusCd("BIS_001");
									fee.setMb_serial_no(parameters.get("mbrRefNo"));
									Map dataMap = (Map)responseMap.get("data");
									fee.setPaymentConfmNo(String.valueOf(dataMap.get("refNo")));
									fee.setTotAmt(fee.getOverFee());
									fee.setOrder_certify_key(String.valueOf(responseMap.get("applNo")));
									fee.setProcessReasonDesc(resultMessage);
									fee.setVoucher_seq(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
									//mbrRefNo PAYMENT_CONFM_NO
									//applNo ORDER_CERTIFY_KEY
									int result = comm.setPaymentBillingKey(fee);
									logger.debug("BIKE OPEN PAY OK ! ",fee.getVoucher_seq());
									
								} 
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
						
						if(!rentInfo.get("BIKE_VOUCHER_CNT").equals("99"))
						//if(!voucher.getBike_voucher_cnt().equals("99"))
						{
							/*
							if((Integer.parseInt(String.valueOf(rentInfo.get("BIKE_USE_CNT")))) >= (Integer.parseInt(String.valueOf(rentInfo.get("BIKE_VOUCHER_CNT")))))
							//if((Integer.parseInt(voucher.getBike_use_cnt())) >= (Integer.parseInt(voucher.getBike_voucher_cnt())))
							{
								//대여 실패
								logger.error("USR_SEQ[" + com.getUserSeq() + "] HAS NO RENT POSSIBLE VOUCHER");
		  						QRLog.setResAck("VOUNO2");
		  						bikeService.updateQRLog(QRLog);
		  						responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
		  						responseVo = setFaiiMsg(responseVo, vo);
										 
		  						return responseVo;
							}
							else
							{
							*/
								//대여 성공
								bikeService.updateBikeCnt(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
							/*}*/
						}
						else
						{
							//대여 성공
							//bikeService.updateBikeCnt(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
						
						}
					}
					else
					{
						
						
						if((rentInfo.get("PARTCLR_MATTER").equals("PAY")) &&  ((rentInfo.get("PAYMENT_CLS_CD").equals("BIL_007"))))
						{
							MainPayUtil MainPayutil = new MainPayUtil();
							HashMap<String, String> parameters = new HashMap<String, String>();
							String billkey = (String) rentInfo.get("BILLING_KEY") ;
				    		if( billkey != null && !"".equals(billkey)) 
				    		{	// 빌링키 없음 실패		
				    			parameters.put("billkey", billkey);	// 정기결제 인증 키
				    		}
				    		parameters.put("goodsId", "BIL_007");
				    		parameters.put("goodsName", "일회 킥보드 잠금해제");
				    		String rent_fee = comm.getLockOffPay(parameters);
				    		//parameters.put("amount", "800");
				    		parameters.put("amount", rent_fee);
				        	
				    		
				    		String responseJson = MainPayutil.approve(parameters,"Y");
				    		
				    		
				    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
							String resultCode = (String) responseMap.get("resultCode");
							String resultMessage = (String) responseMap.get("resultMessage");
						 	
						 	
				    		
				    	//	String	resultCode = "200";
				    	//	String  resultMessage = "TEST";
				    		
							if(!"200".equals(resultCode)) {	// API 호출 실패
								logger.debug("Return Pay Fail-->> ["+resultMessage + "]");
								//comm.setOverFeePayReset(fee);
							}
							else
							{	// API 호출 성공
								try 
								{
									OverFeeVO fee = new OverFeeVO();
									fee.setPaymentMethodCd("BIM_001");
									fee.setResultCD("0000");
									fee.setPaymentStusCd("BIS_001");
									fee.setMb_serial_no(parameters.get("mbrRefNo"));
									Map dataMap = (Map)responseMap.get("data");
									fee.setPaymentConfmNo(String.valueOf(dataMap.get("refNo")));
									fee.setTotAmt(fee.getOverFee());
									fee.setOrder_certify_key(String.valueOf(dataMap.get("applNo")));
									fee.setProcessReasonDesc(resultMessage);
									//mbrRefNo PAYMENT_CONFM_NO
									//applNo ORDER_CERTIFY_KEY
									fee.setVoucher_seq(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
									
									int result = comm.setPaymentBillingKey(fee);
									logger.debug("KICKBOARD OPEN PAY OK !! voucher {} ",fee.getVoucher_seq());
									
								} 
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
						
						
						if(!rentInfo.get("KICK_VOUCHER_CNT").equals("99"))
						//if(!voucher.getKick_voucher_cnt().equals("99"))
						{
							/*
							if((Integer.parseInt(String.valueOf(rentInfo.get("KICK_USE_CNT")))) >= (Integer.parseInt(String.valueOf(rentInfo.get("KICK_VOUCHER_CNT")))))
							//if((Integer.parseInt(voucher.getKick_use_cnt())) >= (Integer.parseInt(voucher.getKick_voucher_cnt())))
							{
								//대여 실패
								logger.error("USR_SEQ[" + com.getUserSeq() + "] HAS NO RENT POSSIBLE VOUCHER");
		  						QRLog.setResAck("VOUNO3");
		  						bikeService.updateQRLog(QRLog);
		  						responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
		  						responseVo = setFaiiMsg(responseVo, vo);
										 
		  						return responseVo;
							}
							else
							{
							*/
								//대여 성공
								bikeService.updateKickCnt(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
								//bikeService.updateKickCnt(voucher.getVoucher_seq());
							/*}*/
						}
						else
						{
							//대여 성공
							//bikeService.updateKickCnt(String.valueOf(rentInfo.get("VOUCHER_SEQ")));
							//bikeService.updateKickCnt(voucher.getVoucher_seq());
						
						}
					
					}
	 
					if(!bikeService.rentProcUpdate(com, rentInfo))
					{
						logger.error("rentProcUpdate: invalid voucher:ERROR_E5" );
		     				
						QRLog.setResAck("RENFA");
						bikeService.updateQRLog(QRLog);
						responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
						responseVo = setFaiiMsg(responseVo, vo);
		     	    		
						return responseVo;
					}
					else
					{
						/**
						 * 대여정보가 정상적으로 저장된 경우, SMS발송
						 */
						logger.debug("RENT_EVENT  rentProcUpdate usr_seq {} ",com.getUserSeq());
		     				
						QRLog.setResAck("RENT");
						bikeService.updateQRLog(QRLog);
		     	 		
						//요금 확인 ..
						Map<String, Object> fee1 = new HashMap<String, Object>();
		  				
		  				fee1.put("BIKE_SE_CD", ourBikeMap.get("BIKE_SE_CD"));
		  				fee1.put("ADD_FEE_CLS",rentInfo.get("PAYMENT_CLS_CD"));
		  				
		  				Map<String, Object> minPolicy = bikeService.getOverFeeMinPolicy(fee1);	//TB_SVC_ADD_FEE  
		  				
		  				String add_fee = minPolicy.get("ADD_FEE_CLS").toString();
		  				
						com.setStationId(String.valueOf(rentInfo.get("RENT_STATION_ID")));
						com.setUserSeq(String.valueOf(rentInfo.get("USR_SEQ")));
						Map<String, Object> msgInfo = bikeService.getRentMsgInfo(com);
		     				

						SmsMessageVO sms = new SmsMessageVO();
						sms.setTitle("대여안내");
						sms.setType("S");
		     			
						if(msgInfo != null && msgInfo.get("DEST_NO") != null && !msgInfo.get("DEST_NO").equals(""))
						{
		     				sms.setAutoSendYn("K");
		     				
							String destno = String.valueOf(msgInfo.get("DEST_NO"));
							if(destno != null && !destno.equals(""))
							{
		     					String Message = null;
		     					if(msgInfo.get("BIKE_SE_CD").equals("BIK_001"))
		     					{
		     					//Message = "<레일킥> " + msgInfo.get("BIKE_NO") + " 자전거 대여완료. "+add_fee+"분마다 추가요금 200원 발생합니다.";									
		     						Message = "<레일킥> " + msgInfo.get("BIKE_NO") + " 자전거 대여완료. 10분마다 추가요금 200원 발생합니다.";
		     					}
		     					else
		     					{
		     						//<레일킥> KAK-0004 킥보드 대여완료. 1시간초과시 이용요금 1000원,2시간초과시 10분마다 1000원이 발생합니다.
		     						//
		     						//<레일킥> #{kick_no} 킥보드 대여완료. 1시간초과시 이용요금 #{요금}원,2시간초과시 10분마다 #{초과요금}원이 발생합니다.
		     						//신규 추가 2022.11.24
		     						Message = "<레일킥> " + msgInfo.get("BIKE_NO") + " 킥보드 대여완료. 반납은 정거장에서만 가능합니다. 1시간초과시 이용요금 1000원,2시간초과시 10분마다 1000원이 발생합니다.";
		     						
		     					/*	
		     						if(String.valueOf(rentInfo.get("RENT_CLS_CD")).equals("RCC_004"))
		     						//if(voucher.getRent_cls_cd().equals("RCC_004"))
		     						{
		     							Message = "<레일킥> " + msgInfo.get("BIKE_NO") + " 킥보드 대여완료. 1분마다  추가요금 1000원이 발생합니다.";
		     						}
		     						else
		     							Message = "<레일킥> " + msgInfo.get("BIKE_NO") + " 킥보드 대여완료. 1분마다 추가요금 1000원이 발생합니다.";
		     					*/
		     						
		     					}
		     					sms.setDestno(destno);
		     					sms.setMsg(Message.toString());
		     			//		sms.setTemplate_code("bizp_2022100709530018895146258");	//
		     			//		sms.setTemplate_code("bizp_2022112314593418895402655"); //new
		     					sms.setTemplate_code("bizp_2022121514030325490579286"); //new 정거장 추가
		     					//bizp_2022112314593418895402655
		     					
		     					//bizp_2022112314593418895402655  이용요금 수정 \
		     					//if(info.getUSR_MPN_NO().equals("01051932653")||info.getUSR_MPN_NO().equals("01067774004") || info.getUSR_MPN_NO().equals("01080193152") )
		     					if(destno =="01051932653" || destno == "01067774004" || destno == "01080193152")
		     						logger.debug("[RENT]SCH_MANAGER : Don't send SMS ");
		     					else
		     						SmsSender.sender(sms);
		     				}
						}
					}
  				}//usrtype =01
  			}	//거치정보 있을때
  		}	//락상태 여림 (02)
        else
        {
        	//락 상태 이상 
        	logger.error("rentProcUpdate: invalid LOCK_STATUS :ERROR_E5" );
				
			QRLog.setResAck("RENFA");
			bikeService.updateQRLog(QRLog);
			responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
			responseVo = setFaiiMsg(responseVo, vo);
 	    		
			return responseVo;
        	
        }
        responseVo.setBle_fwupdate(Constants.CODE.get("WIFI_UPDATE_00")); //  f/w 무선 업데이트 진행
		 
		responseVo.setModem_fwupdate(Constants.CODE.get("WIFI_UPDATE_00")); //  f/w 무선 업데이트 진행
	
		responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		responseVo.setSeqNum(vo.getSeqNum());
		responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
		responseVo.setBicycleState(vo.getBicycleState());
		responseVo.setBicycleId(vo.getBicycleId());
		responseVo.setDayAndNight("00");
		
		
		
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
 				if(String.valueOf(PeriodMap.get("COM_CD")).equals("MSI_039"))	//2020.04.14
 				{
  					 logger.debug("######################## MSI_039 " + String.valueOf(PeriodMap.get("ADD_VAL1")));
  					 int Hour = Integer.parseInt(PeriodMap.get("ADD_VAL1"))/60;
  					 int Minute = Integer.parseInt(PeriodMap.get("ADD_VAL1"))%60;
  					 responseVo.setPeriodHour(getToString(String.valueOf(Hour),2));
  					 responseVo.setPeriodMinute(getToString(String.valueOf(Minute),2));
 				}
 			}
 		}

        return responseVo;
    }
    
    
    public static int getAgeFromBirthday(Date birthday) {
        Calendar birth = new GregorianCalendar();
        Calendar today = new GregorianCalendar();
        
        birth.setTime(birthday);   
        today.setTime(new Date());

        int factor = 0;
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }
        return today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
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




	/**
	 * @param vo
	 */
	private void batteryInfoProc(RentWaitingRequestVo vo) {
		if(!vo.getBattery().equals(null) && !vo.getBattery().equals(""))
     	{
     		logger.debug("##### 대여대기를 통한 자전거 배터리 정보 UPDATE 시작 #####");
     		
     		/*
     		PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
     		periodicStateReportsRequestVo.setBattery(vo.getBattery());
     		periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
     		*/
     		
     		int battery = Integer.parseInt(vo.getBattery(), 16);
     		String battery_info = null;
     		
     		battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
     		
     		Map<String, String> battery_map = new HashMap<String, String>();
     		battery_map.put("BATTERY", String.valueOf(battery));
     		battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
     		battery_map.put("BICYCLE_ID", vo.getBicycleId());
     		
     		commonService.updateBatteryInfo(battery_map);
     		
     		logger.debug("##### 대여대기를 통한 자전거 배터리 정보 UPDATE 종료 #####");
     	}
	}
    
    


	// 대여대기 실패 메세지
    public RentWaitingResponseVo setFaiiMsg(RentWaitingResponseVo responseVo, RentWaitingRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_RES_RENTWAIT);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
    	
    	return responseVo;
    }
    
    
    
    
    // 대여요청
    @RPCService(serviceId = "Bicycle_02", serviceName = "대여 Request", description = "대여 Request")
    public RentalResponseVo rentRequest(RentalRequestVo vo) {
        
    	System.out.println("######################## 대여  ");
        System.out.println("RentWaitingRequestVo vo :" + vo);
        
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
     	
     	if(!vo.getBattery().equals(null) && !vo.getBattery().equals(""))
     	{
     		logger.debug("##### 대여를 통한 자전거 배터리 정보 UPDATE 시작 #####");
     		
     		/*
     		PeriodicStateReportsRequestVo periodicStateReportsRequestVo = new PeriodicStateReportsRequestVo();
     		periodicStateReportsRequestVo.setBattery(vo.getBattery());
     		periodicStateReportsRequestVo.setBicycleId(vo.getBicycleId());
     		*/
     		
     		int battery = Integer.parseInt(vo.getBattery(), 16);
     		String battery_info = null;
     		
     		battery_info = new CommonUtil().getBattery_Info(vo.getBattery());
     		
     		Map<String, String> battery_map = new HashMap<String, String>();
     		battery_map.put("BATTERY", String.valueOf(battery));
     		battery_map.put("BATTERY_INFO", String.valueOf(battery_info));
     		battery_map.put("BICYCLE_ID", vo.getBicycleId());
     		
     		commonService.updateBatteryInfo(battery_map);
     		
     		logger.debug("##### 대여를 통한 자전거 배터리 정보 UPDATE 종료 #####");
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
			System.out.println("유효한 이용권 없음");
			responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
			responseVo = setFaiiMsg(responseVo, vo);
    		
			return responseVo;
		}
		else
		{
			/**
			 * 대여정보가 정상적으로 저장된 경우, SMS발송
			 */
			com.setStationId(String.valueOf(rentInfo.get("RENT_STATION_ID")));
			com.setUserSeq(String.valueOf(rentInfo.get("USR_SEQ")));
			Map<String, Object> msgInfo = bikeService.getRentMsgInfo(com);
			
			SmsMessageVO sms = new SmsMessageVO();
			sms.setTitle("대여안내");
			sms.setType("S");
			
			
			if(msgInfo != null && msgInfo.get("DEST_NO") != null && !msgInfo.get("DEST_NO").equals(""))
			{
				//String destno = (String)msgInfo.get("DEST_NO");
				String destno = String.valueOf(msgInfo.get("DEST_NO"));
				if(destno != null && !destno.equals("")){
					sms.setDestno(destno);
					SmsSender.sender(sms, SendType.SMS_001, 
							String.valueOf(msgInfo.get("BIKE_NO")),
							String.valueOf(msgInfo.get("STATION_NAME")),
							String.valueOf(msgInfo.get("HOUR")),
							String.valueOf(msgInfo.get("MINUTES")));
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
    	
    	System.out.println("RentWaitingRequestVo vo :" + vo);
    	
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
