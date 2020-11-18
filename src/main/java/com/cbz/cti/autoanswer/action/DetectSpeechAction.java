package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.AsrNoInputTimeout;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import ai.cbz.inbound.common.response.DialogDetectSpeechParams;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-21 16:32
 */
public class DetectSpeechAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(DetectSpeechAction.class);
    BaseEventMessage eventMessage;
    boolean isSetTimer;
    public DetectSpeechAction(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy proxy, DialogActionBean actionBean, BaseEventMessage eventMessage, boolean isSetTimer) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
        this.eventMessage=eventMessage;
        this.isSetTimer=isSetTimer;
    }

    @Override
    public void executeAction() {
        DialogDetectSpeechParams detectSpeechParams = (DialogDetectSpeechParams) actionBean.getAction().getParams();
        int asrModeID = detectSpeechParams.getAsrModeID();
        int timeout = detectSpeechParams.getTimeout()*1000;
        logger.info("检测用户输入 模型Id ->{};超时->{}",asrModeID,timeout);
        ChannelStatusManager.getChannelStatus(actionBean.getCallId()).setInputType(ChannelStatusTypeBean.VOICE_INPUT);

        //启动超时器
        if(isSetTimer){
            AsrNoInputTimeout noInputTimeout=new AsrNoInputTimeout(applicationComponent,eventMessage,actionBean.getCallId());
            Timeout timeout1= WheelTimerUtils.submitTask(noInputTimeout,timeout);
            ChannelStatusManager.addAsrTimeout(actionBean.getCallId(),timeout1);//增加超时
        }
        //保存asrModeID
        ChannelStatusManager.getChannelStatus(actionBean.getCallId()).setAsrModeId(asrModeID);
        proxy.sendDetectSpeech(actionBean.getCallId(),
                applicationConfig.getAsrModuleName(),
                timeout,
                Integer.parseInt(applicationConfig.getStartDur()),
                Integer.parseInt(applicationConfig.getEndDur()),
                Integer.parseInt(applicationConfig.getThreshold()),
                asrModeID);
    }
}
