package com.cbz.cti.autoanswer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 * 应用配置
 * @author jinzw
 * @date 2020-10-13 9:59
 */
@Configuration
@Getter
@Setter
public class ApplicationConfig {
    @Value("${fs.record.path}")
    String recordPath;

    @Value("${asr.module.name}")
    String asrModuleName;

    @Value("${asr.vad.startDur}")
    String startDur;

    @Value("${asr.vad.endDur}")
    String endDur;

    @Value("${asr.vad.threshold}")
    String threshold;

    public List<String> filterWord(){
        return Arrays.asList("行","好","有","要","是","不","没");
    }
}
