package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-24 18:22
 */
public class OriginateEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(OriginateEventMessage.class);
    public OriginateEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public void handler(EslEvent eslEvent) {
        String myuuid=eslEvent.getEventHeaders().get("variable_uuid");
        logger.info("originate 事件 uuid-> {}",myuuid);
        ChannelStatusManager.cancelTransTimeout(myuuid);
    }
}
