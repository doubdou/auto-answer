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
 * 说话开始事件事件
 * @author jinzw
 * @date 2020-10-13 14:08
 */
public class PlayStartEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(PlayStartEventMessage.class);
    public PlayStartEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public void handler(EslEvent eslEvent) {
        //音频播放开始事件
        String callId=getCallId(eslEvent.getEventHeaders());
        logger.info("------------------------播放开始 callId -> {} --------------------------",callId);
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        statusBean.setDtmfBreakTag(ChannelStatusTypeBean.DTMF_BREAK_NOT_REQ);
        statusBean.setPlayStatus(ChannelStatusTypeBean.PORCESS_PLAY);
        //播放开始
        try {
            int startTime=(int) (System.currentTimeMillis()-statusBean.getRecordStartTime()-100);
            if(startTime<0) {
                startTime = 0;
            }
        }catch (Exception e){
            logger.info("error  {}",e.getMessage());
        }
    }
}
