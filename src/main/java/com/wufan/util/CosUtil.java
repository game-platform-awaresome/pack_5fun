package com.wufan.util;

import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.StatFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wufan.constant.ServiceConstant.*;
import static com.wufan.util.ConfigurationManager.getProperty;

/**
 * Created by 7cc on 2017/9/11
 */
public class CosUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CosUtil.class);
    private static COSClient cosClient;
    private static final String bucketName = getProperty("bucketName");
    private static final String JSON_KEY_DATA = "data";
    private static final String JSON_KEY_FILESIZE = "filesize";

    static {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(REGION);
        Credentials cred = new Credentials(APPID, SECRETID, SECRETKEY);
        cosClient = new COSClient(clientConfig, cred);
        if (cosClient == null) {
            throw new IllegalArgumentException("cosClient init exception");
        }
    }

    /**
     *  get source url
     *
     * @param ret
     * @return  sourceUrl
     */
    public static String getCosUrlByRet(String ret) {
        return ret.split(",")[4]
                .replaceAll("\"", "")
                .replaceAll("source_url:", "");
    }

    public static Integer getFileSizeByAttributeJson(String fileAttributeJson) {
        try {
            return JSONObject.parseObject(fileAttributeJson)
                    .getJSONObject(JSON_KEY_DATA)
                    .getInteger(JSON_KEY_FILESIZE);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     *   upload file into cos return ret
     *
     * @param localFilePath
     * @param cosFilePath
     * @return  uploadFileRet
     */
    public static String uploadVersionPack(String localFilePath, String cosFilePath) {
        UploadFileRequest uploadFileRequest =
                new UploadFileRequest(bucketName, cosFilePath, localFilePath);
        uploadFileRequest.setEnableShaDigest(false);
        return cosClient.uploadFile(uploadFileRequest);
    }


    public static String getFileAttributeJson(String cosFilePath) {
        StatFileRequest statFileRequest = new StatFileRequest(bucketName, cosFilePath);
        return cosClient.statFile(statFileRequest);
    }
}
