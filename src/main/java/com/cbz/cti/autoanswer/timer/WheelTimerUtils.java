package com.cbz.cti.autoanswer.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author jinzw
 * @date 2020-10-17 9:48
 */
public class WheelTimerUtils {

    /**
     * 时间轮定时器
     */
    private static HashedWheelTimer wheelTimer=new HashedWheelTimer(100, TimeUnit.MILLISECONDS,1000);

    /**
     * 提交任务
     */
    public static Timeout submitTask(TimerTask task, long delay){
        return wheelTimer.newTimeout(task,delay,TimeUnit.MILLISECONDS);
    }
}
