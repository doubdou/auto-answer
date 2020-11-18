package com.cbz.cti.autoanswer.bean;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-06-03 10:40
 */
public class ChannelStatusTypeBean {
    public static final int NOT_BREAK=1;
    public static final int DTMF_BREAK=2;
    public static final int VOICE_BREAK=3;
    public static final int DTMF_VOICE_BREAK=4;

    public static final int FPRBID_INPUT=1;
    public static final int VOICE_INPUT=2;
    public static final int DTMF_INPUT=3;
    public static final int DTMF_VOICE_INPUT=4;

    //目前用户说话状态  1未说话 2说话开始 3 说话结束 4 等待asr识别

    public static final int NO_VOICE=1;
    public static final int BEAGIN_VOICE=2;
    public static final int END_VOICE=3;
    public  static final int WAIT_ASR=4;

    /**
     * 目前状态 1未播放 2 播放状态
     */

    public static final int NOT_PLAY=1;
    public static final int PORCESS_PLAY=2;

    //dtmf 打断标记
    public static final int DTMF_BREAK_NOT_REQ=0;
    public static final int DTMF_BREAK_REQ=1;

}
