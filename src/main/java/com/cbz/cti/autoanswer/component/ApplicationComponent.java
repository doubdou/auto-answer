package com.cbz.cti.autoanswer.component;

import ai.cbz.inbound.common.api.IDialogService;
//import ai.cbz.inbound.service.DialogService;
//import ai.cbz.inbound.common.service.location.LocationService;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 * 应用组件
 * @author jinzw
 * @date 2020-10-13 11:55
 */
@Component
@Setter
@Getter
public class ApplicationComponent {
    //mq组件
//    @Autowired
//    MqUtils mqUtils;
    //号码归属地组件
//    @Reference(version = "1.0.0")
//    LocationService locationService;

    @DubboReference
    public IDialogService dialogService;
//    @Reference(version = "1.0.0")
//    @Autowired
//    public DialogService dialogService;
}
