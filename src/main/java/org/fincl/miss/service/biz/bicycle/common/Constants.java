package org.fincl.miss.service.biz.bicycle.common;

import java.util.HashMap;

public class Constants {
	
	
	///////////////// Response Frame Control Feild ///////////////

	/**
	 * COMMAND TYPE 성공 RESPONSE CONTROL FIELD (0x4300)
	 */
	public static final String SUCC_CMD_CONTROL_FIELD	= "4300";
	/**
	 * COMMAND TYPE 실패 RESPONSE CONTROL FIELD (0x4301)
	 */
	public static final String FAIL_CMD_CONTROL_FIELD	= "4301";
	
	/**
	 * DATA TYPE 성공 RESPONSE CONTROL FIELD (0x0300)
	 */
	public static final String SUCC_DATA_CONTROL_FIELD	= "0300";
	/**
	 * DATA TYPE 실패 RESPONSE CONTROL FIELD (0x0301)
	 */
	public static final String FAIL_DATA_CONTROL_FIELD	= "0301";
	
	/**
	 * ACK TYPE 성공 RESPONSE CONTROL FIELD (0x8300)
	 */
	public static final String SUCC_ACK_CONTROL_FIELD	= "8300";
	/**
	 * ACK TYPE 실패 RESPONSE CONTROL FIELD (0x8301)
	 */
	public static final String FAIL_ACK_CONTROL_FIELD	= "8301";
	
	
	
	//////////////////// 커맨드 아이디////////////////////
	/**
	 * 관리자에 의한 자전거 설치 Request (0x01)
	 */
	public static final String CID_REQ_RESTOREBYADMIN	= "01";
	
	/**
	 * 자전거 설치 Response (0x81)
	 */
	public static final String CID_RES_RESTOREBYADMIN = "81";
	
	/**
	 * 자전거 대여 Request (0x02)
	 */
	public static final String CID_REQ_RENTCOMPLETE = "02";
	
	/**
	 * 자전거 대여 Response (0x82)
	 */
	public static final String CID_RES_RENTCOMPLETE = "82";
	
	/**
	 * 자전거 반납 Request (0x03)
	 */
	public static final String CID_REQ_RETURNBIKE = "03";
	
	/**
	 * 자전거 반납 Response (0x83)
	 */
	public static final String CID_RES_RETURNBIKE = "83";
	
	/**
	 * 카드 인증  Request (0x04)
	 */
	public static final String CID_REQ_AUTHCARD = "04";
	
	/**
	 * 카드 인증 Response (0x84)
	 */
	public static final String CID_RES_AUTHCARD = "84";
	
	
	/**
	 * 관리자 이동 Request(0x05)
	 */
	public static final String CID_REQ_MOVEADMIN = "05";
	
	/**
	 * 관리자 이동 Response (0x85)
	 */
	public static final String CID_RES_MOVEADMIN = "85";
	
	/**
	 * 점검 고장 신고 Request (0x06)
	 */
	public static final String CID_REQ_JAMDECLARATION = "06";
	
	/**
	 * 점검 고장신고 Response (0x86) 
	 */
	public static final String CID_RES_JAMDECLARATION = "86";
	
	/**
	 * 도난 보고 Request (0x07)
	 */
	public static final String CID_REQ_LOSTARNING = "07";
	
	/**
	 * 도난 보고 Response (0x87)
	 */
	public static final String CID_RES_LOSTWARNING = "87";
	
	/**
	 * 대여 취소 Request (0x08)
	 */
	public static final String CID_REQ_RENTCANCEL = "08";
	
	/**
	 * 대여 취소 Response (0x88)
	 */
	public static final String CID_RES_RENTCANCEL = "88";
	
	/**
	 * 관리자 이동 취소 Request(0x09)
	 */
	public static final String CID_REQ_MOVEADMINCANCLE = "09";
	
	/**
	 * 관리자 이동 취소 Response (0x89)
	 */
	public static final String CID_RES_MOVEADMINCANCLE = "89";
	
	/**
	 * 주기적인 상태 보고 Request (0x11)
	 */
	public static final String CID_REQ_REPORTOFBIKE = "11";
	
	/**
	 * 주기적인 상태 보고 Response (0x91)
	 */
	public static final String CID_RES_REPORTOFBIKE = "91";
	
	/**
	 * 주기적인 자동 반납 Request (0x12)
	 */
	public static final String CID_REQ_AUTORETURNFBIKE = "12";
	
	/**
	 * 주기적인 자동 반납 Response (0x92)
	 */
	public static final String CID_RES_AUTORETURNBIKE = "92";
	
	/**
	 * 비밀번호 인증 Request (0x13)
	 */
	public static final String CID_REQ_AUTHPASS = "13";
	
	/**
	 * 비밀번호 인증 Response (0x91)
	 */
	public static final String CID_RES_AUTHPASS = "93";
	
	/**
	 * 대여 대기 Request (0X14)
	 */
	public static final String CID_REQ_RENTWAIT = "14";
	
	/**
	 * 대여 대기 Response (0x94)
	 */
	public static final String CID_RES_RENTWAIT = "94";
	
