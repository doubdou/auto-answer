package com.cbz.cti.autoanswer.utils;

//import ai.cbz.inbound.common.response.Result;
//import ai.cbz.inbound.common.response.location.PhoneResp;
//import ai.cbz.inbound.common.service.location.LocationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-23 15:01
 */
public class CommonUtils {
//    /**
//     * 获取号码归属地
//     * @param service
//     * @param phoneNumber
//     */
//    public static PhoneResp getLocation(LocationService service, String phoneNumber){
//        PhoneResp phoneResp= service.getLocationCity(phoneNumber);
//        return phoneResp;
//    }

    public static Date timestampToDate(long timestamp){
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String d = format.format(timestamp);
        try {
            Date date=format.parse(d);
            return date;
        } catch (ParseException e) {

        }
        return null;
    }

    /**
     * 生成uuid
     * @return
     */
    public static String generateUUID(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
