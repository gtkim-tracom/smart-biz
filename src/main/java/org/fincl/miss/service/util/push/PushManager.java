/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push
 * @파일명          : PushManager.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util.push;

import org.fincl.miss.server.util.IConstants;
import org.fincl.miss.service.util.push.apns.APNSPushSender;
import org.fincl.miss.service.util.push.gcm.GCMPushSender;
import org.fincl.miss.service.util.push.message.APNSMessageBuilder;
import org.fincl.miss.service.util.push.message.GCMMessageBuilder;
import org.fincl.miss.service.util.push.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @파일명          : PushManager.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public class PushManager {
	private static PushManager manager;
	protected static Logger log = LoggerFactory.getLogger(PushManager.class);
	
	public static PushManager getInstance() {
        if(manager == null)
            manager = new PushManager();
        return manager;
    }
	
    public Send getSender(PushVO pushVo) {
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_APNS)) return APNSPushSender.getInstance();
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_GCM)) return GCMPushSender.getInstance();
		
		return null;		
	}	

	public MessageBuilder getBuilder(PushVO pushVo) {
		
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_APNS)) return APNSMessageBuilder.getInstance();
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_GCM)) return GCMMessageBuilder.getInstance();
		return null;		
	}
}
