package org.fincl.miss.service.biz.smart;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;
import org.fincl.miss.server.util.StringUtil;
import org.fincl.miss.service.biz.smart.common.SmartConst;
import org.fincl.miss.service.biz.smart.vo.ReceiveCheckInfoVo;
import org.fincl.miss.service.biz.smart.vo.ReceiveFileInfoVo;
import org.fincl.miss.service.biz.smart.vo.ReceiveFileVo;
import org.fincl.miss.service.biz.smart.vo.ReceiveRequestVo;
import org.fincl.miss.service.biz.smart.vo.SmartTransferVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.fincl.miss.service.biz.smart.service.SmartTransferService;

@RPCServiceGroup(serviceGroupName = "수신")
@Service
public class ReceiveService  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 인스턴스 ID.
	 */
	private final String instanceNodeId = UUID.randomUUID().toString();
	
	@Autowired
	private SmartTransferService SmartTransferService;
	
	/**
	 * Spring CacheManager
	 * hazelcast의 경우.
	 * com.hazelcast.spring.cache.HazelcastCacheManager를 가져온다.  
	 */
	@Autowired
	private CacheManager cacheManager;
	
	@PostConstruct
	public void init(){
		cacheManager.getCache("smartTransRepository").put("UUID", instanceNodeId);
	}
	
    /**
     * 업무개시 및 종료요청
     * 
     * 요청전문에 001 이면 시작, 002면 다음파일 존재, 003이면 다음파일 없음을 의미.
     * 004는 종료
     * @param vo
     * @return
     */
    @RPCService(serviceId = "Smart_0600", serviceName = "업무개시요청", description = "업무개시요청")
    public ReceiveRequestVo waiting(ReceiveRequestVo vo) {
    	
    	logger.debug("################### 업무개시요청 smart_0600");
    	logger.debug("ReceiveRequestVo vo : {}" , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        StringBuffer sb = new StringBuffer();
        
        ReceiveRequestVo responseVo = new ReceiveRequestVo();
        responseVo.setBizGubun(SmartConst.BIZ_GUBUN);
        responseVo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        responseVo.setCommandId(SmartConst.CommandRes.START);
        responseVo.setGubun(SmartConst.GUBUN);
        responseVo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        responseVo.setResCode(SmartConst.ResCode.OK);
        
        DateFormat sdFormat = new SimpleDateFormat("MMddHHmmss");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        responseVo.setSendDtm(tempDate);
       
        logger.debug(" getBizInfo :: {}" ,vo.getBizInfo());
        /**
         * 종료요청이 아닌 파일송신 완료(다음파일 없음)인 경우 캐쉬 정보를 지운다.
         * 키값인 파일명이 업무시작 및 종료요청시에는 없기 때문.
         */
        if(vo.getBizInfo().equals(SmartConst.Biz.EXIST_NONE)){
        	
      //  	logger.debug("before cacheManager");
        	
        	String searchDttm = (String)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|recdate").get();
        	
        	logger.debug(" STMART_003_serarchDttm :: {}" ,searchDttm);
        	
           	
        	addTransMileage(searchDttm);	//마일지지 등록
        	
        	evitCache(vo);
        }
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, vo.getBizInfo(), 3);
        responseVo.setBizInfo(sb.toString());
        
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, vo.getFileName(), 8);
        responseVo.setFileName(sb.toString());
        
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, SmartConst.userId, 20);
        responseVo.setSendName(sb.toString());
        
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, SmartConst.userPwd, 16);
        responseVo.setSendPwd(sb.toString());
        
        logger.debug(" responseVo res:: {}" ,responseVo);
        
        return responseVo;
    }
    
 // 파일정보 수신 요청 
    @RPCService(serviceId = "Smart_0630", serviceName = "파일정보수신요청", description = "파일정보수신요청")
    public ReceiveFileInfoVo waitingFileInfo(ReceiveFileInfoVo vo) {
    	
    	logger.debug("################### 파일정보수신요청 Smart_630");
    	logger.debug("ReceiveRequestVo vo : {}" , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        StringBuffer sb = new StringBuffer();
        
        ReceiveFileInfoVo responseVo = new ReceiveFileInfoVo();
        responseVo.setBizGubun(SmartConst.BIZ_GUBUN);
        responseVo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        responseVo.setCommandId(SmartConst.CommandRes.FILE_INFO);
        responseVo.setGubun(SmartConst.GUBUN);
        responseVo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        responseVo.setResCode(SmartConst.ResCode.OK);
        
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, vo.getFileName(), 8);
        responseVo.setFileName(sb.toString());
        responseVo.setRfileName(sb.toString());
        
        if(this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()) ==null 
			|| isEmpty((String)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()).get())){
        	logger.debug(" smartTransRepoistory init()" );
        	
        	initCache(vo);
        	
        	responseVo.setRfileSize("000000000000");
        }else{
        	/**
        	 * 이어받기 기능 수행을 위한 마지막 전송상태 값 조회.
        	 * 해당 날자에 이미 존재하는 경우에는 반환값에 최종수신한 Byte를 설정한다.
        	 * 상태값이 완료인 경우.전송은 완료되었으나, 최종 종류 패킷을 수신하지 못한 것으로 간주.
        	 */
			String state = (String)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|bizState").get();
		    if(state.equals("START")){
		    	int totalSize = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|totalsize").get();
				
		    	sb.setLength(0);
		        StringUtil.writeTailedSpace(sb, String.valueOf(totalSize), 12);
		    	responseVo.setRfileSize(sb.toString());
		    }else{
		    	/**
		    	 * 마지막 수신된 blockNo
		    	 */
				int rowCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|rowCount").get();
				
				sb.setLength(0);
		        StringUtil.writeTailedSpace(sb, String.valueOf(rowCount*100), 12);
		    	responseVo.setRfileSize(sb.toString());
			}
		}
		
        sb.setLength(0);
        StringUtil.writeTailedSpace(sb, vo.getByteCount(), 4);
        responseVo.setByteCount(sb.toString());
        
        return responseVo;
    }
    
 // 파일정보 수신 요청
