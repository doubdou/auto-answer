package com.cbz.cti.autoanswer.timer;

import ai.cbz.inbound.common.enums.DialogRequestEnum;
import ai.cbz.inbound.common.request.DialogManageRequest;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 定时执行
 * @author jinzw
 * @date 2020-10-27 17:15
 */
public class BreakTask implements TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(BreakTask.class);
    String callId;
    ApplicationComponent component;
    BaseEventMessage eventMessage;

    public BreakTask(String callId, ApplicationComponent component, BaseEventMessage eventMessage) {
        this.callId = callId;
        this.component = component;
        this.eventMessage = eventMessage;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        logger.info("说话时间超过1.5s 发送打断操作");
        DialogManageRequest manageRequest=new DialogManageRequest();
        manageRequest.setChatId(callId);
        manageRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_START);
        DialogManageResponse manageResponse=component.getDialogService().dialogManage(manageRequest);
        DialogData dialogData=eventMessage.parseActions(manageResponse);
        if(dialogData!=null){
            eventMessage.executeActions(dialogData,dialogData.getActions(),callId);
        }
    }
}