	/**
	 * GPS 데이터 전송  Request (0x15)
	 */
	public static final String CID_REQ_SENDGPSDATA = "15";
	
	/**
	 * GPS 데이터 전송 Response (0x95)
	 */
	public static final String CID_RES_SENDGPSDATA = "95";
	
	/**
	 * 다운로드 서버 접속 Request (0x55)
	 */
	public static final String CID_REQ_CONNDOWNSERVER = "55";
	
	/**
	 * 다운로드 서버 접속 ACK (0x56)
	 */
	public static final String CID_ACK_CONNDOWNSERVER= "56";
	
	/**
	 * 다운로드 서버 접속 NACK (0x57)
	 */
	public static final String CID_NACK_CONNDOWNSERVER= "57";
	
	/**
	 * F/W 파일 데이터 다운로드 Request (0x58)
	 */
	public static final String CID_REQ_DOWNFIRMWARE = "58";
	
	/**
	 * F/W 파일 데이터 다운로드 ACK (0x59)
	 */
	public static final String CID_ACK_DOWNFIRMWARE = "59";
	
	/**
	 * F/W 파일 데이터 다운로드 NACK (0x60)
	 */
	public static final String CID_NACK_DOWNFIRMWARE = "60";
	
	/**
	 * 이미지 파일 데이터 다운로드 Request (0x61)
	 */
	public static final String CID_REQ_DOWNIMAGE = "61";
	
	/**
	 * 이미지 파일 데이터 다운로드 ACK (0x62)
	 */
	public static final String CID_ACK_DOWNIMAGE = "62";
	
	/**
	 * 이미지 파일 데이터 다운로드 NACK (0x63)
	 */
	public static final String CID_NACK_DOWNIMAGE = "63";
	
	/**
	 * 음성 파일 데이터 다운로드 Request (0x64)
	 */
	public static final String CID_REQ_DOWNSOUND = "64";
	
	/**
	 * 음성 파일 데이터 다운로드 ACK (0x65)
	 */
	public static final String CID_ACK_DOWNSOUND = "65";
	
	/**
	 * 음성 파일 데이터 다운로드 NACK (0x66)
	 */
	public static final String CID_NACK_DOWNSOUND = "66";
	
	/**
	 * 이미지 파일 정보 다운로드 Request (0x67)
	 */
	public static final String CID_REQ_FILEINFOIMAGE = "67";
	
	/**
	 * 이미지 파일 정보 다운로드 ACK (0x68)
	 */
	public static final String CID_ACK_FILEINFOIMAGE = "68";
	
	/**
	 * 이미지 파일 정보 다운로드 NACK (0x69)
	 */
	public static final String CID_NACK_FILEINFOIMAGE = "69";
	
	/**
	 * 음성 파일 정보 다운로드 Request (0x70)
	 */
	public static final String CID_REQ_FILEINFOSOUND = "70";
	
	/**
	 * 음성 파일 정보 다운로드 ACK (0x71)
	 */
	public static final String CID_ACK_FILEINFOSOUND = "71";
	
	/**
	 * 음성 파일 정보 다운로드 NACK (0x72)
	 */
	public static final String CID_NACK_FILEINFOSOUND = "72";
	
	
	/**
	 * 다운로드 완료 Request (0x73)
	 */
	public static final String CID_REQ_DOWNLOAD = "73";
	
	/**
	 * 다운로드 완료 ACK (0x74)
	 */
	public static final String CID_ACK_DOWNLOAD = "74";
	
	/**
	 * 다운로드 완료 NACK (0x75)
	 */
	public static final String CID_NACK_DOWNLOAD = "75";
	
	
	/**
	 * 업데이트 완료 Request (0x76)
	 */
	public static final String CID_REQ_UPDATE = "76";
	
	/**
	 * 업데이트 완료 ACK (0x77)
	 */
	public static final String CID_ACK_UPDATE= "77";
	
	/**
	 * 업데이트 완료 NACK (0x78)
	 */
	public static final String CID_NACK_UPDATE = "78";
	
	/**
	 * 대여일련번호 인증 Request (0X10)
	 */
	public static final String CID_REQ_AUTHSERIALNUMBER = "10";
	
	/**
	 * 대여일련번호 인증 Response (0X90)
	 */
	public static final String CID_RES_AUTHSERIALNUMBER = "90";
	
	/**
	 * 정차 자전거 자동확인 Request (0X16)
	 */
	public static final String CID_REQ_BICYCLESTOPCHK = "16";
	
	/**
	 * 정차 자전거 자동확인 Response (0X96)
	 */
	public static final String CID_RES_BICYCLESTOPCHK = "96";
	
	
	public static final HashMap<String,String> CODE = new HashMap<String,String>();
	
