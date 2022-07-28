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
import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.Constants;
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
	
	
	
	/* 펌웨어 다운로드 접속 ORIGIN_20170530_JJH_START
	 // 다운로드 서버 접속
    @RPCService(serviceId = "Bicycle_55", serviceName = "다운로드 서버 접속 Request", description = "다운로드 서버 접속 Request")
    public ConnectDownServerResponseVo connectServer(ConnectDownServerRequestVo vo) {
    	
    	logger.debug("RentWaitingRequestVo vo : {}" , vo);
        
    	ConnectDownServerResponseVo responseVo = new ConnectDownServerResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        
        // 버전 체크
        Map<String, Object> serverVersion = fileService.getVersion(com);
        
        double requsetFw  = Double.parseDouble(vo.getFirmwareVs().substring(0,2) + "." + vo.getFirmwareVs().substring(2, 4));
		double requsetImg = Double.parseDouble(vo.getImageVs().substring(0,2) + "." + vo.getImageVs().substring(2, 4));
		double requsetSd =  Double.parseDouble(vo.getSoundVs().substring(0,2) + "." + vo.getSoundVs().substring(2, 4));
		
		 
        double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_VER").toString());
        double serverImg = Double.parseDouble(serverVersion.get("IMAGE_VER").toString());
        double serverSd = Double.parseDouble(serverVersion.get("VOICE_VER").toString());
        

		boolean fwUseYn = serverVersion.get("FIRMWARE_USE_YN").equals("Y");
		boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
		boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
		 
        
		String fileSeq = "";
		
		Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){	// 펌웨어 업데이트 대상 자전거 선별 (7290 : 2100/3600/220/1090/280 CW BJ 확인)_20170329_JJH
			logger.debug("### YES : 펌웨어 업데이트 ###  해당 자전거는 펌웨어 업데이트 대상임~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
			
	        if(requsetFw < serverFw && fwUseYn){
	        	logger.debug("FW 업데이트 ");
	        	
	        	fileSeq = serverVersion.get("FIRMWARE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_01")); // F/W 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverFw)));
	        	
	        }else if(requsetImg < serverImg &&imgUseYn){
	        	logger.debug("IMG 업데이트 ");
	        	fileSeq = serverVersion.get("IMAGE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_02")); // IMG 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverImg)));
	        }else if(requsetSd < serverSd &&sdUseYn){
	        	logger.debug("SD 업데이트 ");
	        	fileSeq = serverVersion.get("VOICE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_03")); // SOUND 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverSd)));
	        }else{
	        	logger.debug("업데이트 진행 안함");
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // 업데이트 진행 안함
	        }
		}else{
			responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // 업데이트 진행 안함
			
			logger.debug("### NO : 펌웨어 업데이트 ### 해당 자전거는 펌웨어 업데이트 대상이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")));
		}
        
        System.out.println(responseVo);
   
        if( !responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_00"))){
        	
        	// 펌웨어 업데이트 결과 요청 insert
        	com.setFirmwareSeq(fileSeq);
        	fileService.firmwareUpdateRequest(com);
        	
        	
        	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
        	
  //      	logger.debug("fileList : {}" ,fileList);
        	if(fileList.size() == 0 ){
        		logger.error("update 파일 찾기 오류  ");
            	responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
            	responseVo = setFaiiMsg(responseVo, vo);
            	
            	return responseVo;
        	}else{
        		
        		// 업데이트 파일이 한개인 경우
        		if(fileList.size() == 1){
        			
        			
        			String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
        			File file = new File(fileName);
        			
        			if(file.isFile()){
        				
        				// 4096 MB 이상 파일 
        				if(((file.length()/1024)/1024) > (4096)){
        					logger.error( " :::::::::::  update 파일 error : {}" , fileName);
            				responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
            				responseVo = setFaiiMsg(responseVo, vo);
            				
            				return responseVo;
        				}
        				
        				if(responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_01"))){
        					double d = (double)file.length()/512;
        					Double x = Math.ceil(Double.valueOf(d));
        					responseVo.setPacketSize(getToString(String.valueOf(x.intValue() ) , 4));
        					
                		}else{
                			responseVo.setPacketSize("0001");
                		}
        				responseVo.setTotal(getToString(String.valueOf(file.length()), 8) );
        				
        			}else{
        				logger.error( " ############# update 파일 error : {} " , fileName);
        				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
        				responseVo = setFaiiMsg(responseVo, vo);
        				
        				return responseVo;
        			}
        			
        			responseVo.setFileCnt("01");
        			
        		}else{
        			// 업데이트 파일이 한개 이상인 경우 
        			
        			double totalSize = 0;
        			for (int i = 0; i < fileList.size(); i++) {
        				String fileName = fileList.get(i).get("FILE_SAVE_PATH")+File.separator + fileList.get(i).get("FILE_NAME");
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
    펌웨어 다운로드 접속 ORIGIN_20170530_JJH_END */
	
	@RPCService(serviceId = "Bicycle_55", serviceName = "다운로드 서버 접속 Request", description = "다운로드 서버 접속 Request")
    public ConnectDownServerResponseVo connectServer(ConnectDownServerRequestVo vo) {
    	
    	logger.debug("update connectServer Bicycle_55(4350)  vo : {}" , vo);
        
    	ConnectDownServerResponseVo responseVo = new ConnectDownServerResponseVo();
        
        CommonVo com = new CommonVo();
        com.setBicycleId(vo.getBicycleId());
        
        Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
        
        // 버전 체크
        Map<String, Object> serverVersion = fileService.getVersion(com);
        
        double requsetFw  = Double.parseDouble(vo.getFirmwareVs().substring(0,2) + "." + vo.getFirmwareVs().substring(2, 4));
		double requsetImg = Double.parseDouble(vo.getImageVs().substring(0,2) + "." + vo.getImageVs().substring(2, 4));
		double requsetSd =  Double.parseDouble(vo.getSoundVs().substring(0,2) + "." + vo.getSoundVs().substring(2, 4));
		
		 
        double serverFw = Double.parseDouble(serverVersion.get("FIRMWARE_VER").toString());
        double serverImg = Double.parseDouble(serverVersion.get("IMAGE_VER").toString());
        double serverSd = Double.parseDouble(serverVersion.get("VOICE_VER").toString());
        

		boolean fwUseYn = serverVersion.get("FIRMWARE_USE_YN").equals("Y");
		boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
		boolean sdUseYn = serverVersion.get("VOICE_USE_YN").equals("Y");
		
		String fileSeq = "";
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){	// 펌웨어 업데이트 대상 자전거 선별 (7290 : 2100/3600/220/1090/280 CW BJ 확인)_20170329_JJH
			logger.debug("### YES : 펌웨어 업데이트 ###  해당 자전거는 펌웨어 업데이트 대상임~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
			
	        if(requsetFw < serverFw && fwUseYn){
	        	logger.debug("FW 업데이트 ");
	        	
	        	fileSeq = serverVersion.get("FIRMWARE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_01")); // F/W 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverFw)));
	        	
	        }else if(requsetImg < serverImg &&imgUseYn){
	        	logger.debug("IMG 업데이트 ");
	        	fileSeq = serverVersion.get("IMAGE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_02")); // IMG 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverImg)));
	        }else if(requsetSd < serverSd &&sdUseYn){
	        	logger.debug("SD 업데이트 ");
	        	fileSeq = serverVersion.get("VOICE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_03")); // SOUND 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverSd)));
	        }else{
	        	logger.debug("업데이트 진행 안함");
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // 업데이트 진행 안함
	        }
		}else if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("N")){
			logger.debug("###  YES : 펌웨어 업데이트 ### 해당 자전거는 위트콤 펌웨어 업데이트 대상임~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
			
			if(requsetFw < serverFw && fwUseYn){
	        	logger.debug("FW 업데이트 ");
	        	
	        	fileSeq = serverVersion.get("FIRMWARE_SEQ").toString();
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_01")); // F/W 업데이트 진행
	        	responseVo.setVersion(versionToHex(String.valueOf(serverFw)));
	        	
	        }else{
	        	responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // F/W 업데이트 진행
	        	logger.debug("###  위트콤 펌웨어 업데이트 버전체크 ### 해당 자전거는 업데이트 대상이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
	        }
			
		}else{
			responseVo.setUpdate(Constants.CODE.get("UPDATE_00")); // 업데이트 진행 안함
			
			logger.debug("### NO : 펌웨어 업데이트 ### 해당 자전거는 펌웨어 업데이트 대상이 아님~!! 자전거 번호 : " + String.valueOf(ourBikeMap.get("BIKE_NO")) + ", 자전거 ID : " + String.valueOf(ourBikeMap.get("BIKE_ID")) + ", COMPANY_CD : " + String.valueOf(com.getCompany_cd()));
		}
        
        System.out.println(responseVo);
   
        if( !responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_00"))){
        	
        	// 펌웨어 업데이트 결과 요청 insert
        	com.setFirmwareSeq(fileSeq);
        	fileService.firmwareUpdateRequest(com);
        	
        	
        	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
        	
  //      	logger.debug("fileList : {}" ,fileList);
        	if(fileList.size() == 0 ){
        		logger.error("update 파일 찾기 오류  ");
            	responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
            	responseVo = setFaiiMsg(responseVo, vo);
            	
            	return responseVo;
        	}else{
        		
        		// 업데이트 파일이 한개인 경우
        		if(fileList.size() == 1){
        			
        			
        			String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
        			logger.debug("##### 업데이트 파일이 한개인 경우 ##### => 다운로드 서버 접속 파일이름 : " + fileName);
        			File file = new File(fileName);
        			
        			if(file.isFile()){
        				
        				// 4096 MB 이상 파일 
        				if(((file.length()/1024)/1024) > (4096)){
        					logger.error( " :::::::::::  update 파일 error : {}" , fileName);
            				responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
            				responseVo = setFaiiMsg(responseVo, vo);
            				
            				return responseVo;
        				}
        				
        				if(responseVo.getUpdate().equals(Constants.CODE.get("UPDATE_01"))){
        					double d = (double)file.length()/512;
        					Double x = Math.ceil(Double.valueOf(d));
        					responseVo.setPacketSize(getToString(String.valueOf(x.intValue() ) , 4));
        					
                		}else{
                			responseVo.setPacketSize("0001");
                		}
        				responseVo.setTotal(getToString(String.valueOf(file.length()), 8) );
        				
        			}else{
        				logger.error( " ############# update 파일 error : {} " , fileName);
        				responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
        				responseVo = setFaiiMsg(responseVo, vo);
        				
        				return responseVo;
        			}
        			
        			responseVo.setFileCnt("01");
        			
        		}else{
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
    
    
    // 이미지 파일정보 다운로드 
    /*@RPCService(serviceId = "Bicycle_67", serviceName = "이미지 파일정보 다운로드 Request", description = "이미지 파일정보 다운로드 Request")
    public ImageFileInfoDownAckVo  downloadImageData(ImageFileInfoDownRequestVo vo) {
    	
    	
    	logger.debug("ImageFileInfoDownRequestVo vo : {}" , vo);
    	
    	String fileSeq = "";
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	
    	ImageFileInfoDownAckVo responseVo = new ImageFileInfoDownAckVo();
    	
    	
    	Map<String, Object> serverVersion = fileService.getVersion(com);
		 boolean imgUseYn = serverVersion.get("IMAGE_USE_YN").equals("Y");
    	fileSeq = serverVersion.get("IMAGE_SEQ").toString();
    	
    	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
    	
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
    */
    
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
    
    
    // f.w 데이터 다운로드 
    /* fw 파일 데이터 다운로드 ORIGIN_20170530_JJH_START
    @RPCService(serviceId = "Bicycle_58", serviceName = "fw 파일 데이터 다운로드 Request", description = "fw 파일 데이터 다운로드 Request")
    public FirmwareDownAckVo downloadData(FirmwareDownRequestVo vo) {
    	
    	logger.debug("FirmwareDownRequestVo vo : {}" , vo);
    	
    	FirmwareDownAckVo responseVo = new FirmwareDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	// 버전 체크
    	Map<String, Object> deviceVersion = fileService.getVersion(com);
    	
    	boolean fwUseYn = deviceVersion.get("FIRMWARE_USE_YN").equals("Y");
		
    	String fileSeq = deviceVersion.get("FIRMWARE_SEQ").toString();
    	
    	if(!fwUseYn){
    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiMsg(responseVo, vo);
			
			return responseVo;
    	}
    	
    	List<HashMap<String, Object>> fileList = fileService.getFileInfo(fileSeq);
    		
    	System.out.println(fileList);
    	
    	int packet = Integer.parseInt(vo.getPacketNum(),16);
    	
    	
    	String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
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
    			logger.error( " :::::::::::  update 파일 error :{}" , fileName);
    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
    			responseVo = setFaiiMsg(responseVo, vo);
    			
    			return responseVo;
    		}
    		
    		
    	}else{
    		System.out.println( " ############# update 파일 error " + fileName);
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
    fw 파일 데이터 다운로드 ORIGIN_20170530_JJH_END */
    
    @RPCService(serviceId = "Bicycle_58", serviceName = "fw 파일 데이터 다운로드 Request", description = "fw 파일 데이터 다운로드 Request")
    public FirmwareDownAckVo downloadData(FirmwareDownRequestVo vo) {
    	
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
    	
    	// 버전 체크
    	Map<String, Object> deviceVersion = fileService.getVersion(com);
    	
    	boolean fwUseYn = deviceVersion.get("FIRMWARE_USE_YN").equals("Y");
		
    	String fileSeq = "";
    	
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();
    	
    	if(fwUseYn){
    		fileSeq = deviceVersion.get("FIRMWARE_SEQ").toString();
    		fileList = fileService.getFileInfo(fileSeq);
    		
    	}else{
    		logger.debug("##### fw 파일 데이터 다운로드 조건이 아님. ##### => fileSeq : " + String.valueOf(deviceVersion.get("FIRMWARE_SEQ")) + ", 자전거 ID : " + vo.getBicycleId());
    	}
    	
    	if(fileList.size() == 0 || !fwUseYn){
    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
			responseVo.setErrorId(Constants.CODE.get("ERROR_D7"));
			responseVo = setFaiiMsg(responseVo, vo);
			
			return responseVo;
    	}

    		
    	System.out.println(fileList);
    	
    	int packet = Integer.parseInt(vo.getPacketNum(),16);
    	
    	
    	String fileName = fileList.get(0).get("FILE_SAVE_PATH")+File.separator + fileList.get(0).get("FILE_NAME");
    	logger.debug("##### fw 파일 데이터 다운로드 ##### => 다운로드 서버 접속 파일이름 : " + fileName);
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
    			logger.error( " :::::::::::  update 파일 error :{}" , fileName);
    			responseVo.setErrorId(Constants.CODE.get("ERROR_D6"));
    			responseVo = setFaiiMsg(responseVo, vo);
    			
    			return responseVo;
    		}
    		
    		
    	}else{
    		System.out.println( " ############# update 파일 error " + fileName);
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
    	
    	logger.debug("FirmwareDownRequestVo vo :{}" , vo);
    	
    	CompleteDownAckVo responseVo = new CompleteDownAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
    	
    	responseVo.setAddDown("00");
    	if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_01"))){
    		responseVo.setAddDown("00");
        }else if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_02"))){
    		
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
    		Map<String, Object> checkVal = fileService.getHasNext(com);
    		if(Integer.parseInt(String.valueOf(checkVal.get("HAS_NEXT")))>0){
    			responseVo.setAddDown("01");
    		}
    	}else{
    		Map<String, Object> deviceVersion = fileService.getVersion(com);
        	String fileSeq = deviceVersion.get("VOICE_SEQ").toString();
        	
        	boolean sdUseYn = deviceVersion.get("VOICE_USE_YN").equals("Y");
   	   		
      		if(!sdUseYn){
   	    		logger.debug( " ############# update 파일 없음 : {}" , fileSeq );
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
    	
    	logger.debug("FirmwareDownRequestVo vo : {}" , vo);
    	
    	CompleteUpdateAckVo responseVo = new CompleteUpdateAckVo();
    	
    	CommonVo com = new CommonVo();
    	com.setBicycleId(vo.getBicycleId());
    	com.setVersion(vo.getFirmwareVersion());
    	
    	
    	if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_01"))){
    		com.setFirmwareClsCd("FWD_001");
    	}else if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_02"))){
    		com.setFirmwareClsCd("FWD_002");
    	}else if(vo.getUpdateType().equals(Constants.CODE.get("UPDATE_03"))){
    		com.setFirmwareClsCd("FWD_003");
    	}
    	
    	Map<String, String> ourBikeMap = new HashMap<String, String>();
		ourBikeMap = bikeService.chkOurBike(com);
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
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
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
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
		
		if(String.valueOf(ourBikeMap.get("FIRMWARE_DOWN_YN")).equals("Y")){
			com.setCompany_cd("CPN_001");
		}else{
			com.setCompany_cd("CPN_002");
		}
    	
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
