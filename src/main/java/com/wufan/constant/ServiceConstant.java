package com.wufan.constant;

/**
 * Created by 7cc on 2017/9/8
 */
public interface ServiceConstant {

    /** (支持并行的最大个数) 满足n个任务发送给线程池执行 */
    int TASKSIZE = 10;
    /** 任务个数不够触发执行, 时间当前值到自动执行 */
    int WAITTASKTIME = 10000;
    /** 打包超时时间Future停止超时任务时间 */
    int FUTUREGETTIMEOUT = Integer.MAX_VALUE;


    /** 腾讯云COS */
    long APPID = 1254307713;
    String SECRETID = "AKIDYRCl8Iq7olYDoGekqrhL4eLw1yodtvLb";
    String SECRETKEY = "CPn4nOn061mLiv1UowuhIhtHHG9rNI4e";
    String REGION = "sh";
}
