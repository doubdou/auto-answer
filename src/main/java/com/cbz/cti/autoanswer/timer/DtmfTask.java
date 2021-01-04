package com.cbz.cti.autoanswer.timer;

import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
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
 *
 * @author jinzw
 * @date 2020-10-29 15:40
 */
public class DtmfTask implements TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(DtmfTask.class);
    ApplicationComponent component;
    BaseEventMessage baseEventMessage;
    String callId;
    public DtmfTask(ApplicationComponent component,BaseEventMessage eventMessage,String callId) {
        this.component = component;
        this.baseEventMessage=eventMessage;
        this.callId=callId;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        //发送mq事件
        String dtmf= ChannelStatusManager.getDtmfActionData(callId).getCurrDtmf();
        logger.info("检测dtmf 超时 ->  {}",dtmf);

       //发送DM超时事件
        DialogManageRequest dmRequest=new DialogManageRequest();
        dmRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_TIMEOUT);
        dmRequest.setText(dtmf);
        dmRequest.setChatId(callId);
        DialogManageResponse dmReponse=component.getDialogService().dialogManage(dmRequest);
        DialogData dialogData=baseEventMessage.parseActions(dmReponse);
        baseEventMessage.executeActions(dialogData,dialogData.getActions(),callId);
    }
}
