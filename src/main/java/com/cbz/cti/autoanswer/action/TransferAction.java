package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.ChannelStatusTypeBean;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.cache.ChannelStatusManager;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.timer.TransTask;
import com.cbz.cti.autoanswer.timer.WheelTimerUtils;
import com.cbz.cti.autoanswer.utils.CommonUtils;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 转人工
 * @author jinzw
 * @date 2020-10-21 16:33
 */
public class TransferAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(TransferAction.class);
    BaseEventMessage eventMessage;
    public TransferAction(ApplicationConfig applicationConfig,
                          ApplicationComponent applicationComponent,
                          ClientProxy proxy,
                          DialogActionBean actionBean,
                          BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
        this.eventMessage=eventMessage;
    }

    @Override
    public void executeAction() {
        ChannelStatusManager.getChannelStatus(actionBean.getCallId()).setInputType(ChannelStatusTypeBean.FPRBID_INPUT);//设置禁止输入
        //开始转人工处理

        //先originate 在 振铃的时候 bridge 、挂断后park_after_bridge
        String myuuid= CommonUtils.generateUUID();
        logger.info("开始转人工处理;生成的myuuid->{}",myuuid);
//        proxy.originate(actionBean.getCallId(),myuuid,transferParam.getTransferChannelName());
        //定时器执行，等待5s查看是否有originate返回，如果没返回，则标记转人工失败
        TransTask transTask=new TransTask(actionBean.getCallId(),applicationComponent,eventMessage,myuuid);
        Timeout timeout= WheelTimerUtils.submitTask(transTask,5000);
        //保存timeout
        ChannelStatusManager.addTransTimeout(myuuid,timeout);
    }
}
