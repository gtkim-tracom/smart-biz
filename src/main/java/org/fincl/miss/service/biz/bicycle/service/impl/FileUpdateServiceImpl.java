package org.fincl.miss.service.biz.bicycle.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateMapper;
import org.fincl.miss.service.biz.bicycle.service.FileUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileUpdateServiceImpl implements FileUpdateService {

	
	@Autowired
	private FileUpdateMapper fileMapper;

	@Override
	public List<HashMap<String, Object>> getFileInfo(String fileSeq) {
		return fileMapper.getFileInfo(fileSeq);
	}
	
	@Override
	public List<HashMap<String, Object>> getFileInfo(CommonVo com){
		return fileMapper.getFileInfo(com);
	}

	@Override
	public Map<String, Object> getVersion(CommonVo com) {
		
		Map<String, Object> vs = new HashMap<String, Object>();
		
		com.setFirmwareClsCd("FWD_001");
		Map<String, Object> tmp = fileMapper.getVersion(com);
		
		if(tmp != null){
			vs.put("FIRMWARE_MODEM_VER", tmp.get("FIRMWARE_VER"));
			vs.put("FIRMWARE_MODEM_SEQ", tmp.get("FIRMWARE_SEQ"));
			vs.put("FIRMWARE_MODEM_USE_YN", tmp.get("USE_YN"));
			vs.put("FIRMWARE_MODEM_TIME_CAN_DOWN", tmp.get("TIME_CAN_DOWN"));
			vs.put("FIRMWARE_MODEM_BIKE_CAN_DOWN", tmp.get("BIKE_CAN_DOWN"));
		}else{
			vs.put("FIRMWARE_MODEM_VER", "0.0");
			vs.put("FIRMWARE_MODEM_SEQ", 0);
			vs.put("FIRMWARE_MODEM_USE_YN", "N");
			vs.put("FIRMWARE_MODEM_TIME_CAN_DOWN", "N");
			vs.put("FIRMWARE_MODEM_BIKE_CAN_DOWN", "N");
		}
		/*
		com.setFirmwareClsCd("FWD_002");
		tmp = fileMapper.getVersion(com);
		
		if(tmp != null){
			vs.put("FIRMWARE_MODEM_VER", tmp.get("FIRMWARE_VER"));
			vs.put("FIRMWARE_MODEM_SEQ", tmp.get("FIRMWARE_SEQ"));
			vs.put("FIRMWARE_MODEM_USE_YN", tmp.get("USE_YN"));
			vs.put("FIRMWARE_MODEM_TIME_CAN_DOWN", tmp.get("TIME_CAN_DOWN"));
			vs.put("FIRMWARE_MODEM_BIKE_CAN_DOWN", tmp.get("BIKE_CAN_DOWN"));
		}else{
			vs.put("FIRMWARE_MODEM_VER", "0.0");
			vs.put("FIRMWARE_MODEM_SEQ", 0);
			vs.put("FIRMWARE_MODEM_USE_YN", "Y");
			vs.put("FIRMWARE_MODEM_TIME_CAN_DOWN", "Y");
			vs.put("FIRMWARE_MODEM_BIKE_CAN_DOWN", "Y");
		}
		*/
		/*
		com.setFirmwareClsCd("FWD_003");
		tmp = fileMapper.getVersion(com);
		
		if(tmp != null){
			vs.put("VOICE_VER", tmp.get("FIRMWARE_VER"));
			vs.put("VOICE_SEQ", tmp.get("FIRMWARE_SEQ"));
			vs.put("VOICE_USE_YN", tmp.get("USE_YN"));
			vs.put("VOICE_TIME_CAN_DOWN", tmp.get("TIME_CAN_DOWN"));
			vs.put("VOICE_BIKE_CAN_DOWN", tmp.get("BIKE_CAN_DOWN"));
		}else{
			vs.put("VOICE_VER", "0.0");
			vs.put("VOICE_SEQ", 0);
			vs.put("VOICE_USE_YN", "Y");
			vs.put("VOICE_TIME_CAN_DOWN", "Y");
			vs.put("VOICE_BIKE_CAN_DOWN", "Y");
		}
		*/
		return vs;
	}

	@Override
	public void firmwareUpdateComplete(CommonVo com) 
	{
		
		Map<String, Object> tmp = fileMapper.getVersion(com);
		
		com.setFirmwareSeq(tmp.get("FIRMWARE_SEQ").toString());	
	
		fileMapper.updateFirmareUpdateResult(com);
		
		
		fileMapper.updateDeviceFirmware(com);
	}

	@Override
	public void firmwareUpdateRequest(CommonVo com) {
		/**
		 * 중복 등록 제외..이미 등록된 업데이트 시작정보가 있으면 등록하지 않음.
		 */
		if(fileMapper.getFirmwareStarted(com) == 0){
			fileMapper.insertFirmareUpdateResult(com);
		}
	}

	@Override
	public HashMap<String, Object> getFileWithFileNo(CommonVo com) {
		return fileMapper.getFileWithFileNo(com);
	}

	@Override
	public Map<String, Object> getHasNext(CommonVo com) {
		return fileMapper.getHasNext(com);
	}

}
