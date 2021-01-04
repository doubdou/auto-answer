package com.cbz.cti.autoanswer.action;

import ai.cbz.inbound.common.response.DialogAction;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogPlayFileParams;
import com.cbz.cti.autoanswer.constant.FsApplicationName;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-21 16:32
 */
public class PlayFileAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(PlayFileAction.class);
    DialogData dialogData;
    public PlayFileAction(DialogData dialogData,
                          ApplicationConfig applicationConfig,
                          ApplicationComponent applicationComponent,
                          ClientProxy proxy,
                          DialogActionBean actionBean,
                          BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
        this.dialogData=dialogData;
    }
    @Override
    public void executeAction() {
        //取消上一个定时器，有可能存在
        ChannelStatusManager.cancelAsrTimeout(actionBean.getCallId());
        DialogPlayFileParams playFileParams = (DialogPlayFileParams) actionBean.getAction().getParams();
        logger.info("播放文件录音->{};contextId->{}",playFileParams.getPath(),actionBean.getCallId());
        //获取状态
        ChannelStatusBean channelStatusBean=ChannelStatusManager.getChannelStatus(actionBean.getCallId());
        if(channelStatusBean!=null){
            int status=channelStatusBean.getPlayStatus();
            if(status== ChannelStatusTypeBean.PORCESS_PLAY){
                //目前正在播放
                ChannelStatusBean statusBean=ChannelStatusManager.getChannelStatus(actionBean.getCallId());
                //加入到后续的列表中
                List<DialogAction> actions=statusBean.getActionBean();
                List<DialogAction> updateActions=new ArrayList<>();
                updateActions.add(actionBean.getAction());
                updateActions.addAll(actions);
                statusBean.setActionBean(updateActions);
                logger.info("等待当前音频停止  app size -> {} callId -> {}",statusBean.getActionBean().size(),actionBean.getCallId());
                proxy.breakPlay(actionBean.getCallId());
            }else {
                if(channelStatusBean != null){
                    //判断是否打断，用来开启
                    logger.info("本段音频打断方式为---------{}",playFileParams.getSupportBreak());
                    channelStatusBean .setCurrentApp(FsApplicationName.PLAYBACK);
                    channelStatusBean.setSupportBreak(playFileParams.getSupportBreak());
                    channelStatusBean.setPlayStatus(ChannelStatusTypeBean.PORCESS_PLAY);
                }
//                logger.info("直接播放, 文本内容 -> {}",playFileParams.getText());
//                saveAiSpeech(playFileParams,null,actionBean.getCallId(),dialogData);
                //判断是否提前执行asr和dtmf动作
                breakActionCheck(channelStatusBean);
                proxy.sendPlayRecord(actionBean.getCallId(),playFileParams.getPath().trim());
            }
        }
    }


}
