package org.fincl.miss.service.biz.bicycle.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public int getPay(Map<String, Object> min,
			Map<String, Object> max, int overFee) {
    	
		
    	
    	int over = overFee;
    	int pay = 0;
    	
    	int minStr = Integer.parseInt(min.get("OVER_STR_MI").toString());
    	int minEnd = Integer.parseInt(min.get("OVER_END_MI").toString());
    	int minPay = Integer.parseInt(min.get("ADD_FEE").toString());
    	int tempMinEnd = 0;
    	
    	int maxStr = Integer.parseInt(max.get("OVER_STR_MI").toString());
    	int maxPay = Integer.parseInt(max.get("ADD_FEE").toString());
    	int tempmax = 0;
    	
    	String tmp = min.get("ADD_FEE_INTER_MI")==null?"30":min.get("ADD_FEE_INTER_MI").toString();
    	int intervalMin = Integer.parseInt(tmp);
    	
    	tmp = max.get("ADD_FEE_INTER_MI")==null?"30":max.get("ADD_FEE_INTER_MI").toString();
    	int intervalMax = Integer.parseInt(tmp);
    	
    	logger.debug(" getPay overtime {} minStr {} minPay {} maxStr {} maxPay {}" , overFee,minStr,minPay,maxStr,maxPay);	//log 수정 
    	
    	/**
    	 * 기본초과요금 부과
    	 */
    	if(overFee - (minStr-1)>0){
    		/**
    		 * 추과요금 부과
    		 */
    		int overTime = overFee - (maxStr -1) ;
    		if(overTime > 0){
    			// 추과 요금 부과시간
    			int payCount = overTime/intervalMax;
    			if(overTime%intervalMax>0){
    				payCount++;
    			}
    			pay = minPay + (maxPay * payCount);
    			logger.debug("기본 초과 + 추가초과 요금 : getPay_base_fee+overfee {}" , pay);	//log 수정 
    		}else{
    			/**
    			 * 기본요금만 부과.
    			 */
    			pay = minPay;
    			logger.debug("기본 초과  getPay_overfee : {}", pay);	//수정 
    		}
    		
    	}else{
    		logger.debug("초과요금 없음  getPay no_overfee");		//수정 
    	}
    	
//    	
//		for(int j=1; tempMinEnd <= minEnd; j++ ){
//			if(over >= (tempMinEnd = minStr + (intervalMin*j))){
//				pay+= minPay;
//			}
//			if(tempMinEnd > minEnd ){
//				if(over <= minEnd)
//					pay+= minPay;
//				break;
//			}
//		}
//
//		logger.debug("기본 초과 : {}", pay);
//    	
//    	if(over >= maxStr){
//    		
//    		for(int i=0; over > tempmax; i++){
//    			if(over >= (tempmax = maxStr+ (intervalMax*i)) ){
//    				pay += maxPay;
//    			}
//    			
//    			if(over < tempmax ){
//    				pay += maxPay;
//    			}
//    		}
//    		
//    		logger.debug("기본 초과 + 추가초과 요금 : {}" , pay);
//    	}

    	
		return pay;
	}
	
	
	public static void main(String[] args) {
		
		
		
		Map<String, Object> min = new HashMap<String, Object>();
		
		min.put("OVER_STR_MI", "61");
		min.put("OVER_END_MI", "90");
		min.put("ADD_FEE", "1000");
		min.put("ADD_FEE_INTER_MI", "30");
		
		
		Map<String, Object> max = new HashMap<String, Object>();
		
		max.put("OVER_STR_MI", "91");
		max.put("OVER_END_MI", "0");
		max.put("ADD_FEE", "1000");
		max.put("ADD_FEE_INTER_MI", "30");
		
		System.out.println(new CommonUtil().getPay(max, min, 124));
		System.out.println(new CommonUtil().getPay(min, max, 124));
		
	}


}
