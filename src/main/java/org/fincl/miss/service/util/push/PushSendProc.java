/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push
 * @파일명          : PushSendProc.java
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

/**
 * @파일명          : PushSendProc.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public class PushSendProc {
	public static void exePush(PushVO pushVo) throws Exception {
		
		PushManager manager =  PushManager.getInstance();
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_GCM)) {
			GCMPushSender sender =  (GCMPushSender)manager.getSender(pushVo);
			GCMMessageBuilder msgBuilder =(GCMMessageBuilder)manager.getBuilder(pushVo);
			
			Object msg;
			try {
				msg = msgBuilder.build(pushVo);
				sender.send(pushVo, msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
			APNSPushSender sender = (APNSPushSender)manager.getSender(pushVo);
			APNSMessageBuilder msgBuilder = (APNSMessageBuilder)manager.getBuilder(pushVo);
			
			Object msg;
			try {
				msg = msgBuilder.build(pushVo);
				sender.send(pushVo, msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
