package org.fincl.miss.service.biz.bicycle.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;

public interface FileUpdateService {


	List<HashMap<String, Object>> getFileInfo(String fileSeq);
	
	List<HashMap<String, Object>> getFileInfo(CommonVo com);

	HashMap<String, Object> getFileWithFileNo(CommonVo com);
	
	Map<String, Object> getVersion(CommonVo com);

	void firmwareUpdateComplete(CommonVo com);

	void firmwareUpdateRequest(CommonVo com);

	Map<String, Object> getHasNext(CommonVo com);
}
