/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push.message
 * @파일명          : APNSMessageBuilder.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util.push.message;

import org.fincl.miss.server.util.IConstants;
import org.fincl.miss.server.util.StringUtil;
import org.fincl.miss.service.util.push.PushVO;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javapns.notification.PushNotificationPayload;
/**
 * @파일명          : APNSMessageBuilder.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public class APNSMessageBuilder implements MessageBuilder  {
	private volatile static APNSMessageBuilder instance = new APNSMessageBuilder();	

	private final static String DELIMITER = "\"";
	private final static int MAX_SIZE = 250;
	private final static int MSG_MAX_SIZE = 160;
	private APNSMessageBuilder() {
	}
	
	public static APNSMessageBuilder getInstance() {
		return instance;
	}
	/**
	 * @location   : org.fincl.miss.service.util.push.message.MessageBuilder.build
	 * @writeDay   : 2015. 8. 20. 오후 7:50:46
	 * @overridden : @see org.fincl.miss.service.util.push.message.MessageBuilder#build(org.fincl.miss.service.util.push.PushVO)
	 * @Todo       :
	 */ 
	@Override
	public Object build(PushVO pushVo) throws Exception {
		int sendSeq = Integer.parseInt(pushVo.getMessageId());
		String deviceToken = pushVo.getTokenId();
		
		validateTokenFormat(deviceToken);
		
		String testYn = "Y";
		
		resizeMessage(pushVo);
		
		if("Y".equals(testYn)) {
			return makeJavaPnsPayload(pushVo);
		}
		
		String payload = makeAPNSPayload(pushVo);
		if(payload == null) return null;
		return makePushMsg(sendSeq, deviceToken, payload);
	}
	private void resizeMessage(PushVO pushVo) throws Exception  {
		
		String alert = pushVo.getAlert();
		String message = pushVo.getMessage();
		String ellipsis = "...";
		
		alert = setReplace1(alert);
		alert = setReplace2(alert);
		message = setReplace1(message);
		message = setReplace2(message);
		
		int useSize = MSG_MAX_SIZE - getSize(alert);
		if(useSize < ellipsis.length()) {
			useSize = MSG_MAX_SIZE;
			alert = ""; 
		}
		
		int msgSize = getSize(message);
		
		if((msgSize - useSize) > 0) {
			String[] tempMsg = StringUtil.cutString(message, useSize-3, "UTF-8");
			if(tempMsg == null) message = ellipsis;
			else message = tempMsg[0] + ellipsis;
		}
		pushVo.setAlert(alert);;
		pushVo.setMessage(message);
		
	}
	

	public static PushNotificationPayload makeJavaPnsPayload(PushVO pushVo) throws Exception {
		
		PushNotificationPayload payload = PushNotificationPayload.complex();
		HashMap<String, String> unit = new HashMap<String, String>();
		
		String messageId = pushVo.getMessageId();
		String alert = pushVo.getAlert();
		String message = pushVo.getMessage();
		String badge = pushVo.getBedge();
		String sound = pushVo.getSound();
		String encryptyn = pushVo.getEncryptYn();
		
		if(alert == null     || "".equals(alert)) alert = "";
		if(sound == null     || "".equals(sound)) sound = "default";
		if(badge == null     || "".equals(badge)) badge = "1";
		
		payload.addCustomDictionary("encryptyn", encryptyn);
		payload.addCustomDictionary("messageid", messageId);
		payload.addCustomDictionary("message", setReplace(message));
		
		payload.addAlert(alert);
		payload.addSound(sound);
		payload.addBadge(Integer.parseInt(badge));
		
		if(payload.getPayloadSize() > MAX_SIZE) {
			unit.put(IConstants.RESULT_CODE, IConstants.SVC_MESSAGE_SIZE_EXCEED);
			return null;
		}
		
		return payload;
	}
	
	public static String makeAPNSPayload(PushVO pushVo) throws Exception {
		String result = null;
        HashMap<String, String> unit = new HashMap<String, String>();
		
		String messageId = pushVo.getMessageId();
		String alert = pushVo.getAlert();
		String message = pushVo.getMessage();
		String badge = pushVo.getBedge();
		String sound = pushVo.getSound();
		String encryptyn = pushVo.getEncryptYn();
		String urlLink = pushVo.getPushLinkUrl();
		
		if(message == null   || "".equals(message))   return null;
		if(alert == null     || "".equals(alert)) alert = "";
		if(sound == null     || "".equals(sound)) sound = "default";
		if(badge == null     || "".equals(badge)) badge = "1";		

		StringBuffer payload = new StringBuffer();
		
		payload.append("{\"aps\":{");
		payload.append("\"alert\":\"").append(alert).append(DELIMITER);
		payload.append(",\"sound\":\"").append(sound).append(DELIMITER);
		payload.append(",\"badge\":").append(badge);
		payload.append("}");
		payload.append(",\"").append("encryptyn").append("\":\"").append(encryptyn).append(DELIMITER);
		payload.append(",\"").append("messageid").append("\":\"").append(messageId).append(DELIMITER);		
		payload.append(",\"").append("message").append("\":\"").append(message).append(DELIMITER);
		payload.append(",\"").append("link").append("\":\"").append(urlLink).append(DELIMITER);
		payload.append("}");

//		if(getSize(result) > MAX_SIZE) {			
//			unit.put(Constant.RESULT_CODE, IConstants.SVC_MESSAGE_SIZE_EXCEED);
//			return null;
//		}
		
		if(payload != null && payload.length() > 0) result = payload.toString();
		
		return result;
	}
	
	
	
	public static byte[] makePushMsg(int sendSeq, String deviceToken, String payload) throws Exception {
		
		ByteArrayOutputStream baos = null;
		baos = new ByteArrayOutputStream();
		
		// Command
		baos.write((byte) 0x01);
		
		// Identifier
		baos.write(intToByteArray(sendSeq));
		
		// Expiry
		baos.write(intToByteArray((int)(new GregorianCalendar().getTimeInMillis() / 1000) + 60));
		
		try {
			baos.write(int2byteArr(deviceToken.length() / 2, 2));
			
			// deviceToken
			for ( int i = 0 ; i < deviceToken.length() ; i += 2 ) {
				String s = deviceToken.substring(i, i + 2);
				int    b = Integer.parseInt(s, 16);
				baos.write((byte)b);
			}
		
		} catch (Exception e) {
			throw new Exception(IConstants.APNS_INVALID_TOKEN);
		}
		
		
		// Payload length  
		baos.write(int2byteArr(payload.getBytes("UTF-8").length, 2));

		// Payload
		baos.write(payload.getBytes("UTF-8"));
		
		return baos.toByteArray();
	}
	
	public static String setReplace1(String value) {
    	if(value.indexOf("\r\n") > -1 ) value = getReplace(value, "\r\n", "\\r");
        if(value.indexOf("\n") > -1 )   value = getReplace(value, "\n", "\\r");
        if(value.indexOf("\r") > -1 )   value = getReplace(value, "\r", "\\r");
    	return value;
    }
    
    public static String setReplace2(String value) {
        if(value.indexOf("\\") > -1 ) value = getReplace(value, "\\", "\\\\");
        if(value.indexOf("\"") > -1 ) value = getReplace(value, "\"", "\\\"");
        return value;
    }
    
    public static int getSize(String str) {
    	int result = 0;
    	try{
    		if(str == null) return 0;
    		result = str.getBytes("UTF-8").length;
    	}catch(Exception e){
    		result = 0;
    	}
    	return result;
    }
	
	/**
	 * int�� 4byte�� ���̳θ��� ��ȯ
	 * @param value
	 * @return
	 */
	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}
	
	/**
	 * ���̳θ��� int�� ��ȯ
	 * @param brr
	 * @param offset
	 * @return
	 */
	public static int ByteArrayToInt(byte[] brr, int offset) {
		int rtnInt = Integer.parseInt(toHexString(brr, offset, 4), 16);
		return rtnInt;
	}
	
	public static String toHexString(byte b[], int offset, int length)
    {
        StringBuffer buf = new StringBuffer();
        for(int i = offset; i < offset + length; i++)
        {
            int bi = 255 & b[i];
            int c = 48 + (bi / 16) % 16;
            if(c > 57)
                c = 65 + (c - 48 - 10);
            buf.append((char)c);
            c = 48 + bi % 16;
            if(c > 57)
                c = 65 + (c - 48 - 10);
            buf.append((char)c);
        }

        return buf.toString();
    }
	
	
	public static byte[] int2byteArr(int value, int len) {
		byte[] ret = new byte[len];
		
		for ( int i = 0 ; i < len ; ++i ) {
			ret[i] = (byte)((value >>> ((len - i - 1) * 8)) & 0xFF);
		}
		
		return ret;
	}
	
	
	public static String makePayload(String alert, String sound, int badge, HashMap<String, String> data) {
		String payload = "{\"aps\":{";
		boolean isPrev = false;
		
		if ( (alert != null) && (alert.length() > 0) ) {
			alert = setReplace(alert);
			payload += "\"alert\":\"" + alert + "\"";
			isPrev = true;
		}
		
		if ( (sound != null) && (sound.length() > 0) ) {
			if ( isPrev ) payload += ",";
			payload += "\"sound\":\"" + sound + "\"";
			isPrev = true;
		}
		
		if ( badge > 0 ) {
			if ( isPrev ) payload += ",";
			
			payload += "\"badge\":" + badge;
		}
		
		payload += "}";
		
		if ( (data != null) && data.size() > 0 ) {
			Set<String> keySet = data.keySet();
			Iterator<String> itr = keySet.iterator();
			String key = "";
			String value = "";
			
			while ( itr.hasNext() ) {
				key = itr.next();
				value = data.get(key);
				value = setReplace(value); // APNS Replace ó��
				payload += ",\"" + key + "\":\"" + value + "\"";
			}
		}
		
		payload += "}";
		
		//APNS �ٹٲ� ó��
		if(payload.indexOf("\r\n") > -1 ){
			payload = getReplace(payload, "\r\n", "\\r");
		}
		
		if(payload.indexOf("\n") > -1 ){
			payload = getReplace(payload, "\n", "\\r");
		} 
		
		if(payload.indexOf("\r") > -1 ){
			payload = getReplace(payload, "\r", "\\r");
		}
		
		
		return payload;
	}
	
	public static PushNotificationPayload makeJavaPnsPayload(String alert, String sound, int badge, HashMap<String, String> data) throws Exception {
		
		PushNotificationPayload payload = PushNotificationPayload.complex();
		
		if ( (data != null) && data.size() > 0 ) {
			Set<String> keySet = data.keySet();
			Iterator<String> itr = keySet.iterator();
			String key = "";
			String value = "";
			
			while ( itr.hasNext() ) {
				key = itr.next();
				value = data.get(key);
				value = setReplace(value); // APNS Replace ó��
				payload.addCustomDictionary(key, value);
			}
		}
		payload.addAlert(alert);
		payload.addSound(sound);
		payload.addBadge(badge);
		return payload;
	}
	
	public static void validateTokenFormat(String token) throws Exception {
		if (token == null) {
			throw new Exception(IConstants.APNS_INVALID_DEVTOKEN);
		}
		if (token.getBytes().length != 64)
			throw new Exception(IConstants.APNS_INVALID_DEVTOKEN);
	}

	
	/**
	 * @param value
	 * @return
	 */
	private static String setReplace(String value) {
		if(value.indexOf("\\") > -1 ){
			value = getReplace(value, "\\", "\\\\");
		}
		
		if(value.indexOf("\"") > -1 ){
			value = getReplace(value, "\"", "\\\"");
		}
		return value;
	}  
	
	
	/**
     * <pre>
     * </pre>
     *
     * @param str -
     * @param rep -
     * @param tok -
     *
     * @return String
     */
    public static String getReplace(String str, String rep, String tok) {
        String retStr = "";

        if (str == null || "".equals(str))
            return "";

        for (int i = 0, j = 0;(j = str.indexOf(rep, i)) > -1; i = j + rep.length()) {
            retStr += (str.substring(i, j) + tok);
        }
        return (str.indexOf(rep) == -1)
            ? str
            : retStr + str.substring(str.lastIndexOf(rep) + rep.length(), str.length());
    }
    
}
