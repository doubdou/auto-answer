package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.constant.FsApplicationName;
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
 *  事件执行完成
 * @author jinzw
 * @date 2020-10-22 17:04
 */
public class AppCompleteEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(PlayStopEventMessage.class);
    public AppCompleteEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public synchronized void handler(EslEvent eslEvent) {
        String application=eslEvent.getEventHeaders().get("Application");
        String callId=getCallId(eslEvent.getEventHeaders());
        String callee=getCallee(eslEvent.getEventHeaders());
        logger.info("事件执行完成, application:{},callId:{} ",application,callId);
        //继续执行下个app
        ChannelStatusBean statusBean = ChannelStatusManager.getChannelStatus(getCallId(eslEvent.getEventHeaders()));
        logger.info("get status Bean:{}", statusBean.toString());
        String preApp=statusBean.getCurrentApp();
        logger.info("get currentApp: {}", preApp);
        if(application.equals(FsApplicationName.BREAK)){
            logger.info("---------------------------打断完成--------------------------");
            //特殊处理 break事件
            //发送mq事件
            statusBean.setPlayStatus(ChannelStatusTypeBean.NOT_PLAY);//设置目前播放状态，未在播放
        }else if(application.equals(FsApplicationName.TRANER)){
            logger.info("转人工返回");
        } else {
            if(application.equals(FsApplicationName.SPEAK)||application.equals(FsApplicationName.PLAYBACK)){
                logger.info("标记播放完成");
                statusBean.setPlayStatus(ChannelStatusTypeBean.NOT_PLAY);
            }
            if(application.equals(FsApplicationName.SPEAK)){
                logger.info("tts合成完成");
                statusBean.setPlayStatus(ChannelStatusTypeBean.NOT_PLAY);
            }
            //排除answer，因为对话开始的时候聊天CC_DM_CHAT_CREATE 会发送answer事件
            if(application.equals(FsApplicationName.PLAYBACK)||application.equals(FsApplicationName.SPEAK)|| application.equals(FsApplicationName.SLEEP)){
                if(statusBean.getActionBean()==null||statusBean.getActionBean().size()==0){
                    logger.info("节点执行完成，发送DM 节点执行完成");
                    DialogManageRequest dmRequest=new DialogManageRequest();
                    dmRequest.setChatId(callId);
                    dmRequest.setCallee(callee);
                    dmRequest.setReqType(DialogRequestEnum.CC_DM_CHAT_ACTION_SUCCESS);
                    DialogManageResponse dmResponse=applicationComponent.getDialogService().dialogManage(dmRequest);
                    DialogData dialogData=parseActions(dmResponse);
                    if(dialogData!=null){
                        executeActions(dialogData,dialogData.getActions(),callId);
                    }
                }else {
                    if(preApp.equals(application)){
                        logger.info("继续执行下一个app");
                        executeActions(statusBean.getDialogData(),statusBean.getActionBean(),getCallId(eslEvent.getEventHeaders()));
                    }
                }
            }
        }
    }
}
