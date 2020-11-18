package com.cbz.cti.autoanswer;

import com.cbz.cti.autoanswer.esl.FsClient;
import com.cbz.cti.autoanswer.utils.SpringUtil;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-10 16:38
 */
@SpringBootApplication
//@EnableDubbo(scanBasePackages = "ai.cbz.inbound.autoanswer")
@EnableDubbo(scanBasePackages = "ai.cbz.inbound.common.api")
public class AutoAnswerApplication {
    public static void main(String [] args) throws InboundConnectionFailure {
        SpringApplication.run(AutoAnswerApplication.class,args);
        FsClient fsClient = SpringUtil.getBean(FsClient.class);
        fsClient.connect();
    }
}
