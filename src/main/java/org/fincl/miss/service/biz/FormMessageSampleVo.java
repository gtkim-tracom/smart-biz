package org.fincl.miss.service.biz;

import org.fincl.miss.server.message.MessageVO;

public class FormMessageSampleVo extends MessageVO {
    
    private String sn;
    private String cn;
    private String[] locale;
    private String caller;
    private String num;
    
    public String getSn() {
        return sn;
    }
    
    public void setSn(String sn) {
        this.sn = sn;
    }
    
    public String getCn() {
        return cn;
    }
    
    public void setCn(String cn) {
        this.cn = cn;
    }
    
    public String[] getLocale() {
        return locale;
    }
    
    public void setLocale(String[] locale) {
        this.locale = locale;
    }
    
    public String getCaller() {
        return caller;
    }
    
    public void setCaller(String caller) {
        this.caller = caller;
    }
    
    public String getNum() {
        return num;
    }
    
    public void setNum(String num) {
        this.num = num;
    }
}
