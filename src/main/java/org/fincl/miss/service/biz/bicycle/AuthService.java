package org.fincl.miss.service.biz.bicycle;

import java.util.HashMap;
import java.util.Map;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.CardAuthRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.CardAuthResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.PasswordAuthRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.PasswordAuthResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "인증")
@Service
public class AuthService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	BicycleRentService bikeService;
	
	@Autowired
	CommonService commonService;
	
	 @RPCService(serviceId = "Bicycle_04", serviceName = "카드 인증 Request", description = "카드 인증 Request")
	 public CardAuthResponseVo cardAuth(CardAuthRequestVo vo) {
		 
		 logger.debug("######################## CardAuthResponseVo_Bicycle_04 ");
		 logger.debug("CardAuthRequestVo vo ::::::::::: git {}" , vo);
		 
		 /*MessageHeader m = vo.getMessageHeader();
		 System.out.println(" MessageHeader m::" + m);*/
		 
		 CardAuthResponseVo responseVo = new CardAuthResponseVo();
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 
		 //기본 언어로 한글을 정의.
		 responseVo.setLang(Constants.CODE.get("LAG_001"));
		 
		
		// 자전거 상태 값 이상
		/* if(!vo.getBicycleState().equals(Constants.CODE.get("BIKE_STATE_02"))){
			 System.out.println("INVALID 자전거 ID 자전거 상태값 이상" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }*/
		 
		 
		/* // 해당하는 자전거 정보가 없는 경우
		 Map<String, Object> deviceInfo = commonService.checkBicycle(com);
		 if(deviceInfo == null){
			 System.out.println("INVALID 자전거 ID ");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }*/
		 String card = vo.getUserCardNum();
		 com.setCardTypeCd("CAD_001");
		 if(vo.getCardType().equals(Constants.CODE.get("CARD_TYPE_00"))){
			card = vo.getUserCardNum().substring(0,4)+"-"+vo.getUserCardNum().substring(4,8)+"-"+vo.getUserCardNum().substring(8,12)+"-"+vo.getUserCardNum().substring(12,16);
		 }else{
			com.setCardTypeCd("CAD_002");
		 }
		 String noCard = vo.getUserCardNum();
		 com.setCardNum(card);
		 com.setAdmincardNum(noCard);
		 
		 
		 // 카드번호 조회
		 // 자전거 예약건 조회
		 Map<String, Object> cardInfo = commonService.checkCardNum(com);
		 
		 if(cardInfo == null){
			 
			// 카드 정보가 없는 경우 예약정보로 카드 등록여부를 확인한다.
			// 예약정보를 확인하기 위해 자전거정보로 주차정보를 확인하여 거치대 정보를 조회한다.
			// 현재 자전거로 예약정보 확인 불가.. 거치대에 예약... 
			 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
			 if(parkingMap == null){
				 logger.error("##### 카드인증 : 등록되어 있지않은 카드 ##### ");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_FE"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }else{
				 com.setRockId(String.valueOf(parkingMap.get("RACK_ID")));
			 }
				 
				 
			 Map<String, Object> rentInfo = commonService.reservationCheck(com);
				 
			 if(rentInfo == null){
				 
				 logger.error("예약 확인 실패");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_EF"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo; 
			 
			 }else{
				
				/**
				 * 예약정보가 확인되면, 사용자 언어정보를 설정.. 
				 */
				responseVo.setLang(Constants.CODE.get(rentInfo.get("LANG_CLS_CD"))); // 사용자 언어
				 
				// 카드 인증
				 if(String.valueOf(rentInfo.get("RENT_CLS_CD")).equals("RCC_004")){
					 logger.error("유효한 이용권 없음");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }else{
					 // 카드 등록
					 if(String.valueOf(rentInfo.get("TERMINAL_CARD_REG_YN")).equals("Y")){
						 
						 // 사용자 관리번호 
						 com.setUserSeq(rentInfo.get("USR_SEQ")+"");
						 com.setStationId(String.valueOf(rentInfo.get("RENT_STATION_ID")));
						 //카드 등록 실행
						 /**
						  * 기존카드가 존재시에는 업데이트하는 것으로 프로세스 변경함에 따라,
						  * 등록된 카드여부 조회 후 없는 경우에 업데이트 하는 로직을 
						  * 기존 카드가 존재하는 경우 업데이트하고, 없는 경우 새로이 등록하는 것으로 변경함.
						  * --> 다시 원복 (중복체크) 본인인 경우에도 중복오류 발생.
						  *
						  */
						 if(commonService.chkExistCard(com) || commonService.chkExistAdminCard(com)){
							 logger.error("카드등록오류(이미 등록된 카드)");
							 responseVo.setErrorId(Constants.CODE.get("ERROR_D5"));
							 responseVo = setFaiiMsg(responseVo, vo);
							 
							 return responseVo;
							 
						 }else{
					
						 	 Map<String, Object> registUsr = commonService.registCard(com);
							 if(registUsr ==  null){
								 logger.error("카드 등록 실패");
								 responseVo.setErrorId(Constants.CODE.get("ERROR_DC"));
								 responseVo = setFaiiMsg(responseVo, vo);
								 
								 return responseVo;
							 }else{
								 /**
								  * 카드가 등록되면 등록된 카드 사용자의 언어정보 변경.
								  */
								 logger.debug("카드 등록 성공");
								 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_00"));   // 사용자
								 responseVo.setLang(Constants.CODE.get(registUsr.get("LANG_CLS_CD"))); // 사용자 언어
							 }
						 }
						 
						 
					 }else{
						 logger.error("등록되지 않은 카드 번호 입력");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_E6"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
				 } 
			 }
			 
		 }else{
			// 카드 인증 실행
			 if(cardInfo.get("USER").equals("USER")){
				 /**
				  * 카드 인증시 사용자 정보가 확인되면 해당 사용자의 장애발생시  카드사용자의
				  * 언어로 음성 안내...
				  * 언어 설정...
				  */
				 //운휴 체크....
				 //운휴 기간 체크 : 사용자 일때만 체크 2019.09.06
				 boolean chkDelayTime = commonService.chkDelayTime(com);
				 if(!chkDelayTime)
				 {
					 logger.error("운휴기간 내 대여불가");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_CF"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
					 
				 }
				 //
				 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_00"));   // 사용자
				 responseVo.setLang(Constants.CODE.get(cardInfo.get("LANG_CLS_CD"))); // 사용자 언어
				 
				// CASCADE 된 자전거 여부확인_20170116_JJH_START
				 if(commonService.isLastCascade(com)){
					 logger.debug("##### CASCADE 마지막 자전거 여부 문제없음~!! #####");
				 }else{
					 logger.debug("##### CASCADE 자전거 ID 중간자전거였음~!! #####");
					 
					 responseVo.setErrorId(Constants.CODE.get("ERROR_E8"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				// CASCADE 된 자전거 여부확인_20170116_JJH_END
				 
				// 예약정보를 확인하기 위해 자전거정보로 주차정보를 확인하여 거치대 정보를 조회한다.
				// 현재 자전거로 예약정보 확인 불가.. 거치대에 예약...
				 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
				 if(parkingMap == null){
					 logger.error("INVALID 자전거 ID & INVALID 거치대 ID ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }else{
					 com.setRockId(String.valueOf(parkingMap.get("RACK_ID")));
				 }
					 
				 
				 //고장신고 체크 추가  08.20
				 
				 
				 //
				 
				 com.setUserSeq(cardInfo.get("USR_SEQ")+"");
				 logger.debug("############## 카드 번호 ::{}", com.getCardNum());
				 // 카드 번호를 이용하여 사용자가 블랙리스트 등록 되었는지 확인
				 if(commonService.isBlackList(com)){
					 logger.error("블랙리스트 등록 이용자 ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_D9"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				 
				 // 카드 번호를 이용하여 초과요금 미납내역 있는지 확인
				 if(commonService.isUnpaidList(com)){
					 logger.error("초과요금 미납내역 존재 ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_DB"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;   
				 }
				 
				 
				 Map<String, Object> rentInfo = commonService.reservationCheck(com);
				 
				 if(rentInfo != null){
					 //예약 정보가 있는 경우 예약자와 카드번호와 동일한지 확인
					 if(!rentInfo.get("USR_SEQ").equals(cardInfo.get("USR_SEQ"))){
						 logger.error("카드번호 인증 오류/ 예약자와 카드 태깅 사용자 불일치");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_D2"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 logger.debug("######################## 이미 등록된 카드일 경우의 체크#######################");
					 
					 if(String.valueOf(rentInfo.get("TERMINAL_CARD_REG_YN")).equals("Y")){
						 /**
						  * 2015-12-23 추가 dki
						  * 
						  *수정-->>2016-01-07 dki */	 
						 //TERMINAL_CARD_REG_YN --> N 갱신 처리.
						 
						 /* 중복카드 등록 방지_20170518_JJH
						 commonService.updateRegistCard(com);
						 logger.debug("카드 등록 성공");
						 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_00"));   // 사용자
						 responseVo.setLang(Constants.CODE.get(rentInfo.get("LANG_CLS_CD"))); // 사용자 언어
						 
						 return responseVo;
						 */
						 
						 
						// 사용자 관리번호 
						 com.setUserSeq(rentInfo.get("USR_SEQ")+"");
						 com.setStationId(String.valueOf(rentInfo.get("RENT_STATION_ID")));
						 //카드 등록 실행
						 /**
						  * 기존카드가 존재시에는 업데이트하는 것으로 프로세스 변경함에 따라,
						  * 등록된 카드여부 조회 후 없는 경우에 업데이트 하는 로직을 
						  * 기존 카드가 존재하는 경우 업데이트하고, 없는 경우 새로이 등록하는 것으로 변경함.
						  * --> 다시 원복 (중복체크) 본인인 경우에도 중복오류 발생.
						  *
						  */
						 if(commonService.chkExistCard(com) || commonService.chkExistAdminCard(com)){
							 logger.error("##### 카드인증 : 카드등록오류(이미 등록된 카드) #####");
							 responseVo.setErrorId(Constants.CODE.get("ERROR_D5"));
							 responseVo = setFaiiMsg(responseVo, vo);
							 
							 return responseVo;
							 
						 }
					 }
				 }else{
					 //예약 정보가 없는 경우 해당 자전거를 예약 상태로 입력 
					 
					 BikeRentInfoVo voucher = bikeService.getVoucher(com);
					 if(voucher == null){
						 logger.error("유효한 이용권 없음");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 
					 Map<String, Object> useBike = bikeService.getUseBikeInfo(com);
					 if(useBike != null){
						 logger.error("미반납된 자전거 보유");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_DA"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 
					 bikeService.reservationInsert(com, voucher);
				 }
				 
				 
			 }else if(cardInfo.get("USER").equals("ADMIN")){
				 
				 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_01")); // 관리자
				 responseVo.setLang(Constants.CODE.get("LAG_001")); // 관리자 언어
			 }
		 }
		 /*
		 if(cardInfo == null && rentInfo == null){
			 logger.error("등록되지 않은 카드 번호 입력");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E6"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
			 
		 }else if(cardInfo == null && rentInfo != null){
			 // 카드 인증
			 if(String.valueOf(rentInfo.get("RENT_CLS_CD")).equals("RCC_004")){
				 logger.error("유효한 이용권 없음");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }else{
				 // 카드 등록
				 if(String.valueOf(rentInfo.get("TERMINAL_CARD_REG_YN")).equals("Y")){
					 
					 // 사용자 관리번호 
					 com.setUserSeq(rentInfo.get("USR_SEQ")+"");
					 
					 
					 //카드 등록 실행
					 if(commonService.chkExistCard(com)){
						 logger.error("카드등록 실패");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_DC"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
						 
					 }else{
						 Map<String, Object> registUsr = commonService.registCard(com);
						 if(registUsr ==  null){
							 logger.error("카드등록오류(이미 등록된 카드)");
							 responseVo.setErrorId(Constants.CODE.get("ERROR_D5"));
							 responseVo = setFaiiMsg(responseVo, vo);
							 
							 return responseVo;
						 }else{
							 logger.debug("카드 등록 성공");
							 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_00"));   // 사용자
							 responseVo.setLang(Constants.CODE.get(registUsr.get("LANG_CLS_CD"))); // 사용자 언어
						 }
					 }
					 
					 
				 }else{
					 logger.error("등록되지 않은 카드 번호 입력");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_E6"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
			 }
		 }else if(cardInfo != null ){
			 
			 // 카드 인증 실행
			 if(cardInfo.get("USER").equals("USER")){
				 
				 
				 
				 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_00"));   // 사용자
				 responseVo.setLang(Constants.CODE.get(cardInfo.get("LANG_CLS_CD"))); // 사용자 언어
				 
				 com.setUserSeq(cardInfo.get("USR_SEQ")+"");
				 logger.debug("############## 카드 번호 ::{}", com.getCardNum());
				 // 카드 번호를 이용하여 사용자가 블랙리스트 등록 되었는지 확인
				 if(commonService.isBlackList(com)){
					 logger.error("블랙리스트 등록 이용자 ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_D9"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				 
				 
				 // 카드 번호를 이용하여 초과요금 미납내역 있는지 확인
				 if(commonService.isUnpaidList(com)){
					 logger.error("초과요금 미납내역 존재 ");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_DB"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				 
				 
				 
				 if(rentInfo != null){
					 //예약 정보가 있는 경우 예약자와 카드번호와 동일한지 확인
					 if(!rentInfo.get("USR_SEQ").equals(cardInfo.get("USR_SEQ"))){
						 logger.error("카드번호 인증 오류/ 예약자와 카드 태깅 사용자 불일치");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_D2"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
				 }else{
					 //예약 정보가 없는 경우 해당 자전거를 예약 상태로 입력 
					 
					 BikeRentInfoVo voucher = bikeService.getVoucher(com);
					 if(voucher == null){
						 logger.error("유효한 이용권 없음");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_E5"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 
					 Map<String, Object> useBike = bikeService.getUseBikeInfo(com);
					 if(useBike != null){
						 logger.error("미반납된 자전거 보유");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_DA"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 
					 bikeService.reservationInsert(com, voucher);
				 }
				 
				 
				 
			 }else if(cardInfo.get("USER").equals("ADMIN")){
				 
				 responseVo.setAdminUser(Constants.CODE.get("ADMIN_USER_01")); // 관리자
				 responseVo.setLang(Constants.CODE.get("LAG_001")); // 관리자 언어
			 }
			 
		 }
		 */
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTHCARD);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 
		 return responseVo;
	        
	 }
	 
	 // 카드인증 실패 메세지
	 public CardAuthResponseVo setFaiiMsg(CardAuthResponseVo responseVo, CardAuthRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTHCARD);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 /*
	  * 
	  * 비밀번호 인증..
	  * 인터페이스 변경..사용자/관리자 카드번호 추가.
	  */
	 @RPCService(serviceId = "Bicycle_13", serviceName = "비밀번호 인증 Request", description = "비밀번호 인증 Request")
	 public PasswordAuthResponseVo passWordAuth(PasswordAuthRequestVo vo) {
		 
		 logger.debug("######################## PasswordAuth_Bicycle_13");
		 logger.debug("PasswordAuthRequestVo vo :::::::::::{}" , vo);
		 
		 PasswordAuthResponseVo responseVo = new PasswordAuthResponseVo();
		 
		 CommonVo com = new CommonVo();
		 com.setBicycleId(vo.getBicycleId());
		 com.setPassword(vo.getPassword());
		 
		
		 
		 // 자전거 상태 값 이상
		 if(!vo.getBicycleState().equals(Constants.CODE.get("BIKE_STATE_02"))){
			 logger.error("INVALID 자전거 ID 자전거 상태값 이상" );
			 responseVo.setErrorId(Constants.CODE.get("ERROR_E9"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 
		 // 해당하는 자전거 정보가 없는 경우
		 Map<String, Object> deviceInfo = commonService.checkBicycle(com);
		 if(deviceInfo == null){
			 logger.error("INVALID 자전거 ID ");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
		 
		 logger.debug("info : {}" , deviceInfo);
		 
		 
		 /**
		  * 비밀번호 인증 프로세스 케이스
		  * 1. 예약 후 비밀번호 인증
		  * 1) 카드 인증 후(예약) 비밀번호 입력.
		  *   : 카드번호가 존재하며, 비밀번호 체크
		  * 2) 앱 대여 후 (카드인증 없이) 비밀번호 입력
		  *   : 카드번호가 00000000
		  * 2. 관리자 이동 시 비밀번호 인증
		  *   : 카드번호가 존재하며, 관리자비밀번호 체크
		  */
		 
		 String card = vo.getCardNum();
		 com.setCardTypeCd("CAD_001");
		 if(vo.getCardType().equals(Constants.CODE.get("CARD_TYPE_00"))){
			card = vo.getCardNum().substring(0,4)+"-"+vo.getCardNum().substring(4,8)+"-"+vo.getCardNum().substring(8,12)+"-"+vo.getCardNum().substring(12,16);
		 }else{
			com.setCardTypeCd("CAD_002");
		 }
		 
		 com.setCardNum(card);
		 com.setAdmincardNum(vo.getCardNum());
		 
		 
		// 비밀번호 확인
		 String pass = String.valueOf(Integer.parseInt( vo.getPassword().substring(0,2) , 16)) + String.valueOf(Integer.parseInt( vo.getPassword().substring(2,4) , 16))+ String.valueOf(Integer.parseInt( vo.getPassword().substring(4,6) , 16)) +String.valueOf(Integer.parseInt( vo.getPassword().substring(6,8) , 16));
		 com.setPassword(pass);
		 com.setBikeId(String.valueOf(deviceInfo.get("BIKE_ID")));
		
		 Map<String, Object> cardInfo = null;
		 
		 //운휴 기간 체크 : 관리자 카드 이후 이동 시 (패스워드 체크시) 2019.09.06
		 boolean chkDelayTime = commonService.chkDelayTime(com);
		 if(!chkDelayTime)
		 {
			 cardInfo = commonService.checkCardNum(com);
			 
			 if(cardInfo.get("USER").equals("ADMIN"))
			 {
				 logger.error("운휴기간  !!!!: administrator_ok.....");
			 }
			 else
			 {
				 logger.error("운휴기간 내 대여불가");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_CF"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }
		 }
		 
		 boolean isUser = false;
		 //앱대여로 카드태킹이 없이 넘어오는 경우.
		 if(vo.getCardType().equals(Constants.CODE.get("CARD_TYPE_01")) 
				 || Integer.parseInt(vo.getCardLength(),16) == 0 
				 || card.equals("0000-0000-0000-0000")){
			 isUser = true;
			 logger.debug("############## card_info null");
		 }else{
			 cardInfo = commonService.checkCardNum(com);
			 // 카드정보가 존재하는 경우..
			 if(cardInfo != null){
				 if(cardInfo.get("USER").equals("USER")){
					 com.setUserSeq(cardInfo.get("USR_SEQ")+"");
					 logger.debug("############## 카드 번호 ::{}", com.getCardNum());
					 isUser = true;
					 logger.debug("############## card_info not null");
				 }else if(cardInfo.get("USER").equals("ADMIN")){
					 logger.debug("############## 관리자카드 번호 ::{}", com.getCardNum());
					 if(!commonService.checkAdminPwd(com)){
						 logger.error("관리자 비밀번호 오류 : 재배치 금지 ");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_E7"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_04"));
						 logger.debug("############## card_info admin");
						 return responseVo; 
					 }
				 }
			 }else{
				 logger.error("등록되지 않은 카드 번호 입력");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_E6"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }
		 }
		 

		 if(isUser){
			 
			 Map<String, Object> userInfo = commonService.passWordCheck(com);
			 if(userInfo == null){
				 logger.error("예약 확인 실패 ");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_EF"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 }
			 
			 logger.debug(String.valueOf(userInfo.get("RENT_ENC_PWD")));
			 logger.debug(String.valueOf(userInfo.get("IN_ENC_PWD")));
			 logger.debug(com.getPassword());
			 
			 /**
			  * 원래 userInfo.get("RENT_ENC_PWD")와 userInfo.get("IN_END_PWD") 를 비교하는 로직이었으나,
			  * 복호화값이 기대와 다른 값이 나와 DB 내에서 비교하는 로직으로 변경
			  */
		     
			 Map<String, Object> checkUsrPwdMap = commonService.checkUserPwd(com);
			 
			 if(! (Integer.parseInt(checkUsrPwdMap.get("IN_ENC_PWD").toString()) > 0) ){
				 logger.error("대여 비밀번호 오류 ");
				 responseVo.setErrorId(Constants.CODE.get("ERROR_EB"));
				 responseVo = setFaiiMsg(responseVo, vo);
				 
				 return responseVo;
			 } else {
				 logger.error("###중복대여 체크###");
				 
				 logger.debug("##### AuthService, com #### ==> " + com.toString());
				 logger.debug("##### checkUsrPwdMap, com #### ==> " + checkUsrPwdMap.get("IN_ENC_PWD") + ",  ==> " + checkUsrPwdMap.get("USR_SEQ"));
				 /**
				  * 2015-12-23 추가 dki
				  * 카드,앱 대여시 부정 중복대여를 체크.
				  * 개인대여시 대여 테이블에 데이터가 2건이상일 경우 부정대여. 
				  * 부정 대여일경우 대여테이블의 데이터 삭제 후 에러 메세지 리턴.
				  * */
				 com.setUserSeq(String.valueOf(checkUsrPwdMap.get("USR_SEQ")));
				 
				 if(commonService.chkRentDoubleBooking(com)) {
					 logger.error("중복대여");
					 //차후 코드 입력.
					 responseVo.setErrorId(Constants.CODE.get("ERROR_EF"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				 
			 }
		 }
		 
		 responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTHPASS);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
		 
	 }
	 
	 // 비밀번호 인증 실패 메세지
	 public PasswordAuthResponseVo setFaiiMsg(PasswordAuthResponseVo responseVo, PasswordAuthRequestVo vo ){
		 
		 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
		 responseVo.setSeqNum(vo.getSeqNum());
		 responseVo.setCommandId(Constants.CID_RES_AUTHPASS);
		 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_02"));
		 responseVo.setBicycleId(vo.getBicycleId());
		 
		 return responseVo;
	 }
	    
	 // 대여일련번호 인증 Request
     @RPCService(serviceId = "Bicycle_10", serviceName = "대여일련번호 인증 Request", description = "대여일련번호 인증 Request")
     public RentalResponseVo serialNumberRentalRequest(SerialNumberRentalRequestVo vo) {
        
    	 String tmpSerialNo = String.valueOf(vo.getSerialNumber().replaceAll("0", ""));
    	 
    	 vo.setSerialNumber(tmpSerialNo);
    	 
		 logger.debug("######################## 대여일련번호 인증 _Bicycle_10 ");		//수정 
		 logger.debug("SerialNumberRentalRequestVo vo :::::::::::{} " , vo);
		
		 RentalResponseVo responseVo = new RentalResponseVo();
		 CommonVo com = new CommonVo();
		 
		 Map<String, String> serialMap = new HashMap<String, String>();
		 serialMap = bikeService.getSerialNumberInfo(vo);
		
//		 if(serialMap != null){	// 해당 대여일련번호가 유효한 대여일련번호인지 유무
		 //2019.09.03. null pointer 수정 함.
		 
		 //운휴 기간 
		 boolean chkDelayTime = commonService.chkDelayTime(com);
		 if(!chkDelayTime)
		 {
			 logger.error("운휴기간 내 대여불가");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_CF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
			 
		 }
		 //
		 
		 if(serialMap == null || serialMap.get("USR_SEQ") == null || serialMap.get("USR_SEQ").toString().equals("") 
	             || serialMap.get("VOUCHER_SEQ") == null || serialMap.get("VOUCHER_SEQ").toString().equals("") )
		 {

			//2018.03.28  witcom  수정함...
			 logger.debug("##### 대여일련번호 조회 => 입력한 대여일련번호 정보 없음.NO_EXIST_AUTH_SERIAL_NUM = {}",vo.getSerialNumber());
			 
			 responseVo.setErrorId(Constants.CODE.get("ERROR_CC"));
    		 responseVo = setFaiiMsg(responseVo, vo);
    		
    		 return responseVo;
    		 
			
		 }else{	// 입력한 대여일련번호 정보가 없음.
			 //2018.03.28  witcom  수정함...
			 if(!serialMap.get("USR_SEQ").equals("N") && !serialMap.get("VOUCHER_SEQ").equals("N")){	// 이용권 미사용 또는 이용기간 충분.
				 logger.debug("##### 대여일련번호 인증 => 이용권 미사용 또는 이용기간 충분. #####");
				 
				 com.setBicycleId(String.valueOf(vo.getBicycleId()));
				 com.setUserSeq(String.valueOf(serialMap.get("USR_SEQ")));
				 
				 if(!commonService.isLastCascade(com)){	// CASCADE 된 자전거라면 중간 자전거인지 여부확인
					 logger.debug("##### 대여일련번호 인증 => CASCADE 자전거 ID 중간자전거였음~!! #####");
					 
					 responseVo.setErrorId(Constants.CODE.get("ERROR_E8"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;	
				 }
				 
				// 예약정보를 확인하기 위해 자전거정보로 주차정보를 확인하여 거치대 정보를 조회한다.
				// 현재 자전거로 예약정보 확인 불가.. 거치대에 예약...
				 Map<String, Object> parkingMap = commonService.checkParkingInfo(com);
				 
				 if(parkingMap == null){
					 logger.error("##### 대여일련번호 인증 => INVALID 자전거 ID & INVALID 거치대 ID #####");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_F7"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }else{
					 com.setRockId(String.valueOf(parkingMap.get("RACK_ID")));
				 }
				 
				 
				 if(commonService.isBlackListByUserSeq(com)){	// UserSeq로 BlackList 여부확인
					 logger.error("##### 대여일련번호 인증 => 블랙리스트 등록 이용자~!! #####");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_D9"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;
				 }
				 
				 if(commonService.isUnpaidListByUserSeq(com)){	// 카드 번호를 이용하여 초과요금 미납내역 있는지 확인
					 logger.error("##### 대여일련번호 인증 => 초과요금 미납내역 존재~!! #####");
					 responseVo.setErrorId(Constants.CODE.get("ERROR_DB"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;   
				 }
				 
				 Map<String, Object> rentInfo = commonService.reservationCheck(com);
				 
				 if(rentInfo != null){	//예약 정보가 있는 경우 예약자와 카드번호와 동일한지 확인
					 logger.error("##### 대여일련번호 인증 => 대여하려고 하는 거치대의 자전거가 예약상태임~!! #####");
					 
					 responseVo.setErrorId(Constants.CODE.get("ERROR_D2"));
					 responseVo = setFaiiMsg(responseVo, vo);
					 
					 return responseVo;

				 }else{
					 //예약 정보가 없는 경우 해당 자전거를 예약 상태로 입력 
					 BikeRentInfoVo voucher = bikeService.getUseVoucherInfo(com);
					 Map<String, Object> useBike = bikeService.getUseBikeInfo(com);
					 
					 if(useBike != null){
						 logger.error("미반납된 자전거 보유");
						 responseVo.setErrorId(Constants.CODE.get("ERROR_DA"));
						 responseVo = setFaiiMsg(responseVo, vo);
						 
						 return responseVo;
					 }
					 
					 bikeService.reservationInsert(com, voucher);
				 }
				 
			 }else if(serialMap.get("USR_SEQ").equals("N") || serialMap.get("VOUCHER_SEQ").equals("N")){	// 이용기간 만료
				 logger.debug("##### 대여일련번호 조회 => 이용권 기간만료. #####");
				 
				 responseVo.setErrorId(Constants.CODE.get("ERROR_CC"));
	    		 responseVo = setFaiiMsg(responseVo, vo);
	    		
	    		 return responseVo;
			 }
		 }
        
         responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
         responseVo.setSeqNum(vo.getSeqNum());
         responseVo.setCommandId(Constants.CID_RES_AUTHSERIALNUMBER);
         responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_07"));
         responseVo.setBicycleId(vo.getBicycleId());
        
         return responseVo;
    }
    

	 // 대여일련번호 인증 실패 메세지
     public RentalResponseVo setFaiiMsg(RentalResponseVo responseVo, SerialNumberRentalRequestVo vo){
    	
    	 responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	 responseVo.setSeqNum(vo.getSeqNum());
    	 responseVo.setCommandId(Constants.CID_RES_AUTHSERIALNUMBER);
    	 responseVo.setBicycleState(Constants.CODE.get("BIKE_STATE_FF"));
    	 responseVo.setBicycleId(vo.getBicycleId());
     	
    	 return responseVo;
    }
     
}