//    /**
//     * 파일수신..Block 단위로 전송되며,
//     * 한 Block이 전송이 완료되면, 결번확인 요청을 전송한다.
//     * 결번확인 요청시 해당 블럭의 결번을 확인을 하고 반환한다.
//     * 
//     * 해당 파일 수신 요청시 전달되는 패킷은.
//     * 한 개당 최대 1M넘지 않은 범위내에서 전송되어야 하며,(전송건당 최대 9건 데이타 전송가능)
//     * 최대 100개까지 전송가능하다. (즉 한 블럭당 최대 900건(9*100)을 전송)
//     * 결번확인 요청을 오기전까지,
//     * 1) DB 저장하거나,(TB_SVC_TRANSFER_TMONEY)
//     * 2) 캐시 메모리에 저장하는 작업을 수행한다.(count)
//     * 3) File 로 백업 (YYYYMMDD_#{blobkNo}_#{seqNo})
//     * @param vo
//     */
    @RPCService(serviceId = "Smart_0320", serviceName = "파일수신요청", description = "파일수신요청")
    public ReceiveFileVo receiveData(ReceiveFileVo vo) {
    	
    	logger.debug("################### 파일수신요청 Smart_0320");
    	logger.debug("receiveData vo : {}" , vo);
        
    	
		SimpleDateFormat ndFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m::: {}" , m);
        
        /**
         * To-do
         * 파일명으로 /nas_link/spb/SEND_FILES에 저장한다.
         * 요청전문 수신시 처리할것인지. 아니면,
         * 이곳에서 처리할지는 아직 미정..테스트 후 적용 예정
         * 
         */
        logger.debug("parse........start");
        
        ArrayList<String> transState = (ArrayList<String>) this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|state").get();
        logger.debug("transState : {}", transState);
        int failCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|failCount").get();
        logger.debug("fail count : {}", failCount);
        int rowCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|rowCount").get();
        logger.debug("row count : {}", rowCount);
        int blockNo = Integer.parseInt(vo.getBlockNo());
        logger.debug("blockNo : {}", blockNo);
        int seqNo = Integer.parseInt(vo.getSeqNo());
        logger.debug("seqNo : {}", seqNo);
   //     int startCount = (seqNo -1)*9;
        String[] dataArray = vo.getData();
        logger.debug("연동데이터 수 : {}", dataArray.length);
        for(int i= 0 ; i< dataArray.length; i++){
        	String data = dataArray[i];
        	logger.debug("연동데이터 : {}", dataArray[i]);
        	String dataType = data.substring(0, 1);
        	logger.debug("연동데이터.dataType : {}", dataType);
        	try{
	        	if(dataType.equals("H")){
	        		logger.debug("1. header");
	        		String regDate = data.substring(1,9);
	        		DateFormat df = new SimpleDateFormat("yyyyMMdd");
	        		try{
	        			Date date = df.parse(regDate);
	        			
	        			/**
	        			 * 
	        			 */
	        			regDate =  sFormat.format(date);
	        			this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|recdate",regDate);
	        	
	        		}catch(Exception e){
	        			
	        		}
	        	}else if(dataType.equals("D")){
	        		
	        		int seq = Integer.parseInt(data.substring(1,11));
	        		int usrSeq = Integer.parseInt(StringUtil.replace(data.substring(11,31)," ",""));
	        		String rideDttm = data.substring(31,45);
	            	String transportCd = data.substring(45,46);
	            	String alightDttm = data.substring(46,60);
	            	
	            	SmartTransferVO transferVO = new SmartTransferVO();
	            	Date cvRdDate = ndFormat.parse(rideDttm);
					Date cvAdDate = ndFormat.parse(alightDttm);
						
					rideDttm = sdFormat.format(cvRdDate);
					alightDttm = sdFormat.format(cvAdDate);
					
	            	transferVO = new SmartTransferVO();
	            	transferVO.setSeq(String.valueOf(seq));
	            	transferVO.setUsrSeq(BigInteger.valueOf(usrSeq));
	            	transferVO.setRideDttm(rideDttm);
	            	transferVO.setTransportCd(transportCd);
	            	transferVO.setAlightDttm(alightDttm);
	            	/**
	            	 * DB 에 저장
	            	 */
	            	logger.debug("2. data");
	            	SmartTransferService.addTransTmoneyHistory(transferVO);
	            	logger.debug("2-1, data insert");
	        	}else{
	        		int totalSize = Integer.parseInt(data.substring(1,11));
	        		logger.debug("3.footer");
	        	}
	        	logger.debug("연동카운트 증가 : 1, {}",transState.size());
	        	transState.add("1");
	        	rowCount++;
        	}catch(Exception e){
        		e.printStackTrace();
        		logger.debug("연동카운트 증가 : 0, {}",transState.size());
        		transState.add("0");
        	}
        	
        }
        /*
         * 현재진행상태를 캐쉬메모리에 저장
         */
        this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|blockNo", blockNo);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|seqNo", seqNo);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|rowCount", rowCount);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|state",transState);
        this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|failCount",failCount);

        return vo;
    }

    
