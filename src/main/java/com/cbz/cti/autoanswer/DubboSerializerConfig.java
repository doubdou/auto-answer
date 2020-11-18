package com.cbz.cti.autoanswer;

//import ai.cbz.inbound.common.support.StackTraceElementSerializer;
import org.apache.dubbo.common.serialize.support.SerializableClassRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@Configuration
public class DubboSerializerConfig {

    @Bean
    public void initDubboSerializer() {
//        SerializableClassRegistry.registerClass(StackTraceElement.class, new StackTraceElementSerializer());
    }
}
