package com.cbz.cti.autoanswer.timer;

import ai.cbz.inbound.common.enums.DialogRequestEnum;
import ai.cbz.inbound.common.request.DialogManageRequest;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * TODO
 * 转人工定时处理
 * @author jinzw
 * @date 2020-10-24 18:09
 */
public class TransTask implements TimerTask {
    String callId;
    ApplicationComponent applicationComponent;
    BaseEventMessage eventMessage;
    String myuuid;
    public TransTask(String callId, ApplicationComponent applicationComponent, BaseEventMessage eventMessage, String myuuid) {
        this.callId = callId;
        this.applicationComponent = applicationComponent;
        this.eventMessage=eventMessage;
        this.myuuid=myuuid;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        //调用转人工失败
        DialogManageRequest dmRequest=new DialogManageRequest();
        dmRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_ACTION_FAILED);
        dmRequest.setChatId(callId);
        DialogManageResponse dmResponse=applicationComponent.getDialogService().dialogManage(dmRequest);
        DialogData dialogData=eventMessage.parseActions(dmResponse);
        if(dialogData!=null){
           eventMessage.executeActions(dialogData,dialogData.getActions(),callId);
        }
        ChannelStatusManager.removeDtmfTimeout(myuuid);
    }
}
