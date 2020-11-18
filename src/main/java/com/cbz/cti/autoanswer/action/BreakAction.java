package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 打断操作
 * @author jinzw
 * @date 2020-10-24 9:32
 */
public class BreakAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(BreakAction.class);
    public BreakAction(ApplicationConfig applicationConfig,
                       ApplicationComponent applicationComponent,
                       ClientProxy proxy,
                       DialogActionBean actionBean,
                       BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
    }

    @Override
    public void executeAction() {
        //判断此时是否正在播放
        String callId=actionBean.getCallId();
        //
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        if(statusBean.getPlayStatus()== ChannelStatusTypeBean.PORCESS_PLAY){
            //播放状态，暂停播放
            logger.info("此时正在播放,将停止播放 callId -> {}",callId);
            proxy.breakPlay(callId);
        }
    }
}
