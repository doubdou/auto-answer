package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.HangupTask;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-21 16:09
 */
public class HangupAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(HangupAction.class);

    public HangupAction(ApplicationConfig applicationConfig,
                        ApplicationComponent applicationComponent,
                        ClientProxy proxy,
                        DialogActionBean actionBean,
                        BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
    }

    @Override
    public void executeAction() {
        logger.info("挂断操作");
        ChannelStatusManager.getChannelStatus(actionBean.getCallId()).setInputType(ChannelStatusTypeBean.FPRBID_INPUT);
        WheelTimerUtils.submitTask(new HangupTask(proxy,actionBean.getCallId()),0);
    }
}
