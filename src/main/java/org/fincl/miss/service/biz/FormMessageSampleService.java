package org.fincl.miss.service.biz;

import org.springframework.stereotype.Service;

@Service("formMessageSampleService")
public class FormMessageSampleService {
    
    public FormMessageSampleVo test(FormMessageSampleVo Vo) {
        
        // HttpHeaderVo fd = Vo.getHeaderVo();
        // System.out.println("vo::" + Vo);
        // System.out.println("fd::" + fd);
        
        // Map m = Vo.getHeaderMap();
        // System.out.println("m::" + m);
        
        FormMessageSampleVo aa = new FormMessageSampleVo();
        
        return Vo;
    }
}
