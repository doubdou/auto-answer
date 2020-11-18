package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
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
 *  电话呼入事件
 * @author jinzw
 * @date 2020-10-13 13:58
 */
public class InComingEventMessage extends BaseEventMessage{
    private static final Logger logger = LoggerFactory.getLogger(InComingEventMessage.class);
    public InComingEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }
    @Override
    public void handler(EslEvent eslEvent) {
        //电话呼入处理
        String status=eslEvent.getEventHeaders().get("Answer-State");
        String callDirection=eslEvent.getEventHeaders().get("Caller-Direction");
        logger.info("status->{};callDirection->{}",status,callDirection);
        if(status.equals("ringing")&&callDirection.equals("inbound")){
            //电话呼入事件，//调用DM
            String callee=getCallee(eslEvent.getEventHeaders());
            String caller=getCaller(eslEvent.getEventHeaders());
            String callId=getCallId(eslEvent.getEventHeaders());
            //保存状态
            ChannelStatusBean statusBean=new ChannelStatusBean();
            statusBean.setActionBean(null);
            statusBean.setPlayStatus(ChannelStatusTypeBean.NOT_PLAY);
            statusBean.setCurrentApp("");
            statusBean.setCallId(callId);
            statusBean.setSupportBreak(ChannelStatusTypeBean.NOT_BREAK);
            ChannelStatusManager.addChannelStatus(callId,statusBean);
            logger.info("主叫-> {};被叫->{};callId->{}",caller,callee,callId);
            DialogManageRequest dialogManageRequest=new DialogManageRequest();
            dialogManageRequest.setReqType(DialogRequestEnum.CC_DM_CHAT_CREATE);
            dialogManageRequest.setCallee(callee);
            dialogManageRequest.setClientId(caller);
            dialogManageRequest.setChatId(callId);
            //应答事件
            DialogManageResponse manageResponse = applicationComponent.getDialogService().dialogManage(dialogManageRequest);
            DialogData dialogData=parseActions(manageResponse);
            if(dialogData!=null){
                executeActions(dialogData,dialogData.getActions(),callId);
            } else {
                logger.info("DEBUG dialogData is null.");
            }
        }
    }
}
