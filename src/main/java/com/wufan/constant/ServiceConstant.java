package com.wufan.constant;

import static com.wufan.util.ConfigurationManager.getProperty;

/**
 * Created by 7cc on 2017/9/8
 */
public interface ServiceConstant {

    /** (支持并行的最大个数) 满足n个任务发送给线程池执行 */
    int TASKSIZE = Integer.valueOf(getProperty("TASKSIZE"));
    /** 任务个数不够触发执行, 时间当前值到自动执行 */
    int WAITTASKTIME = Integer.valueOf(getProperty("WAITTASKTIME"));
    /** 打包超时时间Future停止超时任务时间 */
    int FUTUREGETTIMEOUT = Integer.MAX_VALUE;


    /** 腾讯云COS */

    long APPID = Long.valueOf(getProperty("APPID"));
    String SECRETID = getProperty("SECRETID");
    String SECRETKEY = getProperty("SECRETKEY");
    String REGION = getProperty("REGION");
}
