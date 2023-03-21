package org.fincl.miss.service.biz.smart.service;

import java.util.List;

import org.fincl.miss.service.biz.smart.vo.SmartTransferVO;

public interface SmartTransferService {
	public List<SmartTransferVO> getTransCardList();
	public int addTransTmoneyHistory(SmartTransferVO transferVO);
	public List<SmartTransferVO> getTransMileList(SmartTransferVO transferVO);
	public int addTransMileage(SmartTransferVO transferVO);
	public int setTransTmoney(SmartTransferVO transferVO);
}
