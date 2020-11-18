package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 说话结束事件
 * @author jinzw
 * @date 2020-10-13 14:08
 */
public class PlayStopEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(PlayStopEventMessage.class);
    public PlayStopEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public void handler(EslEvent eslEvent) {
        //音频播放结束事件
        String callId=getCallId(eslEvent.getEventHeaders());
        logger.info("------------------------播放结束 callId -> {} -------------------------- ",callId);
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        statusBean.setPlayStatus(ChannelStatusTypeBean.NOT_PLAY);//设置目前播放状态false
    }
}
