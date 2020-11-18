package com.cbz.cti.autoanswer.timer;

import com.cbz.cti.autoanswer.esl.ClientProxy;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-29 17:20
 */
public class HangupTask implements TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(HangupTask.class);
    ClientProxy proxy;
    String callId;

    public HangupTask(ClientProxy proxy, String callId) {
        this.proxy = proxy;
        this.callId = callId;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        logger.info("执行挂断");
        proxy.sendHangupCommand(callId);
    }
}
