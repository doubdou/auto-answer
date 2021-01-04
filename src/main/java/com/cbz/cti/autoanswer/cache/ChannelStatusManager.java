package com.cbz.cti.autoanswer.cache;

import com.cbz.cti.autoanswer.bean.ChannelDialogueBean;
import com.cbz.cti.autoanswer.bean.ChannelStatusBean;
import com.cbz.cti.autoanswer.bean.DtmfActionDataBean;
import com.cbz.cti.autoanswer.message.PlayStopEventMessage;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * 通道状态管理
 * @author jinzw
 * @date 2020-10-17 9:58
 */
public class ChannelStatusManager {
    private static final Logger logger = LoggerFactory.getLogger(ChannelStatusManager.class);
    private static ConcurrentHashMap<String, ChannelStatusBean> channelStatusMap=new ConcurrentHashMap<>(64);
    //对话管理
    private static  ConcurrentHashMap<String, ChannelDialogueBean> channelDialogueMap=new ConcurrentHashMap<>(64);
    //转人工超时管理
    private static ConcurrentHashMap<String, Timeout> transTimeoutMap=new ConcurrentHashMap<>(64);
    //打断超时管理
    private static ConcurrentHashMap<String,Timeout> breakTimoutMap=new ConcurrentHashMap<>(64);
    //dtmf 超时管理
    private static ConcurrentHashMap<String,Timeout> dtmfTimeoutMap=new ConcurrentHashMap<>(64);
    //dtmf 按键存储
    private static ConcurrentHashMap<String, DtmfActionDataBean> dtmfActionMap=new ConcurrentHashMap<>(64);
    //asr 识别超时
    private static ConcurrentHashMap<String,Timeout> asrTimeoutMap=new ConcurrentHashMap<>(64);
    /**
     * 增加
     */
    public static void addAsrTimeout(String callId, Timeout asrNoInputTimeout){
        cancelAsrTimeout(callId);
        asrTimeoutMap.put(callId,asrNoInputTimeout);
    }

    /**
     * 取消超时
     * @param callId
     */
    public static void cancelAsrTimeout(String callId){
        Timeout timeout=asrTimeoutMap.remove(callId);
        if(timeout!=null&&!timeout.isExpired()){
            timeout.cancel();
        }
    }

    /**
     * 增加dtmf动作实体
     * @param callId
     * @param actionDataBean
     */
    public static void addDtmfActionData(String callId,DtmfActionDataBean actionDataBean){
        dtmfActionMap.remove(callId);
        dtmfActionMap.put(callId,actionDataBean);
    }

    /**
     * 获取dtmf实体
     * @param callId
     * @return
     */
    public static DtmfActionDataBean getDtmfActionData(String callId){
        return dtmfActionMap.get(callId);
    }

    /**
     * 增加dtmf 超时
     * @param callId
     * @param timeout
     */
    public static void addDtmfTimeout(String callId,Timeout timeout){
        dtmfTimeoutMap.put(callId,timeout);
    }

    /**
     * 取消dtmf超时
     * @param callId
     */
    public static void cancelDtmfTimeout(String callId){
        Timeout timeout=dtmfTimeoutMap.remove(callId);
        if(timeout!=null){
            //
            if(!timeout.isExpired()){
                timeout.cancel();
            }
        }
    }

    /**
     *移除转移超时
     */
    public static void removeDtmfTimeout(String myuuid){
        dtmfTimeoutMap.remove(myuuid);
    }


    /**
     * 增加打断超时
     * @param callId
     * @param timeout
     */
    public static void addBreakTimeout(String callId,Timeout timeout){
        breakTimoutMap.put(callId,timeout);
    }

    /**
     * 取消打断超时
     * @param callId
     */
    public static void cancleBreakTimeout(String callId){
       Timeout timeout= breakTimoutMap.remove(callId);
        if(timeout!=null){
            timeout.cancel();
        }
    }


    /**&
     * 增加超时
     * @param myuuid
     * @param timeout
     */
    public static void addTransTimeout(String myuuid,Timeout timeout){
        transTimeoutMap.put(myuuid,timeout);
    }

    public static void cancelTransTimeout(String myuuid){
        Timeout timeout=transTimeoutMap.remove(myuuid);
        if(timeout!=null){
            timeout.cancel();
        }
    }


    /**
     * 增加对话
     * @param callId
     * @param dialogueBean
     */
    public static void addChannelDialogue(String callId,ChannelDialogueBean dialogueBean){
        channelDialogueMap.put(callId,dialogueBean);
    }

    public static ChannelDialogueBean getChannelDialogue(String callId){
        return channelDialogueMap.get(callId);
    }

    public static ChannelDialogueBean removeChannelDiaogue(String callId){
        return channelDialogueMap.remove(callId);
    }

    /**
     * 增加
     * @param callId
     * @param channelStatusBean
     */
    public static void  addChannelStatus(String callId,ChannelStatusBean channelStatusBean){
        channelStatusMap.put(callId,channelStatusBean);
    }

    /**
     * 获取状态
     * @param callId
     * @return
     */
    public static ChannelStatusBean getChannelStatus(String callId){
        ChannelStatusBean channelStatusBean = channelStatusMap.get(callId);
        logger.info("ChannelStatusManager getChannelStatus,callId:{} {}",callId,channelStatusBean.getActionBean().toString());
//        return channelStatusMap.get(callId);
        return channelStatusBean;
    }

    /**
     * 清空
     * @param callId
     */
    public static void clearChannelCache(String callId){
        channelStatusMap.remove(callId);
        channelDialogueMap.remove(callId);
        transTimeoutMap.remove(callId);
        breakTimoutMap.remove(callId);
        dtmfTimeoutMap.remove(callId);
        dtmfActionMap.remove(callId);
        cancelAsrTimeout(callId);
        asrTimeoutMap.remove(callId);
    }
}
