package com.wufan.actor;

import com.alibaba.fastjson.JSONObject;
import com.wufan.model.output.PackURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 7cc on 2017/9/8
 */
public class FutureResponse {

    private static final Logger LOG = LoggerFactory.getLogger(FutureResponse.class);
    private volatile PackURL packURL = new PackURL();
    public final static Map<String, FutureResponse> FUTURES = new ConcurrentHashMap<>();
    private volatile Lock lock = new ReentrantLock();
    private volatile Condition condition = lock.newCondition();

    public FutureResponse() {   }

    public FutureResponse(String taskId) {
        packURL.setTaskId(taskId);
        FUTURES.put(taskId, this);
    }

    public FutureResponse(String taskId, String callBackUrl) {
        packURL.setTaskId(taskId);
        packURL.setCallBackUrl(callBackUrl);
        FUTURES.put(taskId, this);
    }

    public PackURL get() {
        lock.lock();
        try {
            while (!hasURL()) {
                condition.await();
            }
        } catch (Throwable e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return packURL;
    }

    public static void receiveCallBack(PackURL packURL) {
        try {
            FutureResponse future = FUTURES.remove(packURL.getTaskId());
            if (future == null) {
                return;
            }
            packURL.setCallBackUrl(future.packURL.getCallBackUrl());
            String callBackUrl = buildCallBackUrl(packURL);
            HttpRequestUtil.sendUrl(callBackUrl);
        } catch (Throwable e) {
            LOG.error(String.format("receiveCallBack fail - packURL==%s - error info==%s", JSONObject.toJSON(packURL), e.getMessage()), e);
        }

    }

    private static String buildCallBackUrl(PackURL packURL) throws UnsupportedEncodingException {
        String callBackUrl = packURL.getCallBackUrl();
        String cosUrl = packURL.getCosUrl();
        String taskId = packURL.getTaskId();
        String status = String.valueOf(packURL.getStatus());

        return new StringBuilder(callBackUrl.trim())
                .append("&taskId=")
                .append(URLEncoder.encode(taskId.trim(),"utf-8"))
                .append("&status=")
                .append(URLEncoder.encode(status.trim(),"utf-8"))
                .append("&cosUrl=")
                .append(URLEncoder.encode(cosUrl.trim(),"utf-8"))
                .toString();
    }


    public static void receive(PackURL packURL) {
        FutureResponse future = FUTURES.remove(packURL.getTaskId());
        if (future == null) {
            return;
        }
        Lock lock = future.lock;
        lock.lock();
        try {
            future.packURL = packURL;
            Condition condition = future.condition;
            if(condition != null) {
                condition.signal();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private boolean hasURL() {
        return packURL.getCosUrl() != null ? true : false;
    }

    /**
     *  内部类 , 用户发送数据的http工具类
     *  @author 7cc
     *
     */
    public static class HttpRequestUtil {
        /**
         *  具体发送url 的方法
         * @param callBackUrl
         * @throws IOException
         */
        public static void sendUrl(String callBackUrl) throws IOException {
            HttpURLConnection con = null;
            BufferedReader in = null;
            try {
                URL obj = new URL(callBackUrl);
                con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("GET");

                LOG.info(String.format("Callback - sendUrl:%s", callBackUrl));
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                } catch (Throwable e) {
                    // nothing
                }
                try {
                    con.disconnect();
                } catch (Throwable e) {
                    // nothing
                }
            }
        }
    }


}