//    /**
//     * 결번확인 요청
//     * @param vo
//     * @return
//     */
    @RPCService(serviceId = "Smart_0620", serviceName = "결번확인요청", description = "결번확인요청")
    public ReceiveCheckInfoVo checkResend(ReceiveCheckInfoVo vo) {
    	
    	logger.debug("################### 결번확인요청 Smart_0620");
    	logger.debug("ReceiveCheckInfoVo vo : {}" , vo);
        
        
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        StringBuffer sb = new StringBuffer();
        
        vo.setCommandId(SmartConst.CommandRes.CHK_MISSING);
        vo.setResCode(SmartConst.ResCode.OK);
        
        int failCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|failCount").get();
        try {
        	vo.setFailCount(StringUtil.addZero(String.valueOf(failCount), 3));
		} catch (Exception e) {
			vo.setFailCount("000");
		}
        
        ArrayList<String> transState = (ArrayList<String>) this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|state").get();
        
        StringBuffer sb1 = new StringBuffer();
        for(String s: transState){
        	sb1.append(s);
        }
        
        try {
            sb1.append(StringUtil.addZero("", (100-transState.size())));
			vo.setFailStatus(sb1.toString());
		} catch (Exception e) {
			vo.setFailStatus(sb.toString());
		}       
        
        //전송처리 결과 초기화
        transState.clear();
        this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|state",transState);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|failCount",0);
  
		return vo;
    }
