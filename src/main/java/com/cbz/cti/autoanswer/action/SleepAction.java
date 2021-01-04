package com.cbz.cti.autoanswer.action;

import ai.cbz.inbound.common.response.DialogPlayFileParams;
import ai.cbz.inbound.common.response.DialogWaitParams;
import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.timer.HangupTask;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: Jinzw
 * @Date: 2021/1/4 10:37
 */
public class SleepAction extends BaseAction{
    private static final Logger logger = LoggerFactory.getLogger(HangupAction.class);

    public SleepAction(ApplicationConfig applicationConfig,
                        ApplicationComponent applicationComponent,
                        ClientProxy proxy,
                        DialogActionBean actionBean,
                        BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
    }

    @Override
    public void executeAction() {
        DialogWaitParams dialogWaitParams = (DialogWaitParams) actionBean.getAction().getParams();
        Integer timeout = dialogWaitParams.getTimeout();
        logger.info("等待操作 时间:{}ms", timeout);
        getProxy().sendSleepCommand(actionBean.getCallId(), timeout.toString());
    }
}
