package com.wufan.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;

import static com.wufan.constant.ServiceConstant.*;

/**
 * Created by 7cc on 2017/9/11
 */
public class CosUtil {

    private static COSClient cosClient;

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

    /**
     *   upload file into cos return ret
     *
     * @param bucketName
     * @param localFilePath
     * @param cosFilePath
     * @return  uploadFileRet
     */
    public static String uploadVersionPack(String bucketName, String localFilePath, String cosFilePath) {
        UploadFileRequest uploadFileRequest =
                new UploadFileRequest(bucketName, cosFilePath, localFilePath);
        uploadFileRequest.setEnableShaDigest(false);
        return cosClient.uploadFile(uploadFileRequest);
    }
}
