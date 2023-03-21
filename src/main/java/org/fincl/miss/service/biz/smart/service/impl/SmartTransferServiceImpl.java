package org.fincl.miss.service.biz.smart.service.impl;

import java.util.List;

import org.fincl.miss.service.biz.smart.service.SmartTransferMapper;
import org.fincl.miss.service.biz.smart.service.SmartTransferService;
import org.fincl.miss.service.biz.smart.vo.SmartTransferVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartTransferServiceImpl implements SmartTransferService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmartTransferMapper smartTransferMapper;
    
	@Override
	public List<SmartTransferVO> getTransCardList() {
		return this.smartTransferMapper.getTransCardList();
	}

	@Override
	public int addTransTmoneyHistory(SmartTransferVO transferVO) {
		return this.smartTransferMapper.addTransTmoneyHistory(transferVO);
	}

	@Override
	public List<SmartTransferVO> getTransMileList(SmartTransferVO transferVO) {
		return this.smartTransferMapper.getTransMileList(transferVO);
	}

	@Override
	public int addTransMileage(SmartTransferVO transferVO) {
		return this.smartTransferMapper.addTransMileage(transferVO);
	}

	@Override
	public int setTransTmoney(SmartTransferVO transferVO) {
		return this.smartTransferMapper.setTransTmoney(transferVO);
	}

}
