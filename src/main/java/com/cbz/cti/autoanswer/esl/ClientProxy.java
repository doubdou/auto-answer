package com.cbz.cti.autoanswer.esl;

import com.cbz.cti.autoanswer.constant.FsApplicationName;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.CommandResponse;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-16 18:06
 */

public class ClientProxy {
    FsClient fsClient;

    public void setFsClient(FsClient fsClient) {
        this.fsClient = fsClient;
    }

    /**
     *  @param asrType asr 类型，unimrcp mrcp 方式 裸码流一句话 yntspeech:ynt_sync_asr 裸码流 流式识别 yntspeech:ynt_async_asr
     * @param speechTimeout 多久没说话超时
     * @param recognitionTimeout 连续说50-300ms即判断开始说话
     * @param completeTimeout 静默时间
     * @param sensitivityLevel 能量值
     */
    public CommandResponse sendDetectSpeech(String callId, String asrType, int speechTimeout, int recognitionTimeout, int completeTimeout, int sensitivityLevel,int modelId){
        String detectSpeech = "%s {start-input-timers=true,no-input-timeout=%s,recognition-timeout=%s,speech-complete-timeout=%s,confidence-threshold=%s,sensitivity-level=%s,modelId=%s}%s directory";
        detectSpeech=String.format(detectSpeech,asrType,speechTimeout,recognitionTimeout,completeTimeout,0.2,sensitivityLevel,modelId,"hello");
        return fsClient.sendCommandApi(callId,"execute", FsApplicationName.DETECT_SPEECH,detectSpeech);
    }

    /**
     * 挂断
     * @param callId callId
     * @return
     */
    public CommandResponse sendHangupCommand(String callId){
        return fsClient.sendCommandApi(callId,"execute", FsApplicationName.HANGUP,null);
    }

    /**
     * 播放录音
     * @param callId callId
     * @param args
     * @return
     */
    public CommandResponse sendPlayRecord(String callId,String args){
        fsClient.sendCommandApi(callId,"execute","set","playback_terminators=none");
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.PLAYBACK,args);

    }

    /**
     * answer命令
     * @param callId callId
     * @return
     */
    public CommandResponse sendAnswerCommand(String callId){
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.ANSWER,null);
    }

    /**
     * 录音
     * @param callId callId
     * @param callRecordPath 录音文件名
     * @return
     */
    public CommandResponse sendCallRecord(String callId,String callRecordPath){
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.RECORD_SESSION,callRecordPath);
    }

    public CommandResponse playTTS(String callId, String text,String voice){
        String args="unimrcp:unimrcpserver-tts|"+voice+"|"+text;
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.SPEAK,args);
    }

    public CommandResponse bridge(String callId,String myuuid,String phoneNumber){
        //String args="{transfer_ringback=${fr-ring},park_after_bridge=true,bridge_early_media=true,origination_uuid=%s}user/%s";
        //设置通道变量
        if(phoneNumber==null||phoneNumber.isEmpty()){
            phoneNumber="1002";
        }
//        1. 以后呼叫外线是： sofia/gateway/siproxy/${号码}
//        2. 呼叫内线是： {sip_invite_domain=${域}}sofia/gateway/uas/${号码}
        fsClient.sendCommandApi(callId,"execute","export","park_after_bridge=true");
        fsClient.sendCommandApi(callId,"execute","set","continue_on_fail=true");
        String args="{park_after_bridge=true,origination_uuid=%s,ringback=${us-ring}}sofia/internal/sip:%s@192.168.88.62:5060";
        args=String.format(args,myuuid,phoneNumber);
        //定时器，5秒内没有接到originate事件，表示转接失败
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.TRANER,args);
    }

    public CommandResponse originate(String callId,String myuuid,String phoneNumber) {
        phoneNumber="1002";
        //fsClient.sendCommandApi(callId,"execute","set","continue_on_fail=true");
        String args="sofia/internal/sip:%s@192.168.88.120:5060";
        args=String.format(args,myuuid,phoneNumber);
        fsClient.sendBackgroundApiCommand(FsApplicationName.ORIGINATE,args);
       return null;
    }


    public CommandResponse breakPlay(String callId){
//        return sendPlayRecord(callId,"/home/jinzw/silence.wav");
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.BREAK,null);
    }

    public CommandResponse record(String callId,String path){
//        RECORD_USE_THREAD
        fsClient.sendCommandApi(callId,"execute","set","RECORD_USE_THREAD=true");
        fsClient.sendCommandApi(callId,"execute","set","RECORD_STEREO=true");
//        fsClient.sendCommandApi(callId,"execute","set","enable_file_write_buffering=false");

        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.RECORD_SESSION,path);
    }


    public CommandResponse stopRecord(String callId,String path){
        return fsClient.sendCommandApi(callId,"execute",FsApplicationName.STOP_RECORD_SESSION,path);
    }



    public void reconnect() throws InboundConnectionFailure {
        fsClient.connect();
    }

}
