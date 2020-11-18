package com.cbz.cti.autoanswer.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 * dtmf动作bean
 * @author jinzw
 * @date 2020-10-29 15:46
 */
@Getter
@Setter
public class DtmfActionDataBean {
    int timeout;
    String dtmfEndTag;
    String currDtmf="";
    int dtmfLength=-1;
}