//
//    /**
//     * 전문재전송(이어받기)
//     * 0320 요청과 동일 수행
//     * 
//     * 단,  요청전문이 해당 전문을 다시 보내는지, 아니면, 결번만 따로 보내는지는 확인필요. 
//     * 해당 전문을 다시 보내는 경우,
//     * 전문재전송시 결번처리 및 전송카운트 계산로직 변경이 필요함
//     * @param vo
//     */
    @RPCService(serviceId = "Smart_0310", serviceName = "결번데이터송신", description = "결번데이터송신")
    public ReceiveFileVo resendData(ReceiveFileVo vo) {
    	
    	logger.debug("################### 파일수신요청 Smart_0310");
    	logger.debug("resendData vo : {}" , vo);
        
    	
		SimpleDateFormat ndFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
        MessageHeader m = vo.getMessageHeader();
        logger.debug(" MessageHeader m:: {}" , m);
        
        /**
         * To-do
         * 파일명으로 /nas_link/spb/SEND_FILES에 저장한다.
         * 요청전문 수신시 처리할것인지. 아니면,
         * 이곳에서 처리할지는 아직 미정..테스트 후 적용 예정
         * 
         */
        
        
        ArrayList<String> transState = (ArrayList<String>) this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|state").get();
        int failCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|failCount").get();
        int rowCount = (Integer)this.cacheManager.getCache("smartTransRepository").get(vo.getFileName()+"|r|rowCount").get();
        int blockNo = Integer.parseInt(vo.getBlockNo());
        int seqNo = Integer.parseInt(vo.getSeqNo());
   //     int startCount = (seqNo -1)*9;
//        
        String[] dataArray = vo.getData();
        logger.debug("receive Data ::::::: {}", dataArray.length);
        for(int i= 0 ; i< dataArray.length; i++){
        	String data = dataArray[i];
        	String dataType = data.substring(0, 1);
        	
        	try{
	        	if(dataType.equals("H")){
	        		String regDate = data.substring(1,9);
	        		DateFormat df = new SimpleDateFormat("yyyyMMdd");
	        		
	        		try{
	        			Date date = df.parse(regDate);
	        			
	        			
	        			/**
	        			 * yyyy-MM-dd 로 변환.
	        			 */
	        			regDate =  sFormat.format(date);
	        			this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|recdate",regDate);
	        	
	        		}catch(Exception e){
	        			
	        		}
	        		
	        		
	        	logger.debug("header ::::{}", regDate);
	        	}else if(dataType.equals("D")){
	        		
	        		int seq = Integer.parseInt(data.substring(1,11));
	        		int usrSeq = Integer.parseInt(data.substring(11,31));
	        		String rideDttm = data.substring(31,45);
	            	String transportCd = data.substring(45,46);
	            	String alightDttm = data.substring(46,60);
	            	
	            	SmartTransferVO transferVO = new SmartTransferVO();
	            	Date cvRdDate = ndFormat.parse(rideDttm);
					Date cvAdDate = ndFormat.parse(alightDttm);
						
					rideDttm = sdFormat.format(cvRdDate);
					alightDttm = sdFormat.format(cvAdDate);
					
	            	transferVO = new SmartTransferVO();
	            	transferVO.setSeq(String.valueOf(seq));
	            	transferVO.setUsrSeq(BigInteger.valueOf(usrSeq));
	            	transferVO.setRideDttm(rideDttm);
	            	transferVO.setTransportCd(transportCd);
	            	transferVO.setAlightDttm(alightDttm);
	            	/**
	            	 * DB 에 저장
	            	 */
//	            	transferMapper.addTransTmoneyHistory(transferVO);
	            	logger.debug("data ::::{}", transferVO);
	        	}else{
	        		int totalSize = Integer.parseInt(data.substring(1,11));
	        		logger.debug("footer ::::{}", totalSize);
	        	}
	        	transState.add("1");
	        	logger.debug("result ::::1");
	        	rowCount++;
        	}catch(Exception e){
        		transState.add("0");
	        	logger.debug("result ::::0");
        	}
        	
        }
        /*
         * 현재진행상태를 캐쉬메모리에 저장
         */
        this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|blockNo", blockNo);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|seqNo", seqNo);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|rowCount", rowCount);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|state",transState);
        this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|failCount",failCount);

        return vo;
    }
    
    /**
     * 캐쉬 메모리 정보 초기화.
     */
    private void initCache(ReceiveFileInfoVo vo){
    	
    	logger.debug("====== initcache : {}", vo.getFileName());
    	
    	//전송 파일명
    	this.cacheManager.getCache("smartTransRepository").put(vo.getFileName(), vo.getFileName());
		
    	logger.debug("====== smartTransRepository.{}|r|totalsize:{}",vo.getFileName(), Integer.parseInt(vo.getRfileSize()));
    	//전체파일 사이즈
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|totalsize", Integer.parseInt(vo.getRfileSize()));
		
		//전체 block Count
		int totalSize = ((Double)Math.ceil(Integer.parseInt(vo.getRfileSize()))).intValue();
		double d = (double)totalSize/900;

		Double x = Math.ceil(Double.valueOf(d));
		
		logger.debug("====== smartTransRepository.{}|r|maxBlockNo : {}",vo.getFileName(),  x.intValue());
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|maxBlockNo", x.intValue());
		
		
		logger.debug("====== smartTransRepository.{}|r|blockNo:0",vo.getFileName());
		//현재 수신된 blockNo
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|blockNo", 0);
		
//		현재수신된 seqNo
		logger.debug("====== smartTransRepository.{}|r|seqNo:0",vo.getFileName());
		logger.debug("====== smartTransRepository.{}|r|rowCount:0",vo.getFileName());
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|seqNo", 0);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|rowCount", 0);
		
		// 총 전송 예상 데이터 건수
		if(Integer.parseInt(vo.getByteCount()) >0 ){
			logger.debug("====== smartTransRepository.{}|r|totalcount:{}",vo.getFileName(), (Integer.parseInt(vo.getByteCount()) - 39)/100);
			this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|totalcount", (Integer.parseInt(vo.getByteCount()) - 39)/100);
		}else{
			logger.debug("====== smartTransRepository.{}|r|totalcount:0",vo.getFileName());
			this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|totalcount", 0);
		}
		
		// 전송처리결과
		List<String> transState = new ArrayList<String>();
		
		logger.debug("====== smartTransRepository.{}|r|state : {}",vo.getFileName(),  transState);
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|state",transState);
		logger.debug("====== smartTransRepository.{}|r|failCount : 0",vo.getFileName());
		this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|failCount",0);
		
		DateFormat sdFormat = new SimpleDateFormat("MMddHH");
	    Date nowDate = new Date();
	    String tempDate = sdFormat.format(nowDate);
	    //전송진행상황
	    logger.debug("====== smartTransRepository.{}|r|bizState : START",vo.getFileName());
	    this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|bizState","START");
	    //전송날자
	    logger.debug("====== smartTransRepository.{}|r|date : {}",vo.getFileName() ,tempDate);
	    this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|date",tempDate);
	    
	    DateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date nDate = new Date();
	    String sDate = sFormat.format(nDate);
	    
	    logger.debug("====== smartTransRepository.{}|r|recDate : {}",vo.getFileName() ,tempDate);
	    this.cacheManager.getCache("smartTransRepository").put(vo.getFileName()+"|r|recdate",sDate);
    }


    /**
     * 캐쉬 메모리에서 데이타 삭제.
     */
    private void  evitCache(ReceiveRequestVo vo){
    	
    	//전송 파일명
    	this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName());
		
		//전체파일 사이즈
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|totalsize");
		
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|maxBlockNo");
		
		//현재 수신된 blockNo
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|blockNo");
		
