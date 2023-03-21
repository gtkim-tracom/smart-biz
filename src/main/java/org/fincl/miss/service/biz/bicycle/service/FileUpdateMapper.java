package org.fincl.miss.service.biz.bicycle.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("FileUpdateMapper")
public interface FileUpdateMapper {


	List<HashMap<String, Object>> getFileInfo(String fileSeq);
	
	List<HashMap<String, Object>> getFileInfo(CommonVo com);

	HashMap<String, Object> getFileWithFileNo(CommonVo com);
	
	Map<String, Object> getVersion(CommonVo com);
	
	Map<String, Object> getHasNext(CommonVo com);

	void insertFirmareUpdateResult(CommonVo com);

	void updateFirmareUpdateResult(CommonVo com);

	void updateDeviceFirmware(CommonVo com);

	int getFirmwareStarted(CommonVo com);
}
