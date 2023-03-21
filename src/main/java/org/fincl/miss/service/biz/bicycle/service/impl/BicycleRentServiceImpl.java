package org.fincl.miss.service.biz.bicycle.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import com.mainpay.sdk.utils.ParseUtils;



import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.util.MainPayUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentMapper;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonMapper;
import org.fincl.miss.service.biz.bicycle.vo.QRAdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.fincl.miss.service.biz.bicycle.common.HolidayUtil;


@Service
public class BicycleRentServiceImpl implements BicycleRentService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/*
	 * TODO 반납완료 푸시 메세지. 주변상권 홍보및 연계.
	 */
	//private static final String TARGET_URL = "https://www.bikeseoul.com:446/app/station/moveStationMallPromo.do";

	@Autowired
	//@Qualifier("bizDataSource")
	private BicycleRentMapper bicycleMapper;
	@Autowired
	//@Qualifier("bizDataSource")
	private CommonMapper comm;

	private HolidayUtil holidayUtil;

	@Override
	public boolean rentProcUpdate(CommonVo com, Map<String, Object> userInfo) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfh = new SimpleDateFormat("HH");
		Date today = new Date();
		

		com.setRent_seq(userInfo.get("RENT_SEQ") + "");
		com.setVoucher_seq(userInfo.get("VOUCHER_SEQ") + "");
		com.setRent_cls_cd(userInfo.get("RENT_CLS_CD") + "");
		com.setBikeId(userInfo.get("RENT_BIKE_ID") + "");

		// 이용권 사용정보 테이블
		Map<String, Object> voucher = bicycleMapper.getRentVoucherInfo(com);
		if (voucher == null) 
		{
			return false;
		}

		if (voucher.get("VOUCHER_USE_YN").equals("N")) 
		{
			// 미사용이용권
			bicycleMapper.voucherTableUpdate(voucher);
		}
		// 거치대 정보 업데이트
		com.setRockStusCd("RAS_003");
		bicycleMapper.lockTableUpdate(com);

		// 대여 관리 테이블
		bicycleMapper.rentTableUpdate(com);

		// 자전거 정보 대여로 업데이트 BKS_010
		com.setBikeStusCd("BKS_010");
		bicycleMapper.rentBikeUpdate(com);
		
		// 자전거 배치 이력
		// bicycleMapper.rentBikeLocationUpdate(com);

		// 주차정보 삭제
		bicycleMapper.parkingInfoDelete(com);

		// 추천반납대여소 시간별 정책추출 로직_20170213
		/*
		 * if(!holidayUtil.isHoliday(String.valueOf(sdf.format(today)))){ // 주말이
		 * 아니면 logger.debug("대여 추처반납대여소 로직 진행!!!");
		 * com.setRent_return_ymd(String.valueOf(sdf.format(today)));
		 * com.setHour_column("HOUR_" + String.valueOf(sdfh.format(today)));
		 * com.setRent_return_flag("1"); // flag가 1이면 대여
		 * logger.debug("대여 추처반납대여소 값 설정 끝 ");
		 * bicycleMapper.setRecommendTimePolicy(com); }else{
		 * logger.debug("추천반납대여소 시간별 정책추출 실패 => " +
		 * String.valueOf(sdf.format(today))); }
		 */

		return true;
	}
	
	@Override
	public boolean rentUpdateCancel(String RENT_SEQ) 
	{
		bicycleMapper.rentCancleInsert(RENT_SEQ);
		
		bicycleMapper.rentDelete(RENT_SEQ);
		
		return true;
	}

	@Override
	public BikeRentInfoVo getVoucher(CommonVo com) 
	{

		BikeRentInfoVo voucherInfo = bicycleMapper.getVoucher(com);
		if (voucherInfo == null) 
		{
			voucherInfo = bicycleMapper.getNotUseVoucherInfo(com);
		}

		return voucherInfo;
	}

	@Override
	public boolean reservationInsert(CommonVo com, BikeRentInfoVo info) 
	{

		BikeRentInfoVo bikeInfo = bicycleMapper.getBikeInfo(com);

		com.setRockId(String.valueOf(bikeInfo.getRent_rack_id()));
		com.setRockStusCd("RAS_009");

		if (bikeInfo != null) 
		{
			bikeInfo.setUsr_seq(info.getUsr_seq());
			bikeInfo.setVoucher_seq(info.getVoucher_seq());
			bikeInfo.setRent_cls_cd(info.getRent_cls_cd());

			bicycleMapper.rentTableInsert(bikeInfo);
			logger.debug("##### BicycleRentServiceImpl updateRockStus => "
					+ com.getRockId() + ", " + com.getRockStusCd());
			bicycleMapper.lockTableUpdate(com);
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> getUseBikeInfo(CommonVo com) 
	{
		return bicycleMapper.getUseBikeInfo(com);
	}

	@Override
	public Map<String, Object> getUseBikeInfoFULL(CommonVo com) 
	{
		return bicycleMapper.getUseBikeInfoFULL(com);
	}

	@Override
	public void updateLockTEst(CommonVo com) 
	{
		bicycleMapper.updateLockTEst(com);

		try 
		{
			Thread.sleep(10000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}

	}

	@Override
	public RentHistVo getForReturnUse(CommonVo com) 
	{
		return bicycleMapper.getForReturnUse(com);
	}

	@Override
	public Map<String, Object> getOverFeePolicy(Map<String, Object> info) 
	{
		return bicycleMapper.getOverFeePolicy(info);
	}

	@Override
	public void procReturn(RentHistVo info) 
	{
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat _sdfh = new SimpleDateFormat("HH");
		Date today = new Date();
		String rentSeq = "";
		boolean b_overfee = false;

		logger.debug("QRKICK_procReturn :: {}", info); // 로그 수정....2018.04.02

		// 자전거 주차 정보 INSERT PARKING
		try 
		{
			bicycleMapper.insertParkingInfo(info);
		} 
		catch (Exception e) 
		{
		}

		// 자전거 과거 배치 이력 UPDATE LOCATION_BIKE
		bicycleMapper.updateBikeLocation(info);

		// 자전거 배치 이력 INSERT LOCATION_BIKE
		bicycleMapper.insertBikeLocation(info);

		// 자전거 정보 UPDATE BIKE
		bicycleMapper.updateBike(info);

	//	bicycleMapper.updateBikeGPS(info);	//앞단에서 업데이트 
	
		rentSeq = String.valueOf(bicycleMapper.getRentSeq(info));

		if (rentSeq != null && rentSeq != "") 
		{
			info.setRENT_SEQ(rentSeq);
		}
		// T-APP 수정
		// 같은 정거장 일대 추가...
		// 사용안함 처리..
		// 대여 정거장 하고 반납 정거장이 다른면 이용처리 해야함....
		// 사용시간이 2분 언더일때는 시스템 시간으로 해야할...

		// 대여 이력 INSERT RENT_HIST
		bicycleMapper.insertRentHist(info);

		Map<String, String> GPS_DATA = new HashMap<String, String>();
		GPS_DATA.put("RENT_SEQ", rentSeq);
		GPS_DATA.put("RENT_HIST_SEQ", info.getRENT_HIST_SEQ());
		//bicycleMapper.insertRentMove_Info(GPS_DATA);
		
		bicycleMapper.deleteRentGPSDATA(info);
		// 대여 정보 DELETE RENT
	//	bicycleMapper.deleteRentInfo(info);		////이동 함. 2021.05.10

		// 대여 예약도 DELET RENT
		bicycleMapper.deleteRentInfo_rserved(info);
		OverFeeVO fee = new OverFeeVO();

		// 대여 초과 요금 여부 확인 INSERT RENT_OVER_FEE
		if (info.getOVER_FEE_YN().equals("Y")) 
		{
			bicycleMapper.insertRentOverFee(info); 
			// TB_SVC_RENT_OVER_FEE
			// (OVER_FEE_PROCESS_CLS_CD
			// -> OPD_002
			
			MainPayUtil MainPayutil = new MainPayUtil();
			fee = comm.getOverFeeRETURN(info.getUSR_SEQ());
			HashMap<String, String> parameters = new HashMap<String, String>();
			String billkey = fee.getBillingKey() ;
    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
    			parameters.put("billkey", billkey);	// 정기결제 인증 키
    		}
    		parameters.put("goodsId", "BIL_009");
    		parameters.put("goodsName", "추가과금");
    		parameters.put("amount", fee.getOverFee());
        	
    		String responseJson = MainPayutil.approve(parameters,"Y");
    		
    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
			String resultCode = (String) responseMap.get("resultCode");
			String resultMessage = (String) responseMap.get("resultMessage");
    		
    		/*
    		String responseJson ="{aa=aa;b=bb}";
    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
    		String resultMessage="test error !!";
    		String resultCode = "23";
    		*/
			if(!"200".equals(resultCode)) {	// API 호출 실패
				logger.debug("Return Pay Fail-->> ["+resultMessage + "]");
				comm.setOverFeePayReset(fee);
			}
			else
			{	// API 호출 성공
				try 
				{
					fee.setPaymentMethodCd("BIM_001");
					fee.setResultCD("0000");
					fee.setPaymentStusCd("BIS_001");
					fee.setMb_serial_no(parameters.get("mbrRefNo"));	//mb_serial_no
					Map dataMap = (Map)responseMap.get("data");
					fee.setPaymentConfmNo((String) dataMap.get("refNo"));	//paymentConfmNo
					fee.setTotAmt(fee.getOverFee());
					fee.setOrder_certify_key((String)responseMap.get("applNo"));
					fee.setProcessReasonDesc(resultMessage);
					
					fee.setRentHistSeq(info.getRENT_HIST_SEQ());	//add rent_hist_seq by dearkim
					
					//add 2022.11.23  paymentClsCd
					fee.setPaymentClsCd("BIL_009");
					//add
					logger.debug("rent_hist_seq {} , mb_serial_no {} , payment_Conf_No {}",fee.getRentHistSeq(),fee.getMb_serial_no(),fee.getPaymentConfmNo());
					
					Map<String, String> returnMap = new HashMap<String, String>();
					returnMap = comm.getPaymentInfoExist(fee);
					
					logger.debug("PAYMENT_INFO_EXIST check-->> " +returnMap.get("PAYMENT_INFO_EXIST"));
					if(returnMap.get("PAYMENT_INFO_EXIST").equals("N"))
					{
						logger.debug("##### 초과요금 결제정보가 없다. insert payment_seq ##### {}",fee.getRentHistSeq());
						
						int result = comm.addTicketPayment(fee);
					}else{
						logger.debug("##### 초과요금 결제정보가 이미 있다. #####");
					}
					
					b_overfee = true;
					
					int result = comm.setOverFeePayComplete(fee);
					
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		
		}
		else if (info.getOVER_FEE_YN().equals("B")) 	//이용1시간 초과 시 !!!!
		{
			bicycleMapper.insertRentOverFee(info); 
			// TB_SVC_RENT_OVER_FEE
			// (OVER_FEE_PROCESS_CLS_CD
			// -> OPD_002
			
			MainPayUtil MainPayutil = new MainPayUtil();
			fee = comm.getOverFeeRETURN(info.getUSR_SEQ());
			HashMap<String, String> parameters = new HashMap<String, String>();
			String billkey = fee.getBillingKey() ;
    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
    			parameters.put("billkey", billkey);	// 정기결제 인증 키
    		}
    		parameters.put("goodsId", "BIL_008");
    		parameters.put("goodsName", "이용시간");
    		parameters.put("amount", fee.getOverFee());
        	
    		
    		String responseJson = MainPayutil.approve(parameters,"Y");
    		
    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
			String resultCode = (String) responseMap.get("resultCode");
			String resultMessage = (String) responseMap.get("resultMessage");
    		
    		
    		/*
    		String responseJson ="{aa=aa;b=bb}";
    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
    		String resultMessage="B error !!";
    		String resultCode = "200";
    		*/
    		
			if(!"200".equals(resultCode)) {	// API 호출 실패
				logger.debug("Return Pay Fail-->> ["+resultMessage + "]");
				comm.setOverFeePayReset(fee);	//PAYMENT_ATT_CNT = 0 으로 초기화...
			}
			else
			{	// API 호출 성공
				try 
				{
					fee.setPaymentMethodCd("BIM_001");
					fee.setResultCD("0000");
					fee.setPaymentStusCd("BIS_001");
					fee.setMb_serial_no(parameters.get("mbrRefNo"));	//mb_serial_no
					Map dataMap = (Map)responseMap.get("data");
					fee.setPaymentConfmNo((String) dataMap.get("refNo"));	//paymentConfmNo
					fee.setTotAmt(fee.getOverFee());
					fee.setOrder_certify_key((String)responseMap.get("applNo"));
					fee.setProcessReasonDesc(resultMessage);
					
					fee.setRentHistSeq(info.getRENT_HIST_SEQ());	//add rent_hist_seq by dearkim
					
					//add 2022.11.23  paymentClsCd
					fee.setPaymentClsCd("BIL_008");
					//add
					logger.debug("rent_hist_seq {} , mb_serial_no {} , payment_Conf_No {}",fee.getRentHistSeq(),fee.getMb_serial_no(),fee.getPaymentConfmNo());
					
					Map<String, String> returnMap = new HashMap<String, String>();
					returnMap = comm.getPaymentInfoExist(fee);
					
					logger.debug("PAYMENT_INFO_EXIST check-->> " +returnMap.get("PAYMENT_INFO_EXIST"));
					if(returnMap.get("PAYMENT_INFO_EXIST").equals("N"))
					{
						logger.debug("##### 이용요금 결제정보가 없다. insert payment_seq ##### {}",fee.getRentHistSeq());
						
						int result = comm.addTicketPayment(fee);
					}else{
						logger.debug("##### 이용요금 결제정보가 이미 있다. #####");
					}
					
			//		b_overfee = true;
					
					int result = comm.setOverFeePayComplete(fee);
					
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		// getReportTimeHistory(info); // 반납 상태시간 HISTORY INSERT_20161220_JJH

		/**
		 * 절감탄소량 마일리지 Proc_20160113_JJH_START
		 */
		info.setMILEAGE_CD("MIG_003");
		info.setMILEAGE_DAY_CD("MSI_030");
		info.setMILEAGE_DAY_MAX_CD("MSI_031");
		info.setMILEAGE_POLICY_OPEN_CD("MSI_032");
		info.setMILEAGE_POLICY_NM("절감탄소량");

		//mileagePolicyProc(info);
		// 절감탄소량 마일리지 Proc_20160113_END

		/**
		 * 상태미전송 오류처리를 위해 반납/관리자 설치시에도 상태보고 시간 필드를 업데이트하는 기능 추가.
		 */
		bicycleMapper.updateDeviceState(info);

		bicycleMapper.deleteRentInfo(info);		//2021.05.10 rent 지우기 문자전으로 이동.함.
		// SMS전송.

		String returnStationNo = String.valueOf(bicycleMapper.getStationNo(String.valueOf(info.getRETURN_RACK_ID()))); // 대여소
		String returnStationName = String.valueOf(bicycleMapper.getStationName(String.valueOf(info.getRETURN_RACK_ID()))); // 대여소
		// 번호가져오기_20161121_JJH
		logger.debug("#### SMS_MESSAGE ==>station {} statName {} " , returnStationNo,returnStationName);

	
		if (info.getUSR_MPN_NO() != null && !info.getUSR_MPN_NO().equals(""))
		{
			SimpleDateFormat sdf;

			sdf = new SimpleDateFormat("MM월dd일 HH시mm분");

			try 
			{
				SmsMessageVO smsVo = new SmsMessageVO();
				smsVo.setDestno(info.getUSR_MPN_NO());
		//		smsVo.setMsg("반납되었습니다");
				
				
				if(b_overfee == false)
				{
					if(info.getOVER_FEE_YN().equals("B"))
					{
						smsVo.setTemplate_code("bizp_2022112311555818895227621");
						smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 킥보드 반납완료. 이용시간 "+ info.getUSE_MI() + "분으로 "+info.getOVER_FEE()+"원이 결제됩니다.");
					}
					else
					{
						
				//		smsVo.setTemplate_code("bizp_2022100709591018895833260");
					
						if(info.getBIKE_SE_CD().equals("BIK_001"))
						{
							smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 자전거 반납완료. 이용시간은 "+ info.getUSE_MI() + "분 입니다.");
						}
						else
						{
							if(returnStationName != null){
								smsVo.setTemplate_code("bizp_2022113010150818895333215");
								smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 킥보드 "+ returnStationName+"에 반납완료. 이용시간은 "+ info.getUSE_MI() + "분 입니다.");
							}else{
								smsVo.setTemplate_code("bizp_2022100709591018895833260");
								smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 킥보드 반납완료. 이용시간은 "+ info.getUSE_MI() + "분 입니다.");
							}
						}
					}
				}
				
				else
				{
					smsVo.setDestno(info.getUSR_MPN_NO());
					smsVo.setTemplate_code("bizp_2022100710435141820588238");
					
					if(info.getBIKE_SE_CD().equals("BIK_001"))
					{
						smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 자전거  "+ info.getUSE_MI() + "분이용 반납완료. 초과이용은 " + fee.getOverMi() + "분으로 " +  fee.getOverFee() + "원 결제완료");
					}
					else
					{
						smsVo.setMsg("<레일킥> " + info.getBIKE_NO() +" 킥보드  "+ info.getUSE_MI() + "분이용 반납완료. 초과이용은 " + fee.getOverMi() + "분으로 " + fee.getOverFee() + "원이 결제되었습니다.");
					}
				}
				
				if(info.getUSR_MPN_NO().equals("01051932653")||info.getUSR_MPN_NO().equals("01067774004") || info.getUSR_MPN_NO().equals("01080193152") )
				{
					logger.debug("##### SCH_MANAGER Don't send SMS  . #####");
				}
				else
					SmsSender.sender(smsVo);
				
				
				if(info.getEND_DTTM() == null || info.getEND_DTTM() ==""){
					
					logger.debug("##### MAIN_REFRESH . #####");
					//대여완료 화면이 안뜬다면 앱화면 상단의 레일킥 로고를 눌러 화면을 갱신해주세요.
					//반납내역 화면이 아니시면 앱화면 상단의 레일킥 로고를 눌러 화면을 갱신해주세요.  bizp_2022120713274741820614956
					//화면 갱신
					smsVo.setAutoSendYn("K");			
				//	smsVo.setTemplate_code("bizp_2022120617021941820363800");
				//	smsVo.setMsg(info.getBIKE_NO()+" 킥보드가 대여중이시면 앱화면위의 레일킥 아이콘을 눌러서 화면을 갱신해주세요.");
					
					smsVo.setTemplate_code("bizp_2022120713274741820614956");
					smsVo.setMsg("반납내역 화면이 아니시면 앱화면 상단의 레일킥 로고를 눌러 화면을 갱신해주세요.");
					
					if(info.getUSR_MPN_NO().equals("01051932653")||info.getUSR_MPN_NO().equals("01067774004") || info.getUSR_MPN_NO().equals("01080193152") )
					{
						logger.debug("##### SCH_MANAGER Don't send SMS  . #####");
					}
					else
						SmsSender.sender(smsVo);
				}
				//
			} 
			catch (Exception e) 
			{

			}
		}
	}

	public static int getAgeFromBirthday(Date birthday) 
	{
		Calendar birth = new GregorianCalendar();
		Calendar today = new GregorianCalendar();

		birth.setTime(birthday);
		today.setTime(new Date());

		int factor = 0;

		if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) 
		{
			factor = -1;
		}
		return today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
	}

	/**
	 * @param info
	 * @param paramMap
	 */
	private void mileagePolicyProc(RentHistVo info) 
	{
		logger.debug(String.valueOf(info.getMILEAGE_POLICY_NM()) + "마일리지 Proc Start~!!");
		logger.debug(info.toString());

		RentHistVo mbRentInfo = new RentHistVo(); // 회원 별 등록되어 있는 마일리지 정책에 의거
													// 대여/반납 정보를 비교하여 얻은 리턴값
		Map<String, String> mileageReturnMap = new HashMap<String, String>();
		Map<String, String> saveCarbonStationMap = new HashMap<String, String>();

		if (bicycleMapper.getPolicyOpenYn(String.valueOf(info.getMILEAGE_POLICY_OPEN_CD())).equals("Y")
				&& bicycleMapper.getMemberYn(info).equals("Y")) 
		{ 
			// 반납 => 절감탄소량
			// 마일리지 정책
			// Open
			// 여부/정회원
			// 여부_20170112
			logger.debug(String.valueOf(info.getMILEAGE_POLICY_NM()) + " 마일리지 정책 Open~!!");

			if (String.valueOf(info.getMILEAGE_CD()).equals("MIG_003")) 
			{ 
				// 마일리지
				// 코드가
				// 절감탄소량
				// 마일리지인가??
				saveCarbonStationMap = bicycleMapper.getSaveCarbonStationInfo(info); // 탄소절감 등록 대여/반납 대여소 정보
															// 가져오기_20170121
				info.setSAVE_CARBON_RENT_STATION_ID(String.valueOf(saveCarbonStationMap.get("SAVE_CARBON_RENT_STATION_ID")));
				info.setSAVE_CARBON_RETURN_STATION_ID(String.valueOf(saveCarbonStationMap.get("SAVE_CARBON_RETURN_STATION_ID")));

				if (saveCarbonStationMap != null) 
				{
					logger.debug("saveCarbonStationMap => SAVE_CARBON_RENT_STATION_ID : " + saveCarbonStationMap.get("SAVE_CARBON_RENT_STATION_ID")
							+ ", SAVE_CARBON_RETURN_STATION_ID : " + saveCarbonStationMap.get("SAVE_CARBON_RETURN_STATION_ID"));
				} 
				else 
				{
					logger.debug("saveCarbonStationMap가 NULL이야. 탄소절감 등록 대여/반납 대여소 정보가 없다는 뜻이야~!!");
				}

				mbRentInfo = bicycleMapper.getSaveCarbonInfoCompare(info); // 회원
																			// 별
																			// 탄소절감
																			// 정보
																			// 조회
																			// 및
																			// 대여/반납
																			// 대여소
																			// 마일리지
																			// 적용여부
																			// 확인_20170112

				if (mbRentInfo != null)
					logger.debug("mbRentInfo => " + mbRentInfo.toString());
				else
					logger.debug("mbRentInfo 가 NULL이야. 회원 별 탄소절감 정보 조회 및 대여/반납 대여소 마일리지 적용안되는거지~!!");

			} 
			else if (String.valueOf(info.getMILEAGE_CD()).equals("MIG_004")) 
			{ // 마일리지
																					// 코드가
																					// 추천대여소
																					// 마일리지인가??
				mbRentInfo = recommendReturnProc(info, mbRentInfo);
			}

			if (mbRentInfo != null) 
			{ // 마일리지 적립 대상유무 확인조건

				mbRentInfo.setMILEAGE_CD(info.getMILEAGE_CD());
				mbRentInfo.setMILEAGE_DAY_CD(info.getMILEAGE_DAY_CD());
				mileageReturnMap = bicycleMapper.getMileageMaxPoint(info); // 최대
																			// 일
																			// 마일리지
																			// 부여
																			// 했는가?

				if (Integer.parseInt(String.valueOf(mileageReturnMap.get("MILEAGE_SUM_POINT"))) < Integer.parseInt(String.valueOf(mileageReturnMap.get("MAX_MILEAGE_POINT")))) 
				{ // 아직
																				// 여유가
																				// 있다면
																				// 마일리지
																				// 부여하자~!!
					logger.debug("아직 최대치 마일리지가 부여되지 않았군~!!");

					bicycleMapper.insertSaveCarbonMileage(mbRentInfo);// 마일리지
																		// 부여~!!
				} else {
					logger.debug("이미 최대 마일리지 부여되었음~!!");
				}
			} else {
				logger.debug("mbRentInfo is null~!!");
			}

		} else {
			logger.debug(String.valueOf(info.getMILEAGE_POLICY_NM()) + " 마일리지 정책 Close~!!");
		}

		logger.debug(String.valueOf(info.getMILEAGE_POLICY_NM()) + " 마일리지 Proc End~!!");
	}

	/**
	 * @param info
	 * @param mbRentInfo
	 * @return
	 */
	private RentHistVo recommendReturnProc(RentHistVo info, RentHistVo mbRentInfo) 
	{
		if (String.valueOf(bicycleMapper.getRecommendExistsYn(info)).equals("Y")) 
		{
			logger.debug("추천반납대여소 정보 존재함" + String.valueOf(info.getUSR_SEQ()));

			// 반납한 History 정보 가져오기
			String rentHistSeq = String.valueOf(bicycleMapper.getRentHistSeq(info));

			// 반납한 대여소에 반납 랭크정보 가져오기
			String returnRank = String.valueOf(bicycleMapper.getReturnRank(info));

			// RentHist_SEQ, 랭크정보 테이블 UPDATE하기 (TB_SYS_RECOMMEND_STATION)
			info.setRENT_HIST_SEQ(rentHistSeq);
			info.setRETURN_RANK(returnRank);

			bicycleMapper.updateRecommendInfo(info);

			// mbRentInfo 에 회원정보 넣기
			mbRentInfo = bicycleMapper.getRecommendStationInfoCompare(info);

			if (mbRentInfo != null) 
			{
				logger.debug("mbRentInfo => " + mbRentInfo.toString());
			} 
			else 
			{
				logger.debug("mbRentInfo 가 NULL이야. 추천반납대여소 조회 및 대여/반납 대여소 마일리지 적용안되는거지~!!");
			}
		} 
		else
		{

			logger.debug("추천반납대여소 버튼조차 누르지 않았음~!!" + info.toString());

			mbRentInfo = null;
		}
		return mbRentInfo;
	}

	/**
	 * @param info
	 */
	private void getReportTimeHistory(RentHistVo info) 
	{
		System.out.println("######################## 반납 상태시간 History Start ########################");
		RentalRequestVo com = new RentalRequestVo();
		com.setBicycleId(info.getRENT_BIKE_ID());
		com.setBicycleState("PRO_002");
		com.setMountsId(info.getRETURN_RACK_ID());

		if (info.getCASCADE_YN().equals("Y")) 
		{
			com.setReturnForm("01");
		} 
		else 
		{
			com.setReturnForm("00");
		}

		String sMaxTime = "070000";
		String sMinTime = "220000";

		String sCurTime = new SimpleDateFormat("yyyyMMddHHmmss",
				java.util.Locale.KOREA).format(new java.util.Date());
		String sCurDate = new SimpleDateFormat("yyyyMMdd",
				java.util.Locale.KOREA).format(new java.util.Date());

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		Calendar strCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();

		try 
		{
			date = dateFormat.parse(sCurDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (sCurTime.compareTo(String.valueOf(sCurDate + "000000")) >= 0 && sCurTime.compareTo(String.valueOf(sCurDate + "070000")) < 0) 
		{
			strCal.setTime(date);
			strCal.add(Calendar.DATE, -1);

			sMaxTime = sCurDate + sMaxTime;
			sMinTime = dateFormat.format(strCal.getTime()) + sMinTime;

			System.out.println("######################## if 시작시간, 종료시간 ######################## => " 
					+ String.valueOf(sMinTime) + ", " + String.valueOf(sMaxTime));
		} 
		else 
		{
			endCal.setTime(date);
			endCal.add(Calendar.DATE, 1);

			sMaxTime = dateFormat.format(endCal.getTime()) + sMaxTime;
			sMinTime = sCurDate + sMinTime;
			System.out.println("######################## else 시작시간, 종료시간 ######################## => " 
					+ String.valueOf(sMinTime) + ", " + String.valueOf(sMaxTime));
		}

		System.out.println("######################## 반납 실시간 확인 ==> " + String.valueOf(sCurTime));

		if (sCurTime.compareTo(sMinTime) >= 0 && sCurTime.compareTo(sMaxTime) < 0) 
		{
			System.out.println("######################## 반납시간이 22:00 ~ 07:00 사이임 ==> "
					+ String.valueOf(sCurTime));
			bicycleMapper.insertPeriodInfo(com);
		} 
		else 
		{
			System.out.println("######################## 반납시간이 조건 밖임~!! ==> ");
		}

		System.out.println("######################## 반납 상태시간 History End ########################");
	}

	@Override
	public int getNoParkingRock(CommonVo com) {
		return bicycleMapper.getNoParkingRock(com);
	}

	@Override
	public Map<String, Object> getRentHist(CommonVo com) {
		return bicycleMapper.getRentHist(com);
	}

	@Override
	public void insertRentMoveInfo(Map<String, Object> hist, ArrayList<Integer> lat, ArrayList<Integer> lon, int packNum) 
	{

		for (int i = 0; i < lat.size(); i++) 
		{
			// 분도 계산...
			hist.put("MOVE_ORD", (i + 1) + ((packNum - 1) * 20));
			double latDouble = lat.get(i) * 0.000001;
			double latip = latDouble - (latDouble - (int) latDouble);
			double latfp = (latDouble - (int) latDouble) * 100 / 60;
			// 소숫점 6자리까지 계산.(버림)
			double latp = Math.floor((latip + latfp) * 1000000d) / 1000000d;
			hist.put("MOVE_LATITUDE", latp);
			double lonDouble = lon.get(i) * 0.000001;
			double lonip = lonDouble - (lonDouble - (int) lonDouble);
			double lonfp = (lonDouble - (int) lonDouble) * 100 / 60;
			// 소숫점 6자리까지 계산.(버림)
			double lonp = Math.floor((lonip + lonfp) * 1000000d) / 1000000d;
			hist.put("MOVE_LONGITUDE", lonp);
			try 
			{
				bicycleMapper.insertRentMoveInfo(hist);
			} 
			catch (Exception e) 
			{
			}
		}

	}

	@Override
	public void procAdminMove(CommonVo com) {

		// 자전거 상태 update
		com.setBikeId(comm.getBikeId(com));
		com.setBikeStusCd("BKS_011");
		bicycleMapper.rentBikeUpdate(com);

		// 자전거 배치 이력 UPDATE
		bicycleMapper.rentBikeLocationUpdate(com);

		// 자전거 주차정보 삭제
		bicycleMapper.parkingInfoDelete(com);

		// 거치대 정보 update
		com.setRockStusCd("RAS_003");
		bicycleMapper.lockTableUpdate(com);

		// 자전거 재배치 이력 등록.
		Map<String, Object> hist = new HashMap<String, Object>();
		hist.put("bicycleId", com.getBicycleId());

		/*
		if (bicycleMapper.checkRelocateHist(hist) == 0) {
			bicycleMapper.insertRelocateHist(com);
		}
		*/
	}

	@Override
	public boolean procAdminMounting(CommonVo com, QRAdminMountingRequestVo vo) {
		// 이동거리
		/*
		 * 이동 거리 삭제 int km = Integer.parseInt(vo.getDistance().substring(0,2),
		 * 16); int me = Integer.parseInt(vo.getDistance().substring(2,4), 16);
		 * int distance = (km*1000)+(me*10);
		 */

		RentHistVo info = new RentHistVo();
		AdminMoveRequestVo moveVo = new AdminMoveRequestVo();

		String bikeId = comm.getBikeId(com);

		info.setRENT_BIKE_ID(bikeId);

		com.setBikeId(bikeId);

		info.setCASCADE_YN("N");
		info.setUSE_DIST("0");
		info.setRETURN_RACK_ID(com.getRockId());

		if (comm.checkParkingInfo(com) != null) {
			bicycleMapper.parkingInfoDelete(com);
		}

		// 자전거 주차 정보 INSERT PARKING
		bicycleMapper.insertParkingInfo(info);

		// 자전거 배치 이력이 존재하는 경우 이전 데이터 업데이트.
		//bicycleMapper.rentBikeLocationUpdate(com);

		// 자전거 배치 이력 INSERT LOCATION_BIKE
		//bicycleMapper.insertBikeLocation(info);

		/**
		 * 자전거 장애신고 정보가 존재하면, 자전거 정보는 설치 후 고장상태로 변경 저장한다. 반드시 장애가 존재하는 자전거는 사전
		 * 수리완료 처리 후 설치 진행.
		 * 
		 */
		String faultSeq = comm.getFaultSeq(com);

		if (faultSeq != null) 
		{
			com.setUserSeq(faultSeq);
			comm.deleteFaultInfo(com);
			comm.changeValidBike(com);
		}

		// if(faultSeq == null)
		// {
		// 자전거 정보를 거치상태로 UPDATE BIKE
		if(com.getStationId().equals("ST-1099")){
			
			logger.debug("procAdminMounting : station is Operation Center !!!!!!!!! ");
			info.setSTATION_USE_YN("C");
			
		}
		bicycleMapper.updateBike(info);

		// 자전거 재배치 이력 등록.
		Map<String, Object> hist = new HashMap<String, Object>();
		hist.put("bicycleId", vo.getBicycleId());

		/**
		 * 자전거 대여정보가 존재하면 이력정보를 삭제한 후 반납 SMS 발송
		 */
		checkRentInfoProc(com, info);

		// 점검시간 정보 업데이트
		bicycleMapper.updateDeviceState(info);

		/**
		 * 
		 */

		return true;

	}

	/**
	 * @param com
	 * @param info
	 */
	private void checkRentInfoProc(CommonVo com, RentHistVo info) 
	{
		RentHistVo histInfo = bicycleMapper.checkInvalidRentInfo(com);	//RENT_YN = 'Y' 체크 
		
		if (histInfo != null) 
		{
			histInfo.setRETURN_RACK_ID(info.getRETURN_RACK_ID());
			histInfo.setTRANSFER_YN("N");
			/**
			 * 자전거 대여정보 삭제(설치시 반납되지 않은 자전거 대여이력정보 삭제) 추후 확정되면 우선 해제. Why? 관리자
			 * 설치의 경우 반납위치와 설치위치가 다를수 있기 때문에...이력에 어떤 것으로 남길것인가? 현재는 삭제하나, 요청에
			 * 따라 설치 위치를 이력으로 남길 수도 있음.
			 */
			bicycleMapper.insertInvalidRentHist(histInfo);
			//bicycleMapper.deleteRentGPSDATA(histInfo);
			bicycleMapper.deleteRentInfo(info);
			// SMS전송.

			String returnStationNo = String.valueOf(bicycleMapper.getStationNo(String.valueOf(info.getRETURN_RACK_ID())));
			String returnStationName = String.valueOf(bicycleMapper.getStationName(String.valueOf(info.getRETURN_RACK_ID()))); // 대여소

			SimpleDateFormat sdf;
			sdf = new SimpleDateFormat("MM월dd일 HH시mm분");
			Date today = new Date();

			if (histInfo.getUSR_MPN_NO() != null && !histInfo.getUSR_MPN_NO().equals("")) 
			{
				if (histInfo.getRENT_MTH_CD() == null || histInfo.getRENT_MTH_CD().equals("") || histInfo.getRENT_MTH_CD().equals("CHN_001"))
				{
					try
					{
						SmsMessageVO smsVo = new SmsMessageVO();
						smsVo.setDestno(histInfo.getUSR_MPN_NO());
						
					//	smsVo.setTemplate_code("bizp_2022100710435141820588238");
						
					//	smsVo.setMsg(SendType.SMS_090, histInfo.getBIKE_NO(), String.valueOf(sdf.format(today)), returnStationName);
						
					//	SmsSender.sender(smsVo);
						
						logger.debug("checkRentInfoProc : [admin_install] rent_info is delete  !!!!!!");
						
					} 
					catch (Exception e) 
					{
					}
				} 
				else if (histInfo.getRENT_MTH_CD().equals("CHN_002")) 
				{
					// T-APP 문자 전송!
					TAPPMessageVO TAPPVo = new TAPPMessageVO();
					TAPPVo.setUsr_seq(histInfo.getUSR_SEQ());
					TAPPVo.setBike_no(histInfo.getBIKE_NO());
					TAPPVo.setOver_fee(histInfo.getOVER_FEE());
					TAPPVo.setOver_mi(histInfo.getOVER_MI());
					TAPPVo.setNotice_se(SendType.SMS_002.getCode());
					TAPPVo.setMsg(SendType.SMS_090, histInfo.getBIKE_NO(),
							String.valueOf(sdf.format(today)), returnStationNo);
					
				//	SmsSender.TAPPsender(TAPPVo);
				}
			}

		}
	}

	@Override
	public Map<String, Object> getOverFeeMaxPolicy(Map<String, Object> fee) 
	{
		return bicycleMapper.getOverFeeMaxPolicy(fee);
	}

	@Override
	public Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee) 
	{
		return bicycleMapper.getOverFeeMinPolicy(fee);
	}

	@Override
	public int getUserWeight(String usr_SEQ) 
	{
		int result = 65;
		try 
		{
			result = bicycleMapper.getUserWeight(usr_SEQ);
		} 
		catch (Exception e) 
		{
			logger.debug("no data :: return 0");
		}

		return result;
	}

	@Override
	public Map<String, Object> getRentMsgInfo(CommonVo com) 
	{

		return bicycleMapper.getRentMsgInfo(com);
	}

	@Override
	public Map<String, Object> getRentMsgInfo2(CommonVo com) 
	{

		return bicycleMapper.getRentMsgInfo2(com);
	}

	@Override
	public void removeRelocateHist(CommonVo com) 
	{

		RentHistVo info = new RentHistVo();

		info.setRETURN_RACK_ID(com.getRockId());
		info.setRENT_BIKE_ID(comm.getBikeId(com));
		info.setCASCADE_YN("N");
		info.setUSE_DIST("");

		if (comm.checkParkingInfo(com) != null) 
		{
			bicycleMapper.parkingInfoDelete(com);
		}

		// 자전거 주차 정보 INSERT PARKING
		bicycleMapper.insertParkingInfo(info);

		// 자전거 배치 이력 INSERT LOCATION_BIKE
		bicycleMapper.insertBikeLocation(info);

		// 자전거 정보 UPDATE BIKE
		bicycleMapper.updateBike(info);

		// 재배치 이력 삭제.
		bicycleMapper.removeRelocateHist(com);
	}

	/**
	 * Cascade 반납인 경우, 이전 자전거 ID로 거치된 거치대 ID를 조회한다.
	 * 
	 */
	@Override
	public Map<String, Object> selectCascadParkingRock(CommonVo info) 
	{
		return bicycleMapper.selectCascadParkingRock(info);
	}

	@Override
	public void deleteParkingInfoOnly(RentHistVo info) 
	{
		bicycleMapper.deleteParkingInfoOnly(info);
	}

	@Override
	public void deleteParkingInfoCascade(RentHistVo info) 
	{
		bicycleMapper.deleteParkingInfoCascade(info);
	}

	@Override
	public void insertParkingInfo(RentHistVo info) 
	{
		bicycleMapper.insertParkingInfo(info);
	}

	@Override
	public void insertBikeLocation(RentHistVo info) 
	{
		bicycleMapper.insertBikeLocation(info);
	}

	@Override
	public void updateBike(RentHistVo info) 
	{
		bicycleMapper.updateBike(info);
	}
	
	@Override
	public void updateBikeGPS(RentHistVo info) 
	{
		bicycleMapper.updateBikeGPS(info);
	}
	
	@Override
	public void updateBike_NOGPS(RentHistVo info) 
	{
		bicycleMapper.updateBike_NOGPS(info);
	}

	@Override
	public void deleteDuplicatedParkingInfo(RentHistVo info) 
	{
		bicycleMapper.deleteDuplicatedParkingInfo(info);

	}

	@Override
	public void deleteDuplicatedCascadeParkingInfo(RentHistVo info) 
	{
		bicycleMapper.deleteDuplicatedCascadeParkingInfo(info);

	}

	@Override
	public void insertPeriodParkingInfo(RentHistVo info) 
	{
		bicycleMapper.insertPeriodParkingInfo(info);
	}

	@Override
	public void insertPeriodBikeLocation(RentHistVo info) 
	{
		bicycleMapper.insertPeriodBikeLocation(info);
	}

	@Override
	public void insertInvalidRentHist(RentHistVo info) 
	{
		bicycleMapper.insertInvalidRentHist(info);
	}

	@Override
	public RentHistVo checkInvalidRentInfo(CommonVo info) 
	{
		return bicycleMapper.checkInvalidRentInfo(info);
	}

	@Override
	public void deleteRentInfo(RentHistVo info) 
	{
		bicycleMapper.deleteRentInfo(info);
	}

	@Override
	public void deleteRentInfo_rserved(RentHistVo info) 
	{
		bicycleMapper.deleteRentInfo(info);
	}

	@Override
	public void setLastChkTime(RentalRequestVo info) 
	{
		bicycleMapper.setLastChkTime(info);
	}

	@Override
	public void insertPeriodInfo(RentalRequestVo info) 
	{
		info.setBicycleState("PRO_001");
		bicycleMapper.insertPeriodInfo(info);
	}

	public Map<String, String> chkOurBike(CommonVo com) 
	{
		return bicycleMapper.chkOurBike(com);
	}

	public Map<String, String> getSerialNumberInfo(SerialNumberRentalRequestVo info) 
	{
		return bicycleMapper.getSerialNumberInfo(info);
	}

	public BikeRentInfoVo getUseVoucherInfo(CommonVo com) 
	{
		BikeRentInfoVo voucherInfo = bicycleMapper.getUseVoucherInfo(com);

		if (voucherInfo == null) 
		{
			voucherInfo = bicycleMapper.getNotUseVoucherInfo(com);
		}

		return voucherInfo;
	}

	public String getLanguageCode(CommonVo com) 
	{
		return bicycleMapper.getLanguageCode(com);
	}

	// 2019.02.20
	public Map<String, String> getBikeFirmwareVersion(CommonVo com) 
	{
		return bicycleMapper.getBikeFirmwareVersion(com);
	}

	public void insertQRLog(QRLogVo QRLog) 
	{
		bicycleMapper.insertQRLog(QRLog);
	}

	@Override
	public void updateQRLog(QRLogVo QRLog) 
	{
		bicycleMapper.updateQRLog(QRLog);
	}

	@Override
	public void updatevoucherTAPP_2MIN_UNDER(String Voucher_seq) 
	{
		// TODO Auto-generated method stub
		bicycleMapper.updatevoucherTAPP_2MIN_UNDER(Voucher_seq);
	}

	@Override
	public void updatevoucherTAPP_2MIN_OVER(String Voucher_seq) 
	{
		// TODO Auto-generated method stub
		bicycleMapper.updatevoucherTAPP_2MIN_OVER(Voucher_seq);
	}

	@Override
	public double getBikeMoveDist(CommonVo com) 
	{
		return bicycleMapper.getBikeMoveDist(com);
	}

	@Override
	public double getBikeMoveDist_Last(Map<String, String> GPS_DATA) 
	{
		return bicycleMapper.getBikeMoveDist_Last(GPS_DATA);
	}

	@Override
	public double getCalcDistance(Map<String, String> GPS_DATA) 
	{
		return bicycleMapper.getCalcDistance(GPS_DATA);
	}

	@Override
	public Map<String, Object> getBikeMoveDist_COUNT(Map<String, String> GPS_DATA)
	{
		return bicycleMapper.getBikeMoveDist_COUNT(GPS_DATA);
	}

	@Override
	public void parkingInfoDelete(CommonVo com) 
	{
		bicycleMapper.parkingInfoDelete(com);
	}

	@Override
	public void insertRentGPSDATA(Map<String, String> GPS_DATA) 
	{
		bicycleMapper.insertRentGPSDATA(GPS_DATA);
	}

	@Override
	public void deleteRentGPSDATA(RentHistVo HistVo) 
	{
		bicycleMapper.deleteRentGPSDATA(HistVo);
	}

	@Override
	public void insertRentMove_Info(Map<String, String> GPS_DATA) 
	{
		bicycleMapper.insertRentMove_Info(GPS_DATA);
	}
	
	@Override
	public void updateBikeCnt(String voucherseq) 
	{
		bicycleMapper.updateBikeCnt(voucherseq);
	}
	
	@Override
	public void updateKickCnt(String voucherseq) 
	{
		bicycleMapper.updateKickCnt(voucherseq);
	}
	
	
	@Override
	public Map<String, String> getBikeRETURN_GPS(String RENT_SEQ)
	{
		return bicycleMapper.getBikeRETURN_GPS(RENT_SEQ);
	}
	
	@Override
	public void updateRENTGPS_CLS_CD(Map<String, String> GPS_DATA) 
	{
		bicycleMapper.updateRENTGPS_CLS_CD(GPS_DATA);
	}
}
