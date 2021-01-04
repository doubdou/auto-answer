package com.cbz.cti.autoanswer.message;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DtmfActionDataBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
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
 * @author jinzw
 * @date 2020-10-29 15:16
 */
public class DtmfEventMessage extends BaseEventMessage {
    private static final Logger logger = LoggerFactory.getLogger(DtmfEventMessage.class);
    public DtmfEventMessage(ApplicationConfig applicationConfig, ApplicationComponent applicationComponent, ClientProxy clientProxy) {
        super(applicationConfig, applicationComponent, clientProxy);
    }

    @Override
    public synchronized void handler(EslEvent eslEvent) {
        //判断此时接受的输入是否是dtmf
        String callId=getCallId(eslEvent.getEventHeaders());
        String dtmfDigit=eslEvent.getEventHeaders().get("DTMF-Digit");
        logger.info("接受到dtmf信号 -> {},callId  -> {} ",dtmfDigit,callId);
        ChannelStatusBean statusBean= ChannelStatusManager.getChannelStatus(callId);
        if(statusBean.getInputType()== ChannelStatusTypeBean.DTMF_INPUT||
                statusBean.getSupportBreak()==ChannelStatusTypeBean.DTMF_VOICE_BREAK||
                statusBean.getSupportBreak()==ChannelStatusTypeBean.DTMF_BREAK){
            DtmfActionDataBean actionDataBean= ChannelStatusManager.getDtmfActionData(callId);
            if(statusBean.getInputType()==ChannelStatusTypeBean.DTMF_INPUT){
                logger.info("目前的按键为 ---> {}", JSONObject.toJSONString(actionDataBean));
                if(actionDataBean.getCurrDtmf()==null){
                    actionDataBean.setCurrDtmf(dtmfDigit);
                }else {
                    actionDataBean.setCurrDtmf(actionDataBean.getCurrDtmf()+dtmfDigit);
                }
                logger.info("拼接完成后按键为 ---> {}",JSONObject.toJSONString(actionDataBean));
                //判断是否是dtmf按键开始，且不等于结束符,且再播放中
                if(actionDataBean.getCurrDtmf().length()==1&&
                        statusBean.getSupportBreak()!=ChannelStatusTypeBean.NOT_BREAK&&
                        !actionDataBean.getCurrDtmf().equals(actionDataBean.getDtmfEndTag())&&
                statusBean.getPlayStatus()==ChannelStatusTypeBean.PORCESS_PLAY){
                    logger.info("执行接受dtmf动作，发送打断事件",statusBean.getDtmfTimeout());
                    //判断后面是否持续有dtmf，如果没有，则应该设置客户输入的内容
//                    if(actionDataBean.getDtmfLength()==-1&&actionDataBean.getDtmfEndTag().isEmpty()){
//                        ChannelDialogueBean dmDialogueBean=ChannelStatusManager.getChannelDialogue(callId);
//                        RecordDTO recordDTO=dmDialogueBean.getRecordCustomerDTO();
//                    }
                    requestDm(dtmfDigit,callId,true);
                }else {
                    logger.info("接收dtmf 长度--> {},结束标志为--> {}",actionDataBean.getDtmfLength(),actionDataBean.getDtmfEndTag());
                    if(actionDataBean.getDtmfLength()==-1){
                        //判断是否是结尾
                        if(actionDataBean.getDtmfEndTag().equals(dtmfDigit)){
                            //
                            logger.info("接收到dtmf结束标识  -> {}",dtmfDigit);
                            //取消超时
                            ChannelStatusManager.cancelDtmfTimeout(callId);
                            //设置此时接受状态
                            ChannelStatusManager.getChannelStatus(callId).setInputType(ChannelStatusTypeBean.FPRBID_INPUT);
                            //发送消息到消息记录;
                            logger.info("dtmf 输入结果 -> {}",actionDataBean.getCurrDtmf());
                            //请求Dm
                            String dtmfStr=actionDataBean.getCurrDtmf();
                            actionDataBean.setDtmfLength(-1);
                            actionDataBean.setDtmfEndTag("");
                            actionDataBean.setCurrDtmf("");
                            requestDm(dtmfStr,callId, false);
                        }
                    }else{
                        //判断是否等于这个长度
                        if(actionDataBean.getCurrDtmf().length()==actionDataBean.getDtmfLength()){
                            logger.info("获取dtmf长度等于设定的长度，开始请求Ai");
                            //取消超时
                            ChannelStatusManager.cancelDtmfTimeout(callId);
                            //设置此时接受状态
                            ChannelStatusManager.getChannelStatus(callId).setInputType(ChannelStatusTypeBean.FPRBID_INPUT);
                            //发送消息到消息记录;
                            logger.info("dtmf 输入结果 -> {}",actionDataBean.getCurrDtmf());
                            //请求Dm
                            String dtmfStr=actionDataBean.getCurrDtmf();
                            actionDataBean.setDtmfLength(-1);
                            actionDataBean.setDtmfEndTag("");
                            actionDataBean.setCurrDtmf("");
                            requestDm(dtmfStr,callId, false);
                        }
                    }
                }
            }else if(statusBean.getSupportBreak()==ChannelStatusTypeBean.DTMF_VOICE_BREAK||statusBean.getSupportBreak()==ChannelStatusTypeBean.DTMF_BREAK){
                logger.info("dtmf 执行打断操作");
                if(statusBean.getPlayStatus()==ChannelStatusTypeBean.PORCESS_PLAY){
                    if(statusBean.getDtmfBreakTag()==ChannelStatusTypeBean.DTMF_BREAK_NOT_REQ){
                        logger.info("请求dm打断操作");
                        statusBean.setDtmfBreakTag(ChannelStatusTypeBean.DTMF_BREAK_REQ);
                        requestDm(dtmfDigit,callId, true);
                        actionDataBean.setCurrDtmf("");
                    }
                }
            }
        }else {
            logger.info("接受到dtmf信号，但是此时接受方式不是dtmf");
        }
    }

    private void requestDm(String dtmf, String callId, boolean isBreak){
        logger.info("-------------------------请求DM响应---------------------------");
        DialogManageRequest dmRequest=new DialogManageRequest();
        dmRequest.setReqType(DialogRequestEnum.AA_DM_CHAT_DTMF);
        dmRequest.setChatId(callId);
        dmRequest.setText(dtmf);
        DialogManageResponse manageResponse=applicationComponent.getDialogService().dialogManage(dmRequest);
        DialogData dialogData=parseActions(manageResponse);
        ChannelStatusBean statusBean=ChannelStatusManager.getChannelStatus(callId);
        //判断是否打断请求
        if(isBreak){
            dialogData.getActions().addAll(statusBean.getActionBean());
        }
        logger.info("debug 按键1 {}", JSONObject.toJSONString(ChannelStatusManager.getDtmfActionData(callId)));
        if(dialogData!=null){
            statusBean.setSpeechStatus(ChannelStatusTypeBean.NO_VOICE);
            logger.info("debug 按键2 {}", JSONObject.toJSONString(ChannelStatusManager.getDtmfActionData(callId)));
            executeActions(dialogData,dialogData.getActions(),callId);
            logger.info("debug 按键3  {}", JSONObject.toJSONString(ChannelStatusManager.getDtmfActionData(callId)));
        }
    }
}
