package org.fincl;

import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tempService")
public class TempService {
    @Autowired
    ChannelManagerImpl channerManager;
    
    public TempService() {
        
    }
}
