package com.cbz.cti.autoanswer.listener;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.constant.EslEventName;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.message.*;
import org.freeswitch.esl.client.inbound.IEslEventListener;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.internal.Context;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-10 18:03
 */
@Service
public class EventListener implements IEslEventListener {
    private static final Logger logger = LoggerFactory.getLogger(EventListener.class);
    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    ApplicationComponent applicationComponent;

    ClientProxy clientProxy;

    public void setClientProxy(ClientProxy clientProxy) {
        this.clientProxy = clientProxy;
    }

    //事件
    @Override
    public void onEslEvent(Context context, EslEvent eslEvent) {
        String eslName= eslEvent.getEventName();
        logger.info("event name -> {}",eslName);
        BaseEventMessage baseEventMessage = null;
        switch (eslName){
            case EslEventName.CHANNEL_CREATE:
                baseEventMessage=new InComingEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.CHANNEL_ANSWER:
                baseEventMessage=new AnswerEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.CHANNEL_HANGUP_COMPLETE:
                baseEventMessage=new HangupEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.DETECTED_SPEECH:
                baseEventMessage=new SpeechEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.PLAYBACK_START:
                baseEventMessage=new PlayStartEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.PLAYBACK_STOP:
                baseEventMessage=new PlayStopEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.CHANNEL_EXECUTE_COMPLETE:
                baseEventMessage=new AppCompleteEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.CHANNEL_ORIGINATE:
                baseEventMessage=new OriginateEventMessage(applicationConfig,applicationComponent,clientProxy);
                break;
            case EslEventName.DTMF:
                baseEventMessage=new DtmfEventMessage(applicationConfig,applicationComponent,clientProxy);
            default:
                break;
        }
        if(baseEventMessage!=null){
            logger.info("进行消息处理");
            baseEventMessage.handler(eslEvent);
        }
    }
    //onClose，掉线
    public void onClose() {
        while (true){
            try {
                clientProxy.reconnect();
                break;
            } catch (InboundConnectionFailure inboundConnectionFailure) {
                logger.info("重连失败");
            }
        }
    }
}
