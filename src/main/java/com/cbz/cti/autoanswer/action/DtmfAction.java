package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.bean.DtmfActionDataBean;
import ai.cbz.inbound.common.response.DialogGetDTMFParams;
import com.alibaba.fastjson.JSONObject;
//import com.cbz.cti.autoanswer.bean.*;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.DtmfTask;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-29 15:15
 */
public class DtmfAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(DtmfAction.class);
    BaseEventMessage baseEventMessage;
    private boolean isSetTimer=true;
    public DtmfAction(ApplicationConfig applicationConfig,
                      ApplicationComponent applicationComponent,
                      ClientProxy proxy,
                      DialogActionBean actionBean,
                      BaseEventMessage eventMessage,
                      boolean isSetTimer) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
        this.baseEventMessage=eventMessage;
        this.isSetTimer=isSetTimer;
    }

    @Override
    public void executeAction() {
        //设置目前的接受输入的方式
        ChannelStatusBean statusBean=ChannelStatusManager.getChannelStatus(actionBean.getCallId());
        statusBean.setInputType(ChannelStatusTypeBean.DTMF_INPUT);
        //设置超时
        DialogGetDTMFParams dtmfParams = (DialogGetDTMFParams) actionBean.getAction().getParams();
        statusBean.setDtmfTimeout(dtmfParams.getTimeout()*1000);
        //判断是否设置定时器，如果有dtmf打断或语音打断，则检测dtmf动作会被提取释放
        if(isSetTimer){
            DtmfTask task=new DtmfTask(applicationComponent,baseEventMessage,actionBean.getCallId());
            Timeout timeout=WheelTimerUtils.submitTask(task,dtmfParams.getTimeout()*1000);
            logger.info("执行接受dtmf动作，设置定时器，超时时间为 -> {} s",dtmfParams.getTimeout());
            ChannelStatusManager.addDtmfTimeout(actionBean.getCallId(),timeout);
        }


        //保存参数
        DtmfActionDataBean actionDataBean=ChannelStatusManager.getDtmfActionData(actionBean.getCallId());
        if(actionDataBean==null){
            actionDataBean=new DtmfActionDataBean();
        }
        logger.info("dtmf 属性 --> {}", JSONObject.toJSONString(dtmfParams));
        actionDataBean.setDtmfEndTag(dtmfParams.getEndTag());
        actionDataBean.setTimeout(dtmfParams.getTimeout());
        actionDataBean.setDtmfLength(dtmfParams.getLength());
//        actionDataBean.setCurrDtmf("");
        ChannelStatusManager.addDtmfActionData(actionBean.getCallId(),actionDataBean);
    }
}