	static{
		
		// 자전거 상태
		CODE.put("BIKE_STATE_00", "00");
		CODE.put("BIKE_STATE_01", "01");
		CODE.put("BIKE_STATE_02", "02");
		CODE.put("BIKE_STATE_03", "03");
		CODE.put("BIKE_STATE_04", "04");
		CODE.put("BIKE_STATE_07", "07");
		CODE.put("BIKE_STATE_FF", "FF");
		
		
		
		// 에러코드
		CODE.put("ERROR_FF", "FF");
		CODE.put("ERROR_FE", "FE");
		CODE.put("ERROR_FD", "FD");
		CODE.put("ERROR_FC", "FC");
		CODE.put("ERROR_FB", "FB");
		CODE.put("ERROR_FA", "FA");
		CODE.put("ERROR_F9", "F9");
		CODE.put("ERROR_F8", "F8");
		CODE.put("ERROR_F7", "F7");
		CODE.put("ERROR_F6", "F6");
		CODE.put("ERROR_F5", "F5");
		CODE.put("ERROR_F4", "F4");
		CODE.put("ERROR_F3", "F3");
		CODE.put("ERROR_F2", "F2");
		CODE.put("ERROR_F1", "F1");
		CODE.put("ERROR_F0", "F0");
		CODE.put("ERROR_EF", "EF");
		CODE.put("ERROR_EE", "EE");
		CODE.put("ERROR_ED", "ED");
		CODE.put("ERROR_EC", "EC");
		CODE.put("ERROR_EB", "EB");
		CODE.put("ERROR_EA", "EA");
		CODE.put("ERROR_E9", "E9");
		CODE.put("ERROR_E8", "E8");
		CODE.put("ERROR_E7", "E7");
		CODE.put("ERROR_E6", "E6");
		CODE.put("ERROR_E5", "E5");
		CODE.put("ERROR_E4", "E4");
		CODE.put("ERROR_E3", "E3");
		CODE.put("ERROR_E2", "E2");
		CODE.put("ERROR_E1", "E1");
		CODE.put("ERROR_E0", "E0");
		CODE.put("ERROR_DF", "DF");
		CODE.put("ERROR_DE", "DE");
		CODE.put("ERROR_DD", "DD");
		CODE.put("ERROR_DC", "DC");
		CODE.put("ERROR_DB", "DB");
		CODE.put("ERROR_DA", "DA");
		CODE.put("ERROR_D9", "D9");
		CODE.put("ERROR_D8", "D8");
		CODE.put("ERROR_D7", "D7");
		CODE.put("ERROR_D6", "D6");
		CODE.put("ERROR_D5", "D5");
		CODE.put("ERROR_D4", "D4");
		CODE.put("ERROR_D3", "D3");
		CODE.put("ERROR_D2", "D2");
		CODE.put("ERROR_D1", "D1");
		CODE.put("ERROR_00", "00");
		CODE.put("ERROR_CC", "CC");
		
		
		
		//Lock(거치대) 상태
		CODE.put("LOCK_01", "01");
		CODE.put("LOCK_02", "02");
		CODE.put("LOCK_03", "03");
		
		
		//배터리 상태
		CODE.put("BATTERY_00", "00");
		CODE.put("BATTERY_01", "01");
		CODE.put("BATTERY_02", "02");
		CODE.put("BATTERY_03", "03");
		
		// 결제종류
		CODE.put("PAY_KIND_10", "10");
		CODE.put("PAY_KIND_20", "20");
		CODE.put("PAY_KIND_30", "30");
		CODE.put("PAY_KIND_40", "40");
		
		
		//반납/거치 형태
		CODE.put("RETURN_LOCK_00", "00");
		CODE.put("RETURN_LOCK_01", "01");
		
		
		//카드 등록
		CODE.put("INPUT_CARD_00", "00");
		CODE.put("INPUT_CARD_01", "01");
		
		
		// 사용자 언어
		CODE.put("LAG_001", "00");
		CODE.put("LAG_002", "01");
		CODE.put("LAG_003", "03");
		CODE.put("LAG_004", "02");
		
		//사용자 관리자 구분
		CODE.put("ADMIN_USER_00", "00");  // 사용자
		CODE.put("ADMIN_USER_01", "01");  // 관리자
		
		// 업데인트 유형
		CODE.put("UPDATE_00", "00");  
		CODE.put("UPDATE_01", "01");  
		CODE.put("UPDATE_02", "02");  
		CODE.put("UPDATE_03", "03");  
		
		
		// 무선 업데이트 
		CODE.put("WIFI_UPDATE_00", "00");  
		CODE.put("WIFI_UPDATE_01", "01");
		
		
		// 대여 대기 
		CODE.put("RENT_WAIT_00", "00");  
		CODE.put("RENT_WAIT_01", "01");  
		
		//고장유형
		CODE.put("JAM_00", "00");
		CODE.put("JAM_01", "01");
		CODE.put("JAM_02", "02");
		CODE.put("JAM_03", "03");
		CODE.put("JAM_04", "04");
		CODE.put("JAM_05", "05");
		CODE.put("JAM_06", "06");
		CODE.put("JAM_07", "07");
		CODE.put("JAM_08", "08");
		CODE.put("JAM_09", "09");
				
		//카드타입 (Default : 자리, 01 : UID)
		CODE.put("CARD_TYPE_00", "00");
		CODE.put("CARD_TYPE_01", "01");
	}
	
}
