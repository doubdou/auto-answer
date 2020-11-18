package com.cbz.cti.autoanswer.esl;

import com.cbz.cti.autoanswer.EslConfig;
import com.cbz.cti.autoanswer.listener.EventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.internal.IModEslApi;
import org.freeswitch.esl.client.transport.CommandResponse;
import org.freeswitch.esl.client.transport.SendMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-10 17:34
 */
public class FsClient {
    private static final Logger logger = LoggerFactory.getLogger(FsClient.class);
    EslConfig eslConfig;
    EventListener eslEventListener;
    Client client;
    public FsClient(EslConfig eslConfig, EventListener eslEventListener) {
        this.eslConfig = eslConfig;
        this.eslEventListener=eslEventListener;
    }

    /**
     * 连接请求
     */
    public void connect() throws InboundConnectionFailure {
        if(client!=null){
            //连接前先关闭
            client.close();
            client=null;
        }
        client =new Client();
        InetSocketAddress address=new InetSocketAddress(eslConfig.getHost(),Integer.valueOf(eslConfig.getPort()));
        client.connect(address,eslConfig.getPassword(),30);
        logger.info("监听事件->{}",eslConfig.getEventName());
        client.setEventSubscriptions(IModEslApi.EventFormat.PLAIN,eslConfig.getEventName());
        client.setCallbackExecutor(Executors.newCachedThreadPool());
        ClientProxy proxy=new ClientProxy();
        proxy.setFsClient(this);
        eslEventListener.setClientProxy(proxy);
        client.addEventListener(eslEventListener);
        logger.info("连接成功");
    }

    public CommandResponse sendCommandApi(String callId,String command,String appName,String args){
        SendMsg sendMsg=new SendMsg(callId);
        sendMsg.addCallCommand(command);
        sendMsg.addExecuteAppName(appName);
        if(args!=null){
            sendMsg.addExecuteAppArg(args);
        }
        CommandResponse response= client.sendMessage(sendMsg);
        return response;
    }

    public void sendBackgroundApiCommand(String appName,String param){
        client.sendApiCommand(appName,param);
//        client.sendBackgroundApiCommand(appName,param);
    }
}
