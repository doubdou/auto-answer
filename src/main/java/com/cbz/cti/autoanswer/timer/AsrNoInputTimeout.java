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
 * asr 输入超时 暂时不用
 * @author jinzw
 * @date 2020-10-29 15:40
 */
public class AsrNoInputTimeout implements TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(AsrNoInputTimeout.class);
    ApplicationComponent component;
    BaseEventMessage baseEventMessage;
    String callId;
    public AsrNoInputTimeout(ApplicationComponent component, BaseEventMessage eventMessage, String callId) {
        this.component = component;
        this.baseEventMessage=eventMessage;
        this.callId=callId;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
            //发送DM超时事件
            logger.info("-------------------asr 输入超时-------------------");
            DialogManageRequest dmRequest=new DialogManageRequest();
            dmRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_TIMEOUT);
            dmRequest.setChatId(callId);
            DialogManageResponse dmReponse=component.getDialogService().dialogManage(dmRequest);
            DialogData dialogData=baseEventMessage.parseActions(dmReponse);
            baseEventMessage.executeActions(dialogData,dialogData.getActions(),callId);
        }
    }
