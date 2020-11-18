package com.cbz.cti.autoanswer.action;

import com.cbz.cti.autoanswer.ApplicationConfig;
import com.cbz.cti.autoanswer.bean.DialogActionBean;
import com.cbz.cti.autoanswer.component.ApplicationComponent;
import com.cbz.cti.autoanswer.esl.ClientProxy;
import com.cbz.cti.autoanswer.message.BaseEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-21 16:48
 */
public class AnswerAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(AnswerAction.class);
//    String predictorVersion;
    public AnswerAction(ApplicationConfig applicationConfig,
                        ApplicationComponent applicationComponent,
                        ClientProxy proxy, DialogActionBean actionBean,
                        BaseEventMessage eventMessage) {
        super(applicationConfig, applicationComponent, proxy, actionBean,eventMessage);
//        this.predictorVersion=predictorVersion;
    }

    @Override
    public void executeAction() {
        logger.info("对话创建");
        getProxy().sendAnswerCommand(actionBean.getCallId());
    }
}
