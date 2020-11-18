package com.cbz.cti.autoanswer.bean;

import ai.cbz.inbound.common.response.DialogAction;
import ai.cbz.inbound.common.response.DialogData;
import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;
import java.util.Stack;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-17 10:07
 */
@Getter
@Setter
public class ChannelStatusBean {

    /**
     * 目前正在运行的app
     */
    private String currentApp;

    /**
     * 目前是否支持打断打断方式（1不支持打断 2dtmf 3语音 4语音和dtmf,）
     */
    private int supportBreak;

    /**
     * 输入方式 1 禁止输入，2  语音输入 3 dtmf输入 4 dtmf和语音输入
     */
    private int inputType;


    private int speechStatus=0;


    private int playStatus;

    /**
     * dtmf 打断标记 0 未前期 1 正在请求
     */
    private int dtmfBreakTag;

    /**
     * 需要执行的action列表
     */
    private List<DialogAction> actionBean;
    /**
     * 原始数据
     */
    private DialogData dialogData;

    /**
     * 通话通道ID
     */
    private String callId;
    /**
     * 录音开始时间
     */
    private long recordStartTime;

    /**
     * 连续几次识别为空情况
     */
    private int asrSpeechNullNum=0;

    private int dtmfTimeout=0;
    /**
     * 语义理解产品番号
     */
//    private String predictVersion;
    /**
     * asr模型id
     */
    private int asrModeId=0;

}
