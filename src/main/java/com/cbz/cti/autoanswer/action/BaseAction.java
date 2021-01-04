package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import ai.cbz.inbound.common.enums.DialogActionTypeEnum;
import ai.cbz.inbound.common.response.*;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 动作执行action
 * @author jinzw
 * @date 2020-10-21 16:08
 */
public abstract class BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);
    ClientProxy proxy;
    DialogActionBean actionBean;
    ApplicationConfig applicationConfig;
    ApplicationComponent applicationComponent;
    BaseEventMessage eventMessage;

    public BaseAction(ApplicationConfig applicationConfig,
                      ApplicationComponent applicationComponent,
                      ClientProxy proxy,
                      DialogActionBean actionBean,
                      BaseEventMessage baseEventMessage){
        this.proxy=proxy;
        this.actionBean=actionBean;
        this.applicationConfig=applicationConfig;
        this.applicationComponent=applicationComponent;
        this.eventMessage=baseEventMessage;
    }

    public ClientProxy getProxy() {
        return proxy;
    }

    public abstract void executeAction();

    /**
     * 打断动作检测
     */
    //打断动作检测
    public void breakActionCheck(ChannelStatusBean statusBean){
        int breakType=statusBean.getSupportBreak();
        if(breakType== ChannelStatusTypeBean.DTMF_VOICE_BREAK){
            //dtmf 打断，动作链里面是否有dtmf输入
            for(DialogAction action:statusBean.getActionBean()){
                DialogActionBean actionBean=new DialogActionBean();
                actionBean.setAction(action);
                actionBean.setCallId(statusBean.getCallId());
                if(action.getAction()==DialogActionTypeEnum.DM_AA_CHAT_GET_DTMF){
                    //启动dtmf
                    logger.info("检测到需要提前启动dtmf");
                    DtmfAction dtmfAction=new DtmfAction(applicationConfig,applicationComponent,proxy,actionBean,eventMessage,false);
                    dtmfAction.executeAction();
                }else if(action.getAction()==DialogActionTypeEnum.DM_AA_CHAT_DETECT_SPEECH){
                    //启动detechspeech
                    logger.info("检测到需要提前启动detechspeech");
                    DetectSpeechAction detectSpeechAction=new DetectSpeechAction(applicationConfig,applicationComponent,proxy,actionBean,eventMessage,false);
                    detectSpeechAction.executeAction();
                }
            }
        }else if(breakType==ChannelStatusTypeBean.DTMF_BREAK){
            //dtmf打断
            for(DialogAction action:statusBean.getActionBean()){
                if(action.getAction()== DialogActionTypeEnum.DM_AA_CHAT_GET_DTMF){
                    DialogActionBean actionBean=new DialogActionBean();
                    actionBean.setAction(action);
                    actionBean.setCallId(statusBean.getCallId());
                    //启动dtmf
                    logger.info("检测到需要提前启动dtmf");
                    DtmfAction dtmfAction=new DtmfAction(applicationConfig,applicationComponent,proxy,actionBean,eventMessage,false);
                    dtmfAction.executeAction();
                }
            }
        }else if(breakType==ChannelStatusTypeBean.VOICE_BREAK){
            //语音打断
            //dtmf 打断，动作链里面是否有dtmf输入
            for(DialogAction action:statusBean.getActionBean()){
                if(action.getAction()==DialogActionTypeEnum.DM_AA_CHAT_DETECT_SPEECH){
                    DialogActionBean actionBean=new DialogActionBean();
                    actionBean.setAction(action);
                    actionBean.setCallId(statusBean.getCallId());
                    //启动detechspeech
                    logger.info("检测到需要提前启动detechspeech");
                    DetectSpeechAction detectSpeechAction=new DetectSpeechAction(applicationConfig,applicationComponent,proxy,actionBean,eventMessage,false);
                    detectSpeechAction.executeAction();
                }
            }
        }
    }

}
