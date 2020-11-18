package com.cbz.cti.autoanswer;

import com.cbz.cti.autoanswer.esl.FsClient;
import com.cbz.cti.autoanswer.listener.EventListener;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *初始化bean类
 * @author jinzw
 * @date 2020-10-10 17:13
 */
@Configuration
public class ApplicationInitBean {
    @Autowired
    EslConfig eslConfig;

    @Autowired
    EventListener eventListener;

    @Bean
    public FsClient getFsClient() throws InboundConnectionFailure {
        FsClient fsClient=new FsClient(eslConfig,eventListener);
        return fsClient;
    }

}
