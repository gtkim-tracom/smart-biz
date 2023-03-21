package org.fincl.miss.service.biz.bicycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.service.biz.bicycle.common.CommonUtil;
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.service.BicycleRentService;
import org.fincl.miss.service.biz.bicycle.service.CommonService;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateService;
import org.fincl.miss.service.biz.bicycle.vo.CompleteDownAckVo;
import org.fincl.miss.service.biz.bicycle.vo.CompleteDownRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.CompleteUpdateAckVo;
import org.fincl.miss.service.biz.bicycle.vo.CompleteUpdateRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.ConnectDownServerRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.ConnectDownServerResponseVo;
import org.fincl.miss.service.biz.bicycle.vo.FirmwareDownAckVo;
import org.fincl.miss.service.biz.bicycle.vo.FirmwareDownRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.ImageFileInfoDownAckVo;
import org.fincl.miss.service.biz.bicycle.vo.ImageFileInfoDownRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.SoundFileInfoDownAckVo;
import org.fincl.miss.service.biz.bicycle.vo.SoundFileInfoDownRequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RPCServiceGroup(serviceGroupName = "무선 업데이트")
@Service
public class UpdateService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonService commonService;
	
	@Autowired	BicycleRentService bikeService;
	
	@Autowired
	FileUpdateService fileService;
	
	
	@RPCService(serviceId = "Bicycle_55", serviceName = "다운로드 서버 접속 Request", description = "다운로드 서버 접속 Request")
    public ConnectDownServerResponseVo connectServer(ConnectDownServerRequestVo vo) {
    	
    	logger.debug("ConnectDownServerRequestVo vo : {}" , vo);
        
    	ConnectDownServerResponseVo responseVo = new ConnectDownServerResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        
        Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);

		String	 nBikeSerial;
		if(ourBikeMap != null)
		{
			 //add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
		 	nBikeSerial = bikeNo.substring(2,bikeNo.length());
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 	
		 	logger.debug("QR_4350 ##### => bike {} ,state {} , company {} ",vo.getBicycleId(),vo.getBicycleState(),com.getCompany_cd());
		 	
		 	
		 	QRLogVo QRLog = new QRLogVo();
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 	QRLog.setFirm_fw(vo.getBle_firmwareVs());
		 	QRLog.setModem_fw(vo.getModem_firmwareVs());
		 
			QRLog.setMessage(vo.getReqMessage());
			QRLog.setQr_frame("업데이트 시작");
		 	
		 	bikeService.insertQRLog(QRLog);	
		}
		else
		{
			logger.error("INVALID 자전거 ID ");
			com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			com.setBikeBrokenCd("ELB_006");
			responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			responseVo = setFaiiMsg(responseVo, vo);
			 
			return responseVo;
		}
        
        // 버전 체크
        Map<String, Object> serverVersion = fileService.getVersion(com);
        //double requsetbleFw  = Double.parseDouble(vo.getBle_firmwareVs().substring(0,2) + "." + vo.getBle_firmwareVs().substring(2, 4));
		double requsetModemFw = Double.parseDouble(vo.getModem_firmwareVs().substring(0,2) + "." + vo.getModem_firmwareVs().substring(2, 4));
		
		 
       // double serverbleFw = Double.parseDouble(serverVersion.get("FIRMWARE_BLE_VER").toString());
        double serverModemFw = Double.parseDouble(serverVersion.get("FIRMWARE_MODEM_VER").toString());
        

		//boolean fwbleUseYn = serverVersion.get("FIRMWARE_BLE_USE_YN").equals("Y");
		boolean fwmodemUseYn = serverVersion.get("FIRMWARE_MODEM_USE_YN").equals("Y");
		
		String fileSeq = "";
		
		logger.debug("requsetVersion MW{} serverVersion MW{}", requsetModemFw,serverModemFw);
		// logger.debug("requsetVersion FW{}, MW{} serverVersion FW{},MW{}", requsetbleFw,requsetModemFw,serverbleFw,serverModemFw);
		 /*
        if((requsetbleFw < serverbleFw) && fwbleUseYn)
        {
        	logger.debug("### YES : FIRMWARE UPDATE ###  BLE FIRMWARE UPATE START BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
		
        	
        	fileSeq = serverVersion.get("FIRMWARE_BLE_SEQ").toString();
        	responseVo.setUpdate(Constants.CODE.get("UPDATE_01")); // BLE FIRMWARE UPDATE
        	responseVo.setVersion(versionToHex(String.valueOf(serverbleFw)));
        }
        else */if((requsetModemFw < serverModemFw) && fwmodemUseYn)
        {
        	logger.debug("### YES : FIRMWARE UPDATE ###  MODEL FIRMWARE UPATE START BIKE NO : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", BIKE ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
			
        	fileSeq = serverVersion.get("FIRMWARE_MODEM_SEQ").toString();
        	responseVo.setUpdate(Constants.CODE.get("UPDATE_02")); // MODEL FIRMWARE UPDATE
        	responseVo.setVersion(versionToHex(String.valueOf(serverModemFw)));
        }
        else
        {
        	//2019.12.18  에러로 가도록 수정...
        	logger.debug("### NO : FIRMWARE UPDATE ### UPDATE ITEM NOT FOUND");
        //	responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // 업데이트 진행 안함
        //	logger.error("update 파일 찾기 오류  ");
        	responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
        	responseVo = setFaiiMsg(responseVo, vo);
        	
        	return responseVo;
        	
        }
	       
       
        
  //      System.out.println(responseVo);
   
        if( !responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_00")))
        {
        	
        	// 펌웨어 업데이트 결과 요청 insert
        	com.setFirmwareSeq(fileSeq);
        	fileService.firmwareUpdateRequest(com);

        	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
        	
        	if(fileList.size() == 0 ){
        		logger.error("update 파일 찾기 오류  ");
            	responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
            	responseVo = setFaiiMsg(responseVo, vo);
            	
            	return responseVo;
        	}
        	else
        	{
        		
        		// 업데이트 파일이 한개인 경우
        		if(fileList.size() == 1)
        		{
        			String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
        			logger.debug("##### 업데이트 파일이 한개인 경우 ##### => 다운로드 서버 접속 파일이름 : " + fileName);
        			File file = new File(fileName);
        			
        			if(file.isFile())
        			{
        				// 4096 MB 이상 파일 
        				if(((file.length()/1024)/1024) > (4096)){
        					logger.error( " :::::::::::  update 파일 error : {}" , fileName);
            				responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
            				responseVo = setFaiiMsg(responseVo, vo);
            				
            				return responseVo;
        				}
        				
        				if(responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_01"))
        						 || responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_02")))
        				{
        					double d = (double)file.length()/512;
        					Double x = Math.ceil(Double.valueOf(d));
        					responseVo.setPacketSize(getToString(String.valueOf(x.intValue() ) , 4));
        					
                		}
        				else
        				{
                			responseVo.setPacketSize("0001");
                		}
        				responseVo.setTotal(getToString(String.valueOf(file.length()), 8) );
        				
        			}else{
        				logger.error( " ############# update 파일 error : {} " , fileName);
        				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
        				responseVo = setFaiiMsg(responseVo, vo);
        				
        				return responseVo;
        			}
        			
        			//2019.12.19 추가 ble 01 , mode 02
        			//if(fwbleUseYn){
        				//responseVo.setFileCnt(Constants.CODE.get("UPDATE_01"));
        			//}
        			//else
        			//{
        				responseVo.setFileCnt(Constants.CODE.get("UPDATE_02"));
        			//}
        			
        		}
        		else
        		{
        			// 업데이트 파일이 한개 이상인 경우 
        			
        			double totalSize = 0;
        			for (int i = 0; i < fileList.size(); i++) {
        				String fileName = fileList.get(i).get("FILE_SAVE_PATH")+File.separator + fileList.get(i).get("FILE_NAME");
        				logger.debug("##### 업데이트 파일이 한개 이상인 경우 ##### => 다운로드 서버 접속 파일이름 : " + fileName);
            			File file = new File(fileName);
            			
            			if(file.isFile()){
            				// 업데이트할 전체 파일 사이즈 
            				totalSize += file.length();
            				
            			}else{
            				logger.error( " ############# update 파일 error : {}" , fileName);
            				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
            				responseVo = setFaiiMsg(responseVo, vo);
            				
            				return responseVo;
            			}
						
					}
        			responseVo.setPacketSize("0001");
        			
        			// 전체용량을 16진수로 변환하여 4Byte로 변환.
        			
        			responseVo.setTotal(getToString(String.valueOf(((Double)Math.ceil(totalSize)).intValue()), 8));
        		
        			//	responseVo.setFileCnt(fileList.size()<10?"0"+fileList.size(): ""+fileList.size());
        			
        			// 변경할 파일수 (다운받을 파일 수) : 16진수로 변환하여 1byte로 저장.
        			responseVo.setFileCnt(getToString(String.valueOf(fileList.size()),2));
        			
        		}
        		
        		
        		
        		
        	}
        	
        } 
        
        responseVo.setFrameControl(Constants.SUCC_ACK_CONTROL_FIELD);
        responseVo.setSeqNum(vo.getSeqNum());
        responseVo.setCommandId(Constants.CID_ACK_CONNDOWNSERVER);
        responseVo.setBicycleState(vo.getBicycleState());
        responseVo.setBicycleId(vo.getBicycleId());
        
        
        return responseVo;
    }
    
    
    private String getToString(String str , int length){
    	
    	String strx = str;
    	try{
    		int num = Integer.parseInt(str);
    		strx = Integer.toHexString(num);
    	}catch(Exception e){}
    	
    	
    	int temp = 0;
    	if(strx.length() < length){
    		temp = length - strx.length();
    		for (int i = 0; i <  temp; i++) {
    			strx = "0"+strx;
    		}
    	}
    	return strx;
    	
    }
    
    private String getBlankString(String strx , int length){
    	
    	int temp = 0;
    	if(strx.length() < length){
    		temp = length - strx.length();
    		for (int i = 0; i <  temp; i++) {
    			strx = "0"+strx;
    		}
    	}
    	return strx;
    	
    }
    
    private String versionToHex(String ver){
    	String tmp1 = ver.split("\\.")[0];
    	String tmp2 = ver.split("\\.")[1];
    	
    	tmp1 = tmp1.length()==1?tmp1+"0":tmp1;
    	tmp2 = tmp2.length()==1?tmp2+"0":tmp2;
    	
    	return tmp1+tmp2;
    }
    
    
    
    // 다운로드 서버 접속 실패 메세지
    public ConnectDownServerResponseVo setFaiiMsg(ConnectDownServerResponseVo responseVo, ConnectDownServerRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_ACK_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_CONNDOWNSERVER);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
    
    @RPCService(serviceId = "Bicycle_67", serviceName = "이미지 파일정보 다운로드 Request", description = "이미지 파일정보 다운로드 Request")
    public ImageFileInfoDownAckVo  downloadImageData(ImageFileInfoDownRequestVo vo) {
    	
    	
    	logger.debug("ImageFileInfoDownRequestVo vo : {}" , vo);
    	
    	String fileSeq = "";
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
    	
    	
    	ImageFileInfoDownAckVo responseVo = new ImageFileInfoDownAckVo();
    	
    	
    	Map<String, Object> serverVersion = fileService.getVersion(com);
		 boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
    	
    	
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();
    	
    	if(imgUseYn){
    		fileSeq = serverVersion.get("IMAGE_SEQ").toString();
    		fileList = fileService.getFileInfo(fileSeq);
    	}else{
    		logger.debug("##### 이미지 파일정보 다운로드 조건이 아님. ##### => ImageSeq : " + String.valueOf(serverVersion.get("IMAGE_SEQ")) + ", 자전거 ID : " + vo.getBicycleId() + ", 이미지 버전 : " + vo.getImageVersion());
    	}
    	
    	if(fileList.size() == 0 || !imgUseYn){
    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiMsg(responseVo, vo);
			
			return responseVo;
    	}else{
    		//변경할 파일수 :16진수로 넘어오기 때문에 16진수를 10진수로 변환함.
    		int changefileCnt = Integer.parseInt(vo.getChangeFileCnt() ,16);
    		String fileNum = "";
    		double totalSize = 0;
			for (int i = 0; i < fileList.size(); i++) {
				
				if(i == (fileList.size() - changefileCnt)){
					String fileName = fileList.get(i).get("FILE_SAVE_PATH")+File.separator + fileList.get(i).get("FILE_NAME");
					logger.debug("##### 이미지 파일 다운로드 ##### => 다운로드 서버 접속 파일이름 : " + fileName);
					fileNum = String.valueOf(fileList.get(i).get("FIRMWARE_FILE_NO"));
					File file = new File(fileName);
					
					if(file.isFile()){
						// 업데이트할 전체 파일 사이즈 
						totalSize += file.length();
						break;
						
					}else{
						logger.debug( " ############# update 파일 error : {} " , fileName);
						responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
						responseVo = setFaiiMsg(responseVo, vo);
						
						return responseVo;
					}
				}
				
			}
			
			String vs = versionToHex(String.valueOf(serverVersion.get("IMAGE_VER")));
			
			responseVo.setFileNum(getBlankString(fileNum,2));
			responseVo.setImageVersion(vs);
			responseVo.setFileSize(getToString(String.valueOf(((Double)Math.ceil(totalSize)).intValue()), 8));
			double d = (double)totalSize/512;
			Double x = Math.ceil(Double.valueOf(d));
			responseVo.setPacketSize(getToString(String.valueOf(x.intValue()) , 4));
    	}
    	
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_FILEINFOIMAGE);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    // 이미지 파일정보 다운로드  실패 메세지
    public ImageFileInfoDownAckVo setFaiiMsg(ImageFileInfoDownAckVo responseVo, ImageFileInfoDownRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_FILEINFOIMAGE);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
    @RPCService(serviceId = "Bicycle_58", serviceName = "fw 파일 데이터 다운로드 Request", description = "fw 파일 데이터 다운로드 Request")
    public FirmwareDownAckVo downloadData(FirmwareDownRequestVo vo) {
    	
    	logger.debug("QR_FirmwareDownRequestVo vo : {}" , vo);
    	logger.debug("QR CMD:0x58 downloadData start  ");
    	
    	FirmwareDownAckVo responseVo = new FirmwareDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(ourBikeMap != null)
		 {
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 }
		 else
		 {
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
    	
    	// 버전 체크
    	Map<String, Object> deviceVersion = fileService.getVersion(com);
    	//파일번호 00이면 ble 01이면 modem
    	boolean fwUseYn = false;
    	//if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_01")))
    	//{
    		//fwUseYn = deviceVersion.get("FIRMWARE_BLE_USE_YN").equals("Y");
    	//}
    	//else 
    	if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_02")))
    	{
    		fwUseYn = deviceVersion.get("FIRMWARE_MODEM_USE_YN").equals("Y");
    	}
    	else
    	{
    		logger.debug( " ############# DOWNLOAD ## UPDATE DATA ERROR filenum {}",vo.getFileNum());
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiMsg(responseVo, vo);
			
			return responseVo;
    	}
		
    	String fileSeq = "";
    	
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();
    	
    	if(fwUseYn)
    	{
    		//if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_01")))
    		//{
    			//fileSeq = deviceVersion.get("FIRMWARE_BLE_SEQ").toString();
    		//}
    		//else 
    		if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_02")))
    		{
    			fileSeq = deviceVersion.get("FIRMWARE_MODEM_SEQ").toString();
    		}
    		else
    		{
    			logger.debug( " ############# DOWNLOAD ## UPDATE DATA ERROR2 filenum {}",vo.getFileNum());
    			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
    			responseVo = setFaiiMsg(responseVo, vo);
    			
    			return responseVo;
    		}
    		fileList = fileService.getFileInfo(fileSeq);
    		
    	}
    	else
    	{
    		if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_01")))
    		{
    			logger.debug("##### DOWNLOAD ## FIRMWARE UPDATE TARGET IS NOT ##### => fileSeq : " + String.valueOf(deviceVersion.get("FIRMWARE_BLE_SEQ")) + ", 자전거 ID : " + vo.getBicycleId());
    		}
    		else if(vo.getFileNum().equals(Constants.CODE.get("UPDATE_02")))
    		{
    			logger.debug("##### DOWNLOAD ## FIRMWARE UPDATE TARGET IS NOT ##### => fileSeq : " + String.valueOf(deviceVersion.get("FIRMWARE_MODEM_SEQ")) + ", 자전거 ID : " + vo.getBicycleId());
    		}
    	}
    	
    	if(fileList.size() == 0 || !fwUseYn)
    	{
    		logger.debug( " ############# DOWNLOAD ## UPDATE FILE HAVEN'T ERROR : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiMsg(responseVo, vo);
			
			return responseVo;
    	}

    		
    	System.out.println(fileList);
    	
    	int packet = Integer.parseInt(vo.getPacketNum(),16);
    	
    	
    	String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
    	logger.debug("##### UPDATE ## FIRMWARE DOWNLOAD ##### => SERVER FILE NAME : " + fileName);
    	File file = new File(fileName);
    	
    	InputStream is = null;
    	try 
    	{
    		
    		is = new FileInputStream(file);
    		long length = file.length();
    		
    		byte[] bytes = new byte[(int) length];
    		byte[] byteFile = new byte[512];
    		
    		int strIndex = 0;
    		int ednIndex = bytes.length;
    		
    	//	int offset = 512*(packet-1);
    		int offset = 512*(packet);
    		int end =  offset +512;
    		int numRead = 0;
    		int c = 512;
    		if((end - ednIndex) > 0){
    			c = 512-(end - ednIndex);
    		}
    		
    		logger.debug("length : {}, packet: {} , offset:{}, end : {}, size : {}"  , length , packet, offset, end ,c);
    		
    		
    		while (strIndex < ednIndex && (numRead=is.read(bytes, strIndex, ednIndex-strIndex)) >= 0) {
    			strIndex += numRead;
    		}
    		if (strIndex < ednIndex) {
    			throw new IOException("Could not completely read file "+file.getName());
    		}
    		
    		System.arraycopy( bytes, offset, byteFile, 0, c );
    		
    	//	logger.debug(DatatypeConverter.printHexBinary(byteFile));
    		// 다운로드 파일 
    		responseVo.setDownloadData(DatatypeConverter.printHexBinary(byteFile));
    		
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    				
    	if(file.isFile()){
    		
    		// 4096 MB 이상 파일 
    		if(((file.length()/1024)/1024) > (4096)){
    			logger.error( " :::::::::::  DOWNLOAD ## UPDATE FILE ERROR :{}" , fileName);
    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
    			responseVo = setFaiiMsg(responseVo, vo);
    			
    			return responseVo;
    		}
    		
    		
    	}else{
    		System.out.println( " ############# DOWNLOAD ## UPDATE FILE ERROR " + fileName);
    		responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
    		responseVo = setFaiiMsg(responseVo, vo);
    		
    		return responseVo;
    	}
    				
    				
    			
    	responseVo.setFileNum("00");
    	responseVo.setPacketNum(vo.getPacketNum());
    	
    	responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_DOWNFIRMWARE);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    
    
    // 다운로드 서버 접속 실패 메세지
    public FirmwareDownAckVo setFaiiMsg(FirmwareDownAckVo responseVo, FirmwareDownRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_DATA_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_CONNDOWNSERVER);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
    
    
    // f.w  다운로드 완료 
    @RPCService(serviceId = "Bicycle_73", serviceName = "fw 파일 데이터 다운로드 완료 Request", description = "fw 파일 데이터 다운로드 완료 Request")
    public CompleteDownAckVo downloadComplete(CompleteDownRequestVo vo) {
    	
    	logger.debug("QR_CompleteDownRequestVo vo :{}" , vo);
    	logger.debug("QR CMD:0x73 downloadComplete success  ");
    	
    	CompleteDownAckVo responseVo = new CompleteDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(ourBikeMap != null)
		 {
			 //add 자전거 번호 가져오기 2018.09.01
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 }
		 else
		 {
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
    	
    	responseVo.setAddDown("00");
    	
    	if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_01")))
    	{
    		responseVo.setAddDown("00");
        }
    	else if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_02")))
        {
    		responseVo.setAddDown("00");
    		/*
    		
        	Map<String, Object> deviceVersion = fileService.getVersion(com);
        	String fileSeq = deviceVersion.get("FIRMWARE_MODEM_SEQ").toString();
        	
        	boolean ModemUseYn = deviceVersion.get("FIRMWARE_MODEM_USE_YN").equals("Y");
   		 	
   		 	if(!ModemUseYn){
	    		logger.debug( " ############# COMPLETE ## FILE IS HAVENT : {}" , fileSeq );
				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
				responseVo = setFaiiMsg(responseVo, vo);
				
				return responseVo;
	    	}
   		 
        	com.setFirmwareSeq(fileSeq);
    		com.setFirmwareFileNo(String.valueOf(Integer.parseInt(vo.getFileNum())));
    		Map<String, Object> checkVal = fileService.getHasNext(com);
    		if(Integer.parseInt(String.valueOf(checkVal.get("HAS_NEXT")))>0){
    			responseVo.setAddDown("01");
    		}
    		*/
    	}
    	else
    	{
    		
    	}
     	
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_DOWNLOAD);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    
    
    // f/w 다운로드 완료 
    public CompleteDownAckVo setFaiiMsg(CompleteDownAckVo responseVo, CompleteDownRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_DOWNLOAD);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
    
    
    
    // 업데이트 완료  
    @RPCService(serviceId = "Bicycle_76", serviceName = "fw 파일 데이터 업데이트 완료 Request", description = "fw 파일 데이터 업데이트 완료 Request")
    public CompleteUpdateAckVo updateComplete(CompleteUpdateRequestVo vo) {
    	
    	logger.debug("QR_CompleteUpdateRequestVo vo : {}" , vo);
    	logger.debug("QR CMD:0x76 updateComplete FINISHED !!!!!!!!  ");
    	
    	CompleteUpdateAckVo responseVo = new CompleteUpdateAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	com.setVersion(vo.getFirmwareVersion());
    	
    	com.setFirmwareClsCd("FWD_001");
    	/*
    	//BLE
    	if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_00"))){
    		com.setFirmwareClsCd("FWD_001");
    	}else if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_01"))){
    		com.setFirmwareClsCd("FWD_002");
    	}
    	else
    	{
    		com.setFirmwareClsCd("FWD_002");
    		
    	}
    	*/

    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(ourBikeMap != null)
		{
		 	//add 자전거 번호 가져오기 2018.09.01
			String  bikeNo = ourBikeMap.get("BIKE_NO");
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 	
		 	logger.debug("QR_4300_UPDATE_FINISH ##### => bike {} ,state {} , company {} ",vo.getBicycleId(),vo.getBicycleState(),com.getCompany_cd());
		 	
		 	QRLogVo QRLog = new QRLogVo();
		 	
		 	QRLog.setBicycleId(vo.getBicycleId());
		 	QRLog.setBicycleNo(bikeNo);
		 	QRLog.setBicycleState(vo.getBicycleState());
		 
			QRLog.setMessage(vo.getReqMessage());
			QRLog.setQr_frame("업데이트 완료");
		 	
		 	bikeService.insertQRLog(QRLog);	
		 }
		 else
		 {
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
    	   	
    	// 단말 버전 업데이트 무선 업데이트 내역 업데이트
    	fileService.firmwareUpdateComplete(com);
 
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_UPDATE);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    
    
    // 업데이트 완료 실패
    public CompleteUpdateAckVo setFaiiMsg(CompleteUpdateAckVo responseVo, CompleteUpdateRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_UPDATE);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
 // 이미지 파일정보 다운로드 
    @RPCService(serviceId = "Bicycle_61", serviceName = "이미지 파일 데이타 다운로드 Request", description = "이미지 파일 데이타 다운로드 Request")
    public FirmwareDownAckVo  downloadImageDataFile(FirmwareDownRequestVo vo) {
    	
    	logger.debug("FirmwareDownRequestVo vo : {}" , vo);
    	
    	FirmwareDownAckVo responseVo = new FirmwareDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		/*
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
		*/
		
		if(ourBikeMap != null)
		 {
			 //add 자전거 번호 가져오기 2018.09.01
		 	String  ENTRPS_CD = ourBikeMap.get("ENTRPS_CD");
		 	String BIKE_SE_CD = ourBikeMap.get("BIKE_SE_CD");
		 	com.setCompany_cd("CPN_" + BIKE_SE_CD.substring(4,BIKE_SE_CD.length()));
		 }
		 else
		 {
			 logger.error("INVALID 자전거 ID ");
			 com.setBikeStusCd("BKS_001");	//자전거 상태가 장애로 변경 
			 com.setBikeBrokenCd("ELB_006");
			 responseVo.setErrorId(Constants.CODE.get("ERROR_FF"));
			 responseVo = setFaiiMsg(responseVo, vo);
			 
			 return responseVo;
		 }
    	
    	// 버전 체크
    	Map<String, Object> deviceVersion = fileService.getVersion(com);
    	
    	String fileSeq = deviceVersion.get("IMAGE_SEQ").toString();
    	
    	boolean imgUseYn = deviceVersion.get("IMAGE_USE_YN").equals("Y");
  			
  		if(!imgUseYn){
	    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
				responseVo = setFaiiMsg(responseVo, vo);
				
				return responseVo;
	    }
    	com.setFirmwareSeq(fileSeq);	
    	com.setFirmwareFileNo(String.valueOf(Integer.parseInt(vo.getFileNum())));
    	HashMap<String, Object> fileInfo = fileService.getFileWithFileNo(com);
    		
    	System.out.println(fileInfo);
    	
    	int packet = Integer.parseInt(vo.getPacketNum(),16);
    	
    	String fileName=null;
    	
    	logger.debug("fileNo>>>>{}" , Integer.parseInt(vo.getFileNum()));
//    	for(int i =0 ;i<fileList.size(); i++){
//    		HashMap<String,Object> fileInfo = fileList.get(i);
//    		BigDecimal fileno = (BigDecimal)fileInfo.get("FIRMWARE_FILE_NO");
//    		System.out.println("FIRMWARE_FILE_NO >>>>["+fileno.intValue()+"]");
//    		if(Integer.parseInt(vo.getFileNum()) == fileno.intValue()){
		
    	fileName = fileInfo.get("FILE_SAVE_PATH")+File.separator + fileInfo.get("FILE_NAME");
    	
    	logger.debug("##### 이미지 파일 데이타 다운로드 ##### => " + fileName);
		System.out.println(fileName);
    	File file = new File(fileName);
    	
    	InputStream is = null;
    	try {
    		
    		is = new FileInputStream(file);
    		long length = file.length();
    		
    		byte[] bytes = new byte[(int) length];
    		byte[] byteFile = new byte[512];
    		
    		int strIndex = 0;
    		int ednIndex = bytes.length;
    		
    	//	int offset = 512*(packet-1);
    		int offset = 512*(packet);
    		int end =  offset +512;
    		int numRead = 0;
    		int c = 512;
    		if((end - ednIndex) > 0){
    			c = 512-(end - ednIndex);
    		}
    		
    		logger.debug("length : {}, packet: {} , offset:{}, end : {}, size : {}"  , length , packet, offset, end ,c);
    		
    		
    		while (strIndex < ednIndex && (numRead=is.read(bytes, strIndex, ednIndex-strIndex)) >= 0) {
    			strIndex += numRead;
    		}
    		if (strIndex < ednIndex) {
    			throw new IOException("Could not completely read file "+file.getName());
    		}
    		
    		System.arraycopy( bytes, offset, byteFile, 0, c );
    		
    		System.out.println(DatatypeConverter.printHexBinary(byteFile));
    		// 다운로드 파일 
    		responseVo.setDownloadData(DatatypeConverter.printHexBinary(byteFile));
    		
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    				
    	if(file.isFile()){
    		
    		// 4096 MB 이상 파일 
    		if(((file.length()/1024)/1024) > (4096)){
    			logger.error( " :::::::::::  update 파일 error : {}" , fileName);
    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
    			responseVo = setFaiiImgMsg(responseVo, vo);
    			
    			return responseVo;
    		}
    		
    		
    	}else{
    		logger.error( " ############# update 파일 error : {}" , fileName);
    		responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
    		responseVo = setFaiiImgMsg(responseVo, vo);
    		
    		return responseVo;
    	}
    				
    				
    			
    	responseVo.setFileNum(vo.getFileNum());
    	responseVo.setPacketNum(vo.getPacketNum());
    	
    	responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_DOWNIMAGE);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());

    	//    		}
  //  	}
    	
    	return responseVo;
    }

 // 다운로드 서버 접속 실패 메세지
    public FirmwareDownAckVo setFaiiImgMsg(FirmwareDownAckVo responseVo, FirmwareDownRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_DATA_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_DOWNIMAGE);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
   
    /* 음성 파일정보 다운로드 ORIGIN_20170530_JJH_START
    @RPCService(serviceId = "Bicycle_70", serviceName = "음성 파일정보 다운로드 Request", description = "음성 파일정보 다운로드 Request")
    public SoundFileInfoDownAckVo  downloadSoundData(SoundFileInfoDownRequestVo vo) {
    	
    	
    	logger.debug("SoundFileInfoDownRequestVo vo : {}" , vo);
    	
    	String fileSeq = "";
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	
    	SoundFileInfoDownAckVo responseVo = new SoundFileInfoDownAckVo();
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
    	
    	Map<String, Object> serverVersion = fileService.getVersion(com);
    	boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
    	boolean sdCanDown = serverVersion.get("VOICE_CAN_DOWN").equals("Y");
    	
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();
    	
    	if(sdUseYn && sdCanDown){
    		fileSeq = serverVersion.get("VOICE_SEQ").toString();
    		fileList = fileService.getFileInfo(fileSeq);
    	}else{
    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiSoundMsg(responseVo, vo);
				
			return responseVo;
    	}
    	
    	if(fileList.size() == 0){
    		logger.error( " ############# update 파일 없음 :{} " , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D1"));
			responseVo = setFaiiSoundMsg(responseVo, vo);
			
			return responseVo;
    	}else{
    		//변경할 파일수 :16진수로 넘어오기 때문에 16진수를 10진수로 변환함.
    		int changefileCnt = Integer.parseInt(vo.getChangeFileCnt(),16);
    		String fileNum = "";
    		double totalSize = 0;
    		int idx = fileList.size() - changefileCnt;
    		
    		HashMap<String, Object> fileInfo = fileList.get(idx);
    		
//			for (int i = 0; i < fileList.size(); i++) {
				
//				if(i == (fileList.size() - changefileCnt)){
					String fileName = fileInfo.get("FILE_SAVE_PATH")+File.separator + fileInfo.get("FILE_NAME");
					fileNum = fileInfo.get("FIRMWARE_FILE_NO")+"";
					File file = new File(fileName);
					
					if(file.isFile()){
						// 업데이트할 전체 파일 사이즈 
						totalSize += file.length();
			//			break;
						
					}else{
						logger.error( " ############# update 파일 error :{}" , fileName);
						responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
						responseVo = setFaiiSoundMsg(responseVo, vo);
						
						return responseVo;
					}
//				}
				
//			}
			
			String vs =  versionToHex(String.valueOf(serverVersion.get("VOICE_VER")));
			
			responseVo.setFileNum(getBlankString(fileNum,2));
			responseVo.setSoundVersion(vs);
			responseVo.setFileSize(getToString(String.valueOf(((Double)Math.ceil(totalSize)).intValue()), 8));
			double d = (double)totalSize/512;
			Double x = Math.ceil(Double.valueOf(d));
			// 패킷사이즈 전달시. 0부터 count하기 때문에 패킷이 1이면 0으로 반환해야 함.
			responseVo.setPacketSize(getToString(String.valueOf(x.intValue()) , 4));
				
    	}
    	
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_FILEINFOSOUND);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    음성 파일정보 다운로드_ORIGIN_20170530_JJH_END */
    
    
    //음성 파일정보 다운로드 
    @RPCService(serviceId = "Bicycle_70", serviceName = "음성 파일정보 다운로드 Request", description = "음성 파일정보 다운로드 Request")
    public SoundFileInfoDownAckVo  downloadSoundData(SoundFileInfoDownRequestVo vo) {
    	
    	
    	logger.debug("SoundFileInfoDownRequestVo vo : {}" , vo);
    	
    	String fileSeq = "";
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	
    	SoundFileInfoDownAckVo responseVo = new SoundFileInfoDownAckVo();
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		/*
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
		*/
    	
    	Map<String, Object> serverVersion = fileService.getVersion(com);
    	boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
    	
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();
    	
    	if(sdUseYn){
    		fileSeq = serverVersion.get("VOICE_SEQ").toString();
    		fileList = fileService.getFileInfo(fileSeq);
    	}else{
    		logger.debug("##### 음성 파일정보 다운로드 조건이 아님. ##### => fileSeq : " + String.valueOf(serverVersion.get("VOICE_SEQ")) + ", 자전거 ID : " + vo.getBicycleId() + ", 음성 버전 : " + vo.getSoundVersion());
    	}
    	
    	if(fileList.size() == 0 || !sdUseYn){
    		logger.error( " ############# update 파일 없음 :{} " , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D1"));
			responseVo = setFaiiSoundMsg(responseVo, vo);
			
			return responseVo;
    	}else{
    		//변경할 파일수 :16진수로 넘어오기 때문에 16진수를 10진수로 변환함.
    		int changefileCnt = Integer.parseInt(vo.getChangeFileCnt(),16);
    		String fileNum = "";
    		double totalSize = 0;
    		int idx = fileList.size() - changefileCnt;
    		
    		HashMap<String, Object> fileInfo = fileList.get(idx);
    		
//			for (int i = 0; i < fileList.size(); i++) {
				
//				if(i == (fileList.size() - changefileCnt)){
					String fileName = fileInfo.get("FILE_SAVE_PATH")+File.separator + fileInfo.get("FILE_NAME");
					logger.debug("##### 음성 파일정보 다운로드 ##### => " + fileName);
					fileNum = fileInfo.get("FIRMWARE_FILE_NO")+"";
					File file = new File(fileName);
					
					if(file.isFile()){
						// 업데이트할 전체 파일 사이즈 
						totalSize += file.length();
			//			break;
						
					}else{
						logger.error( " ############# update 파일 error :{}" , fileName);
						responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
						responseVo = setFaiiSoundMsg(responseVo, vo);
						
						return responseVo;
					}
//				}
				
//			}
			
			String vs =  versionToHex(String.valueOf(serverVersion.get("VOICE_VER")));
			
			responseVo.setFileNum(getBlankString(fileNum,2));
			responseVo.setSoundVersion(vs);
			responseVo.setFileSize(getToString(String.valueOf(((Double)Math.ceil(totalSize)).intValue()), 8));
			double d = (double)totalSize/512;
			Double x = Math.ceil(Double.valueOf(d));
			// 패킷사이즈 전달시. 0부터 count하기 때문에 패킷이 1이면 0으로 반환해야 함.
			responseVo.setPacketSize(getToString(String.valueOf(x.intValue()) , 4));
				
    	}
    	
    	responseVo.setFrameControl(Constants.SUCC_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_ACK_FILEINFOSOUND);
    	responseVo.setBicycleState(vo.getBicycleState());
    	responseVo.setBicycleId(vo.getBicycleId());
    	
    	
    	return responseVo;
    }
    
    
    // 음성 파일정보 다운로드  실패 메세지
    public SoundFileInfoDownAckVo setFaiiSoundMsg(SoundFileInfoDownAckVo responseVo, SoundFileInfoDownRequestVo vo ){
    	
    	responseVo.setFrameControl(Constants.FAIL_CMD_CONTROL_FIELD);
    	responseVo.setSeqNum(vo.getSeqNum());
    	responseVo.setCommandId(Constants.CID_NACK_FILEINFOSOUND);
    	responseVo.setBicycleId(vo.getBicycleId());
    	responseVo.setBicycleState(vo.getBicycleState());
    	
    	return responseVo;
    }
    
    
    // 음성 파일정보 다운로드 
   /* 음성 파일 데이터 다운로드 주석_20170530_JJH_START
   @RPCService(serviceId = "Bicycle_64", serviceName = "음성 파일 데이터 다운로드 Request", description = "음성 파일 데이터 다운로드 Request")
   public FirmwareDownAckVo  downloadSoundDataFile(FirmwareDownRequestVo vo) {
   	
   	logger.debug("FirmwareDownRequestVo vo : {}" , vo);
   	
   	FirmwareDownAckVo responseVo = new FirmwareDownAckVo();
   	
   	CommonVo com = new CommonVo();
   	com.setBicycleId(vo.getBicycleId());
   	
   	// 버전 체크
   	Map<String, Object> deviceVersion = fileService.getVersion(com);
   	
   	String fileSeq = deviceVersion.get("VOICE_SEQ").toString();
   	
	boolean sdUseYn = deviceVersion.get("VOICE_USE_YN").equals("Y");
  	
	if(!sdUseYn){
   		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiImgMsg(responseVo, vo);
			
			return responseVo;
   	}
		 	
   	com.setFirmwareSeq(fileSeq);
   	com.setFirmwareFileNo(String.valueOf(Integer.parseInt(vo.getFileNum())));
   	
  // 	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
   	HashMap<String, Object> fileInfo = fileService.getFileWithFileNo(com);	
   	logger.debug("fileInfo : {}", fileInfo);
   	
   	int packet = Integer.parseInt(vo.getPacketNum(),16);
   	
   	String fileName=null;
   	
 //  	System.out.println("fileNo>>>>"+Integer.parseInt(vo.getFileNum()));
///   	for(int i =0 ;i<fileList.size(); i++){
 //  		HashMap<String,Object> fileInfo = fileList.get(i);
   		logger.debug("FIRMWARE_FILE_NO >>>>{}",fileInfo.get("FIRMWARE_FILE_NO"));
 //  		BigDecimal fileno = (BigDecimal)fileInfo.get("FIRMWARE_FILE_NO");
 //  		if(Integer.parseInt(vo.getFileNum()) == fileno.intValue()){
   			fileName = fileInfo.get("FILE_SAVE_PATH")+File.separator + fileInfo.get("FILE_NAME");
   			logger.debug("fileName : {}", fileName);
   	    	File file = new File(fileName);
   	    	
   	    	InputStream is = null;
   	    	try {
   	    		
   	    		is = new FileInputStream(file);
   	    		long length = file.length();
   	    		
   	    		byte[] bytes = new byte[(int) length];
   	    		byte[] byteFile = new byte[512];
   	    		
   	    		int strIndex = 0;
   	    		int ednIndex = bytes.length;
   	    		
   	    	//	int offset = 512*(packet-1);
   	    		int offset = 512*(packet);
   	    		int end =  offset +512;
   	    		int numRead = 0;
   	    		int c = 512;
   	    		if((end - ednIndex) > 0){
   	    			c = 512-(end - ednIndex);
   	    		}
   	    		
   	    		logger.debug("length : {}, packet: {} , offset:{}, end : {}, size : {}"  , length , packet, offset, end ,c);
   	    		
   	    		while (strIndex < ednIndex && (numRead=is.read(bytes, strIndex, ednIndex-strIndex)) >= 0) {
   	    			strIndex += numRead;
   	    		}
   	    		if (strIndex < ednIndex) {
   	    			throw new IOException("Could not completely read file "+file.getName());
   	    		}
   	    		
   	    		System.arraycopy( bytes, offset, byteFile, 0, c );
   	    		
   //	    		logger.debug(DatatypeConverter.printHexBinary(byteFile));
   	    		// 다운로드 파일 
   	    		responseVo.setDownloadData(DatatypeConverter.printHexBinary(byteFile));
   	    		
   	    		
   	    	} catch (Exception e) {
   	    		e.printStackTrace();
   	    	} finally {
   	    		try {
   	    			is.close();
   	    		} catch (IOException e) {
   	    			e.printStackTrace();
   	    		}
   	    	}
   	    	
   	    				
   	    	if(file.isFile()){
   	    		
   	    		// 4096 MB 이상 파일 
   	    		if(((file.length()/1024)/1024) > (4096)){
   	    			logger.error( " :::::::::::  update 파일 error : {}" , fileName);
   	    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
   	    			responseVo = setFaiiImgMsg(responseVo, vo);
   	    			
   	    			return responseVo;
   	    		}
   	    		
   	    		
   	    	}else{
   	    		logger.debug( " ############# update 파일 error :{}" , fileName);
   	    		responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
   	    		responseVo = setFaiiImgMsg(responseVo, vo);
   	    		
   	    		return responseVo;
   	    	}
   	    				
   	    	responseVo.setFileNum(vo.getFileNum());
   	    	responseVo.setPacketNum(vo.getPacketNum());
   	    	
   	    	responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
   	    	responseVo.setSeqNum(vo.getSeqNum());
   	    	responseVo.setCommandId(Constants.CID_ACK_DOWNSOUND);
   	    	responseVo.setBicycleState(vo.getBicycleState());
   	    	responseVo.setBicycleId(vo.getBicycleId());
//   		}
 //  	}
   	
   	
   	
   	return responseVo;
   }
	음성 파일 데이터 다운로드 주석_20170530_JJH_END*/

    @RPCService(serviceId = "Bicycle_64", serviceName = "음성 파일 데이터 다운로드 Request", description = "음성 파일 데이터 다운로드 Request")
    public FirmwareDownAckVo  downloadSoundDataFile(FirmwareDownRequestVo vo) {
    	
    	logger.debug("FirmwareDownRequestVo vo : {}" , vo);
    	
    	FirmwareDownAckVo responseVo = new FirmwareDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
		
		String fileSeq = "";
    	
    	// 버전 체크
    	Map<String, Object> deviceVersion = fileService.getVersion(com);
    	boolean sdUseYn = deviceVersion.get("VOICE_USE_YN").equals("Y");
    	
    	if(sdUseYn){
    		fileSeq = deviceVersion.get("VOICE_SEQ").toString();
    	}else{
    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
 			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
 			responseVo = setFaiiImgMsg(responseVo, vo);
 			
 			return responseVo;
    	}
 		 	
    	com.setFirmwareSeq(fileSeq);
    	com.setFirmwareFileNo(String.valueOf(Integer.parseInt(vo.getFileNum())));
    	
   // 	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
    	HashMap<String, Object> fileInfo = fileService.getFileWithFileNo(com);	
    	logger.debug("fileInfo : {}", fileInfo);
    	
    	int packet = Integer.parseInt(vo.getPacketNum(),16);
    	
    	String fileName=null;
    	
  //  	System.out.println("fileNo>>>>"+Integer.parseInt(vo.getFileNum()));
 ///   	for(int i =0 ;i<fileList.size(); i++){
  //  		HashMap<String,Object> fileInfo = fileList.get(i);
    		logger.debug("FIRMWARE_FILE_NO >>>>{}",fileInfo.get("FIRMWARE_FILE_NO"));
  //  		BigDecimal fileno = (BigDecimal)fileInfo.get("FIRMWARE_FILE_NO");
  //  		if(Integer.parseInt(vo.getFileNum()) == fileno.intValue()){
    			fileName = fileInfo.get("FILE_SAVE_PATH")+File.separator + fileInfo.get("FILE_NAME");
    			logger.debug("##### 음성 파일 데이터 다운로드 ##### => " + fileName);
    	    	File file = new File(fileName);
    	    	
    	    	InputStream is = null;
    	    	try {
    	    		
    	    		is = new FileInputStream(file);
    	    		long length = file.length();
    	    		
    	    		byte[] bytes = new byte[(int) length];
    	    		byte[] byteFile = new byte[512];
    	    		
    	    		int strIndex = 0;
    	    		int ednIndex = bytes.length;
    	    		
    	    	//	int offset = 512*(packet-1);
    	    		int offset = 512*(packet);
    	    		int end =  offset +512;
    	    		int numRead = 0;
    	    		int c = 512;
    	    		if((end - ednIndex) > 0){
    	    			c = 512-(end - ednIndex);
    	    		}
    	    		
    	    		logger.debug("length : {}, packet: {} , offset:{}, end : {}, size : {}"  , length , packet, offset, end ,c);
    	    		
    	    		while (strIndex < ednIndex && (numRead=is.read(bytes, strIndex, ednIndex-strIndex)) >= 0) {
    	    			strIndex += numRead;
    	    		}
    	    		if (strIndex < ednIndex) {
    	    			throw new IOException("Could not completely read file "+file.getName());
    	    		}
    	    		
    	    		System.arraycopy( bytes, offset, byteFile, 0, c );
    	    		
    //	    		logger.debug(DatatypeConverter.printHexBinary(byteFile));
    	    		// 다운로드 파일 
    	    		responseVo.setDownloadData(DatatypeConverter.printHexBinary(byteFile));
    	    		
    	    		
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	} finally {
    	    		try {
    	    			is.close();
    	    		} catch (IOException e) {
    	    			e.printStackTrace();
    	    		}
    	    	}
    	    	
    	    				
    	    	if(file.isFile()){
    	    		
    	    		// 4096 MB 이상 파일 
    	    		if(((file.length()/1024)/1024) > (4096)){
    	    			logger.error( " :::::::::::  update 파일 error : {}" , fileName);
    	    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
    	    			responseVo = setFaiiImgMsg(responseVo, vo);
    	    			
    	    			return responseVo;
    	    		}
    	    		
    	    		
    	    	}else{
    	    		logger.debug( " ############# update 파일 error :{}" , fileName);
    	    		responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
    	    		responseVo = setFaiiImgMsg(responseVo, vo);
    	    		
    	    		return responseVo;
    	    	}
    	    				
    	    	responseVo.setFileNum(vo.getFileNum());
    	    	responseVo.setPacketNum(vo.getPacketNum());
    	    	
    	    	responseVo.setFrameControl(Constants.SUCC_DATA_CONTROL_FIELD);
    	    	responseVo.setSeqNum(vo.getSeqNum());
    	    	responseVo.setCommandId(Constants.CID_ACK_DOWNSOUND);
    	    	responseVo.setBicycleState(vo.getBicycleState());
    	    	responseVo.setBicycleId(vo.getBicycleId());
  //    		}
  //  	}
    	
    	
    	
    	return responseVo;
    }
    
// 다운로드 서버 접속 실패 메세지
   public FirmwareDownAckVo setFaiiSoundMsg(FirmwareDownAckVo responseVo, FirmwareDownRequestVo vo ){
   	
   	responseVo.setFrameControl(Constants.FAIL_DATA_CONTROL_FIELD);
   	responseVo.setSeqNum(vo.getSeqNum());
   	responseVo.setCommandId(Constants.CID_NACK_DOWNSOUND);
   	responseVo.setBicycleId(vo.getBicycleId());
   	responseVo.setBicycleState(vo.getBicycleState());
   	
   	return responseVo;
   }
}
