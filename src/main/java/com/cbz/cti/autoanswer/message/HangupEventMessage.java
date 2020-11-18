package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.HangupTask;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import ai.cbz.inbound.common.enums.DialogRequestEnum;
import ai.cbz.inbound.common.request.DialogManageRequest;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import com.alibaba.fastjson.JSONObject;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 挂断事件处理
 * @author jinzw
 * @date 2020-10-13 14:08
 */
public class HangupEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(HangupEventMessage.class);
    public HangupEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public void handler(EslEvent eslEvent) {
        //挂断事件//发送mq事件
        String callId=getCallId(eslEvent.getEventHeaders());
        String caller=getCaller(eslEvent.getEventHeaders());
        String callee=getCallee(eslEvent.getEventHeaders());
        //判断是否是转接挂断
        String myuuid=eslEvent.getEventHeaders().get("variable_uuid");
        //请求挂断事件
        DialogManageRequest manageRequest=new DialogManageRequest();
        if(!myuuid.equals(callId)){
            String hangupCause=eslEvent.getEventHeaders().get("Hangup-Cause");
            if(!hangupCause.equals("NORMAL_CLEARING")){
                //更改状态,未播放状态
                ChannelStatusManager.getChannelStatus(callId).setSupportBreak(ChannelStatusTypeBean.NOT_BREAK);
                logger.info("转接挂断 人工客服无响应 myuuid -> {};挂断原因-> {}",myuuid,hangupCause);
                manageRequest.setReqType(DialogRequestEnum.CC_DM_CHAT_ACTION_FAILED);
                manageRequest.setChatId(callId);
                DialogManageResponse dmResp=applicationComponent.getDialogService().dialogManage(manageRequest);
                DialogData dialogData=parseActions(dmResp);
                if(dialogData!=null){
                    executeActions(dialogData,dialogData.getActions(),callId);
                }
            }else {
                logger.info("人工转接过后，人工方正常挂断");
                WheelTimerUtils.submitTask(new HangupTask(clientProxy,callId),2000);
            }
        }else {
            manageRequest.setChatId(callId);
            manageRequest.setClientId(caller);
            manageRequest.setCallee(callee);
            manageRequest.setReqType(DialogRequestEnum.CC_DM_CHAT_END);
            DialogManageResponse dialogManageResponse=applicationComponent.getDialogService().dialogManage(manageRequest);
            DialogData dialogData=parseActions(dialogManageResponse);
            logger.info("--------正常挂机-------");
            logger.info("{}", JSONObject.toJSONString(dialogData));
            sendMqLastDialog(callId,dialogData);
            //清除缓存
            ChannelStatusManager.clearChannelCache(callId);
        }
    }
    //发送最后的消息对话
    private void sendMqLastDialog(String callId, DialogData dialogData) {
    }



}
