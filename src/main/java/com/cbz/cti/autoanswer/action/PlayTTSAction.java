package com.cbz.cti.autoanswer.action;

import ai.cbz.inbound.common.response.DialogAction;
import ai.cbz.inbound.common.response.DialogData;
import ai.cbz.inbound.common.response.DialogPlayTTSParams;
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

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-21 17:03
 */
public class PlayTTSAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(PlayTTSAction.class);
    DialogData dialogData;
    public PlayTTSAction(DialogData dialogData,
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
        DialogAction action= actionBean.getAction();
        DialogPlayTTSParams playTTSParam= (DialogPlayTTSParams) action.getParams();
        //判断是否打断，用来开启
        ChannelStatusBean channelStatusBean = ChannelStatusManager.getChannelStatus(actionBean.getCallId());
        //查看播放状态
        int status=channelStatusBean.getPlayStatus();
      if(status== ChannelStatusTypeBean.PORCESS_PLAY){
          logger.info("目前正在播放中");
          proxy.breakPlay(actionBean.getCallId());
          //加入到后续的列表中
          channelStatusBean.getActionBean().add(0,actionBean.getAction());
      }else {
          logger.info("tts 合成直接播放 text -> {}  ",playTTSParam.getText());
          if(channelStatusBean != null){
              //判断是否打断，用来开启
              channelStatusBean .setCurrentApp(FsApplicationName.SPEAK);
              channelStatusBean.setSupportBreak(playTTSParam.getSupportBreak());
              //开始播放状态
              channelStatusBean.setPlayStatus(ChannelStatusTypeBean.PORCESS_PLAY);
              channelStatusBean.setDtmfBreakTag(ChannelStatusTypeBean.DTMF_BREAK_NOT_REQ);
          }

          proxy.playTTS(actionBean.getCallId(),playTTSParam.getText(),playTTSParam.getVoice());
      }
    }
}
