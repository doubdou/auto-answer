package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import ai.cbz.inbound.common.enums.DialogRequestEnum;
import ai.cbz.inbound.common.request.DialogManageRequest;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-13 14:08
 */
public class AnswerEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(AnswerEventMessage.class);
    public AnswerEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }
    @Override
    public void handler(EslEvent eslEvent) {
        String callId=getCallId(eslEvent.getEventHeaders());
        String callee=getCallee(eslEvent.getEventHeaders());
        String caller=getCaller(eslEvent.getEventHeaders());
        //判断是转人工的answer还是正常的answer
        String myuuid=eslEvent.getEventHeaders().get("variable_uuid");
        if(myuuid.equals(callId)){
            //开始录音
            clientProxy.record(callId,applicationConfig.getRecordPath()+callId+".wav");

            logger.info("开始录音 callId:{}",callId);
            DialogManageRequest manageRequest = new DialogManageRequest();
            manageRequest.setChatId(callId);
            manageRequest.setClientId(caller);
            manageRequest.setCallee(callee);
            manageRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_START);
            DialogManageResponse manageResponse=applicationComponent.getDialogService().dialogManage(manageRequest);
            DialogData dialogData = parseActions(manageResponse);
            if(dialogData!=null){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executeActions(dialogData,dialogData.getActions(),callId);
            }
        }else {
            logger.info("转人工，人工客服的answer 事件");
            clientProxy.stopRecord(callId,applicationConfig.getRecordPath()+callId+".wav");
            //发送mq消息
            transferAnswer(callId,2,callee);
            //停止录音
        }
    }
    private void transferAnswer(String callId,int status,String sipName){
        logger.info("transferAnswer 转人工假装成功。");
//        TurnArtificialDTO artificialDTO=new TurnArtificialDTO();
//        artificialDTO.setContextId(callId);
//        artificialDTO.setStatus(status);
//        artificialDTO.setTurnArtificialChannelName(sipName);
//        applicationComponent.getMqUtils().sendMsg(artificialDTO);
    }
}
