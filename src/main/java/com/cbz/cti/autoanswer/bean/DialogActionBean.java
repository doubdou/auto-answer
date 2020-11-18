package com.cbz.cti.autoanswer.bean;

import ai.cbz.inbound.common.response.DialogAction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * TODO
 * @author jinzw
 * @date 2020-10-21 16:12
 */
@Getter
@Setter
public class DialogActionBean {
    DialogAction action;
    String callId;
}
