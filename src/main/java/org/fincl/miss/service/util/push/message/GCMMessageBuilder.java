/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push.message
 * @파일명          : GCMMessageBuilder.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util.push.message;

import org.fincl.miss.server.util.StringUtil;
import org.fincl.miss.service.util.push.PushVO;

import com.google.android.gcm.server.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @파일명          : GCMMessageBuilder.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public class GCMMessageBuilder implements MessageBuilder {
	private volatile static GCMMessageBuilder instance = new GCMMessageBuilder();	
	protected static Logger log = LoggerFactory.getLogger(GCMMessageBuilder.class);
	private GCMMessageBuilder(){
		
	}
	
	public static GCMMessageBuilder getInstance() {
		return instance;
	}
	

	/**
	 * @location   : org.fincl.util.push.message.MessageBuilder.build
	 * @writeDay   : 2015. 8. 6. 오전 10:06:41
	 * @overridden : @see org.fincl.util.push.message.MessageBuilder#build(org.fincl.miss.server.push.PushVO)
	 * @Todo       :
	 */ 
	@Override
	public Object build(PushVO entity) throws Exception {
		//String collapseKey = "collapseKey";
		boolean delayWhileIdle = false;
		//int ttl = 0;
		String messageid = StringUtil.isNull(entity.getMessageId());
		String encryptyn = StringUtil.isNull(entity.getEncryptYn());
		String url     = StringUtil.isNull(entity.getPushLinkUrl());
		String message = StringUtil.isNull(entity.getMessage());
		
		Message payload = new Message.Builder()
		                .collapseKey(String.valueOf(Math.random() % 100 + 1))
				        .delayWhileIdle(delayWhileIdle)
				        .timeToLive(1300)
				        .addData("messageid", messageid)
				        .addData("encryptyn", encryptyn)
				        .addData("url", url)
				        .addData("msg", message)
				        .build();
		 return payload;
	}

}
