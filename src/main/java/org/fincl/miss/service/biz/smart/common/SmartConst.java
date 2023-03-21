package org.fincl.miss.service.biz.smart.common;

public class SmartConst {
	
	/**
	 * 공공자전거 기관코드
	 */
	public static final String BIKE_ORG_CODE = "81000441";
	public static final String BIZ_GUBUN = "FTP";
	public static final String IN_OUT_FLAG = "E";
	public static final String GUBUN = "R";

	public static final String userId = "bikeseoul.com";
	public static final String userPwd = "bikeseoul.com";
	
	/**
	 * 전문코드
	 * @author root
	 *
	 */
	public class CommandReq{
		/**
		 * 업무개시요청 (0600)
		 */
		public static final String START	= "0600";
		
		/**
		 * 결번확인요청
		 */
		public static final String CHK_MISSING	= "0620";
		
		/**
		 * 파일정보 수신요청
		 */
		public static final String FILE_INFO	= "0630";
		
		/**
		 * 결번데이터 송신
		 */
		public static final String RESEND = "0310";
		
		/**
		 * 데이터 송신
		 */
		public static final String SEND_DATA	= "0320";
	
	}
	
	public class CommandRes{
		/**
		 * 업무개시요청 응답
		 */
		public static final String START	= "0610";
		
		/**
		 * 파일정보 수신응답
		 */
		public static final String FILE_INFO	= "0640";
		
		
		/**
		 * 결번확인응답
		 */
		public static final String CHK_MISSING	= "0300";
	
	}

	public class ResCode{
		//정상
		public static final String OK	= "000";
		//기타 오류
		public static final String ERROR = "999";
		
		//시스템 장애
		public static final String SYSTEM_ERR =  "090";			
		
		//송신자명 오류
		public static final String INVALID_USER =  "310";		
		
		//송신자 암호 오류
		public static final String INVALID_PASS =  "320"; 	
		
		//기전송완료
		public static final String EXIST_FILE =  "630"; 
		
		//해당기관 미등록 업무
		public static final String INVALID_COMMAND =  "631"; 	
		
		//비정상 파일명
		public static final String INVALID_FILE_NAME =  "632";	
		
		//비정상 전문 바이트 수
		public static final String INVALID_FILE_SIZE =  "633";	
		
		//포맷오류
		public static final String INVALID_FORMAT = "800";		

	}
	
	public class Biz{
		//업무시작
		public static final String START	= "001";
		
		public static final String EXIST_NEXT	= "002";
		
		public static final String EXIST_NONE	= "003";
		
		public static final String END	= "004";
		
		
	}

	
}
