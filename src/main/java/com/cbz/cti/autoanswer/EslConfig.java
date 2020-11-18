package com.cbz.cti.autoanswer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-10 17:09
 */
@Configuration
public class EslConfig {
    @Value("${fs.esl.host}")
    String host;
    @Value("${fs.esl.port}")
    String port;
    @Value("${fs.esl.password}")
    String password;
    @Value("${fs.esl.event.name}")
    String eventName;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


}
