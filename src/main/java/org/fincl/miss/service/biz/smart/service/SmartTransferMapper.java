package org.fincl.miss.service.biz.smart.service;

import java.util.List;

import org.fincl.miss.service.biz.smart.vo.SmartTransferVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("SmartTransferMapper")
public interface SmartTransferMapper {
	public List<SmartTransferVO> getTransCardList();
	public int addTransTmoneyHistory(SmartTransferVO transferVO);
	public List<SmartTransferVO> getTransMileList(SmartTransferVO transferVO);
	public int addTransMileage(SmartTransferVO transferVO);
	public int setTransTmoney(SmartTransferVO transferVO);
}
