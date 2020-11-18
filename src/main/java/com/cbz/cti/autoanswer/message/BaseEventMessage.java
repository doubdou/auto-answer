package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.action.*;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import ai.cbz.inbound.common.enums.DialogActionTypeEnum;
import ai.cbz.inbound.common.response.DialogAction;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogManageResponse;
import com.cbz.cti.autoanswer.action.*;
import lombok.Getter;
import lombok.Setter;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * TODO
 * esl 消息基类
 * @author jinzw
 * @date 2020-10-13 11:52
 */
@Getter
@Setter
public abstract class BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(BaseEventMessage.class);
    ApplicationConfig applicationConfig;
    ApplicationComponent applicationComponent;
    ClientProxy clientProxy;
    public BaseEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        this.applicationConfig = applicationConfig;
        this.applicationComponent = applicationComponent;
        this.clientProxy = clientProxy;
    }

    public abstract void handler(EslEvent eslEvent);

    /**
     * 获取呼叫id
     * @param header sip 头
     * @return callId
     */
    public String getCallId(Map<String,String> header) {
        String callId=header.get("Channel-Call-UUID");//uuid
        return callId;
    }

    /**
     * 获取被叫
     * @param header 头
     * @return 被叫
     */
    public String getCallee(Map<String,String> header){
        String destinationNumber=header.get("Caller-Destination-Number");//被叫
        if(destinationNumber!=null){
            destinationNumber=destinationNumber.replace("PARK","");
        }
        return destinationNumber;
    }

    /**
     * 获取主叫
     * @param header 头
     * @return 主叫
     */
    public String getCaller(Map<String,String> header){
        String callerNumber=header.get("Caller-Orig-Caller-ID-Number");//主叫
        if(callerNumber!=null){
            callerNumber=callerNumber.replace("PARK","");
        }
        return callerNumber;
    }

    /**
     * 执行动作方法
     * @param actions 对话数据
     * @return
     */
    public void executeActions(DialogData dialogData,List<DialogAction> actions,String callId){
        if(actions==null){
            return;
        }
        if(actions.size()>0){
            //取出一个动作指令，执行动作，
            DialogAction action=actions.remove(0);
            //保持其余的指令
            saveNextAction(dialogData,actions,callId);
            DialogActionTypeEnum actionTypeEnum=action.getAction();
            BaseAction ccAction=null;
            DialogActionBean actionBean=new DialogActionBean();
            actionBean.setAction(action);
            actionBean.setCallId(callId);
            if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_CREATE_COMPLETE)){
                logger.info("会话创建完成，启动应答");
                ccAction = new AnswerAction(applicationConfig,
                        applicationComponent,
                        clientProxy,
                        actionBean,
                        this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_REFUSE)){
                logger.info("拒接接通，启动挂断操作");
                ccAction=new HangupAction(applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_PLAY_FILE)){
                logger.info("播放文件");
                //保存对话状态
                ccAction=new PlayFileAction(dialogData,applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_PLAY_TTS)){
                logger.info("调用tts合成");
                ccAction=new PlayTTSAction(dialogData,applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_END)){
                logger.info("聊天结束，挂断操作");
                ccAction=new HangupAction(applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_TRANSFER)){
                logger.info("转人工");
                ccAction=new TransferAction(applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_GET_DTMF)){
                logger.info("获取dtmf");
                //获取dtmf操作
                ccAction=new DtmfAction(applicationConfig,applicationComponent,clientProxy,actionBean,this,true);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_DETECT_SPEECH)){
                logger.info("检测输入");
                ccAction=new DetectSpeechAction(applicationConfig,applicationComponent,clientProxy,actionBean,this,true);
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_NONE)){
                logger.info("不执行任何操作");
            }else if(actionTypeEnum.equals(DialogActionTypeEnum.DM_CC_CHAT_PAUSE_PLAY)){
                logger.info("暂停播放");
                ccAction=new BreakAction(applicationConfig,applicationComponent,clientProxy,actionBean,this);
            }
            if(ccAction!=null){
                ccAction.executeAction();
            }
        }
    }

    public void action(){

    }

    /**
     * 存储下次执行动作
     */
    private void saveNextAction(DialogData dialogData, List<DialogAction> actions, String callId){
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        if(statusBean!=null){
            statusBean.setDialogData(dialogData);
            statusBean.setActionBean(actions);
        }
    }
    /**
     * 解析动作
     * @param manageResponse 响应
     */
    public DialogData parseActions(DialogManageResponse manageResponse){
        int status=manageResponse.getStatus();
        logger.info("服务器端响应码 -> {};时间->{};文本描述->{}",status,manageResponse.getRespTime(),manageResponse.getMsg());
        if(status==1000){
            //服务器端执行成功
            DialogData dialogData=manageResponse.getData();
            return dialogData;
        }
        return null;
    }
}
