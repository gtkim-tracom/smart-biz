package org.fincl.miss.service.biz.bicycle.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public int getPay(Map<String, Object> min, int overFee) {
    	
		
    	
    	int over = overFee;
    	int pay = 0;
    	
    	int minStr = Integer.parseInt(min.get("OVER_STR_MI").toString());
    	int minEnd = Integer.parseInt(min.get("OVER_END_MI").toString());
    	int minPay = Integer.parseInt(min.get("ADD_FEE").toString());
    	
    	//int maxStr = Integer.parseInt(max.get("OVER_STR_MI").toString());
    	//int maxPay = Integer.parseInt(max.get("ADD_FEE").toString());
    	
    	String tmp = min.get("ADD_FEE_INTER_MI")==null?"30":min.get("ADD_FEE_INTER_MI").toString();
    	int intervalMin = Integer.parseInt(tmp);
    	
    	//tmp = max.get("ADD_FEE_INTER_MI")==null?"30":max.get("ADD_FEE_INTER_MI").toString();
    	//int intervalMax = Integer.parseInt(tmp);
    	
    	logger.debug(" getPay use_MI{} overtime {} OVER_STR_MI {} ADD_FEE {} interval {}" , overFee, overFee-minStr,minStr,minPay,intervalMin);	//log 수정 
    	
    	/**
    	 * 기본초과요금 부과
    	 */
    	if(overFee - (minStr+1)>0){	//1분 가지는 초과 안한걸로 
    		/**
    		 * 추과요금 부과
    		 */
    		//int overTime = overFee;
    		int overTime = overFee - (minStr +2) ;
    		if(overTime > 0){
    			// 추과 요금 부과시간
    			int payCount = overTime/intervalMin;
    			if(overTime%intervalMin>0){
    				payCount++;
    			}
    			pay = (minPay * payCount) +minPay;	//+1000  baseTime  기본요금 + 추가요금 
    			logger.debug("기본 초과 + 추가초과 요금 : getPay_base_fee+overfee {}" , pay);	//log 수정 
    		}else{
    			/**
    			 * 기본요금만 부과.
    			 */
    			pay = minPay + minPay;	//+1000  기본요금 + 추가요금
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
	
	
	public String getBattery_Info(String s_Battery) {
    	
		
    	
		int battery = Integer.parseInt(s_Battery, 16);
 		String battery_info = null;
 		if(battery>= 90)
 		{
 			battery_info = "00";
 		}
 		else if(battery>= 66)
 		{
 			battery_info = "01";
 		}
 		else if(battery>= 33)
 		{
 			battery_info = "02";
 		}
 		else
 		{
 			battery_info = "03";
 		}
    	


    	
		return battery_info;
	}
	
	public String GetGPS(String GPS)
	{
		double d_GPS = 0.0d;
		double latDouble = (Integer.parseInt(GPS, 16))*0.000001;
		 double latip = latDouble - (latDouble - (int)latDouble) ;
		 double latfp = (latDouble - (int)latDouble)*100/60;
		    
		 //소숫점 6자리까지 계산.(버림)
		 d_GPS = Math.floor((latip+latfp)*1000000d)/1000000d;
		 return String.valueOf(d_GPS);
	}
	
	public String GetUSRSeq(String b_USR)
	{
		StringBuilder result = new StringBuilder();
		 for(int i = 0;i < b_USR.length();i++)
		 {
			 String temp = String.valueOf(Integer.parseInt(b_USR.substring(i,i+2), 16));
			 
			 if(temp != null && !temp.equals(""))
			 {
				 if (temp.length() == 1) {
					 temp = "0"+temp;
				 }
				 
				 result.append(temp);
			 }
			 i++;
		 }
		 return String.valueOf(Integer.parseInt(result.toString()));
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
		
		//System.out.println(new CommonUtil().getPay(max, min, 124));
		//System.out.println(new CommonUtil().getPay(min, max, 124));
		
	}


}
