package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.AsrNoInputTimeout;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import ai.cbz.inbound.common.enums.DialogRequestEnum;
import ai.cbz.inbound.common.request.DialogManageRequest;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import io.netty.util.Timeout;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-13 14:08
 */
public class SpeechEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(SpeechEventMessage.class);
    public SpeechEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public void handler(EslEvent eslEvent){
        //获取识别结果
        String callId=getCallId(eslEvent.getEventHeaders());
        String callee=getCallee(eslEvent.getEventHeaders());
        String caller=getCaller(eslEvent.getEventHeaders());
        String asrResult=eslEvent.getEventHeaders().get("ASR-Result");
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        statusBean.setSpeechStatus(ChannelStatusTypeBean.NO_VOICE);
        logger.info("语音识别结果 -> {}; 目前的支持输入方式为-> {},callId -> {}",asrResult,statusBean.getInputType(),callId);
        if((statusBean.getSupportBreak()== ChannelStatusTypeBean.NOT_BREAK
                ||statusBean.getSupportBreak()==ChannelStatusTypeBean.DTMF_BREAK)
                &&statusBean.getPlayStatus()==ChannelStatusTypeBean.PORCESS_PLAY){
            logger.info("不支持打断,或者dtmf打断，且在播放中，表示用户在ai播放话术期间说话完成");
        }else if(statusBean.getInputType()==ChannelStatusTypeBean.FPRBID_INPUT) {
            logger.info("禁止接受任何输入方式");
        }else if(statusBean.getInputType()==ChannelStatusTypeBean.VOICE_INPUT){
            logger.info("语音输入方式");
            //请求AI结果
            if(statusBean.getSpeechStatus()==ChannelStatusTypeBean.BEAGIN_VOICE){
                logger.info("识别结果返回，但是用户又说话了");//取消本次发送ai
                //保存用户上次说的话
            }else {
                if(asrResult!=null&&!asrResult.isEmpty()){
                    if(asrResult.length()==1&&!applicationConfig.filterWord().contains(asrResult)){
                        logger.info("asr 识别结果不包含在单个词中");
                    }else {
                        logger.info("请求DM 获取响应");
                        statusBean.setAsrSpeechNullNum(0);
                        DialogManageRequest manageRequest=new DialogManageRequest();
                        manageRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_TEXT);
                        manageRequest.setCallee(callee);
                        manageRequest.setClientId(caller);
                        manageRequest.setChatId(callId);
                        String preText="";
                        if(preText != null){
                            manageRequest.setText(preText+asrResult);
                        }else {
                            manageRequest.setText(asrResult);
                        }
                        DialogManageResponse manageResponse = applicationComponent.getDialogService().dialogManage(manageRequest);
                        DialogData dialogData=parseActions(manageResponse);
                        if(dialogData!=null){
                            executeActions(dialogData,dialogData.getActions(),callId);
                        }
                    }
                } else {
                        statusBean.setAsrSpeechNullNum(statusBean.getAsrSpeechNullNum()+1);
                        //查看是否连续2次识别为空的情况
                        long delay=4000;
                        if(statusBean.getAsrSpeechNullNum()==1){
                            delay=4000;
                        }else if(statusBean.getAsrSpeechNullNum()==2){
                            delay=3000;
                        }
                        if(statusBean.getAsrSpeechNullNum()>=3){
                            logger.info("连续3次识别为空，启动超时");
                            delay=10;
                        }
                        //启动超时器
                        AsrNoInputTimeout noInputTimeout=new AsrNoInputTimeout(applicationComponent,this,callId);
                        Timeout timeout1= WheelTimerUtils.submitTask(noInputTimeout,delay);
                        ChannelStatusManager.addAsrTimeout(callId,timeout1);//增加超时
                    }
                }
            }
    }
}
