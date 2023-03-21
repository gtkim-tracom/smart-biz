package org.fincl.miss.service.biz.bicycle.vo;

import org.fincl.miss.server.message.MessageVO;

public class BicycleVo extends MessageVO {
    
    private static final long serialVersionUID = 2168387915038521022L;
    
    private String frameControl;
    private String seqNum;
    
    private String commandId;
    
    public String getFrameControl() {
        return frameControl;
    }
    
    public void setFrameControl(String frameControl) {
        this.frameControl = frameControl;
    }
    
    public String getSeqNum() {
        return seqNum;
    }
    
    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    
    public String getCommandId() {
        return commandId;
    }
    
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }
}