//		현재수신된 seqNo
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|seqNo");
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|rowCount");
		
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|totalcount");
		
		// 전송처리결과
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|state");
		this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|failCount");
		
	    this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|bizState");
	    //전송날자
	    this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|date");
	    this.cacheManager.getCache("smartTransRepository").evict(vo.getFileName()+"|r|recdate");
    }

	/**
	 * 파라미터의 두인자의 객체가 동일한지 여부를 확인한다.
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	private boolean objectEquals(Object one, Object two) {
		if ((one == null) && (two != null)) {
			return false;
		}
		return ((one == null) || (one.equals(two)));
	}
	
	/**
	 * 빈 문자열 여부를 확인한다.
	 * Null 또는 공백문자인 경우. true를 반환한다.
	 * 
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
	/**
	 * 주어진 사이즈 내 좌측 공백을 0으로 채운다
	 * @param strx
	 * @param length
	 * @return
	 */
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
	
	private void addTransMileage(String searchDttm) {
		
		int result = 0;
		int	cnt = 0;
		int	usrseq;
		
		logger.debug("################### 마일리지 등록  addTransMileage ################");
		
		SmartTransferVO transferVO = new SmartTransferVO();
		transferVO.setSearchDttm(searchDttm);
		
	
		result = SmartTransferService.setFinishTmoney(transferVO);
		
		logger.debug("setFinishTmoney : searchDttm {}", searchDttm);
		
//		List<SmartTransferVO> transMileList = SmartTransferService.getTransMileList(transferVO);
		
		//test
//		usrseq = 532756;
	//	transferVO.setUsrSeq(BigInteger.valueOf(usrseq));
	//	transferVO.setRentHistSeq("6657216");
		
//		cnt = SmartTransferService.getMileageCount(transferVO);
		
//		logger.debug("transMileList_size {}", transMileList.size());
		
		//test
		/*
		if(transMileList.size() > 0){
			for(SmartTransferVO mileVO:transMileList){
				transferVO = new SmartTransferVO();
				
				transferVO.setUsrSeq(mileVO.getUsrSeq());
				transferVO.setMbCardSeq(mileVO.getMbCardSeq());
				transferVO.setMileageClsCd(mileVO.getMileageClsCd());
				transferVO.setMileagePoint(mileVO.getMileagePoint());
				transferVO.setRentHistSeq(mileVO.getRentHistSeq());
				transferVO.setTransferSeq(mileVO.getTransferSeq());
				
				try{
					
			//		cnt = SmartTransferService.getMileageCount(transferVO);
					
					logger.debug("addTransMileage usr_seq: {}", mileVO.getUsrSeq()," histseq",mileVO.getRentHistSeq(),"  transSeq",mileVO.getTransferSeq()," carseq",mileVO.getMbCardSeq()," clscd",mileVO.getMileageClsCd());
					
					if(cnt <= 0)
					{
									
						result = SmartTransferService.addTransMileage(transferVO);
						result = SmartTransferService.setTransTmoney(transferVO);
					}
					
				}catch(Exception e){}
			}
		}
		*/
		
	}
	
		
    
}
