package com.wufan.handle;

import com.alibaba.fastjson.JSONObject;
import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackURL;
import com.wufan.util.BatCallUtil;
import com.wufan.util.ConfigurationManager;
import com.wufan.util.CosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static com.wufan.util.ConfigurationManager.getProperty;

/**
 * 调用c++脚本,返回taskId 和 url
 *
 * Created by 7cc on 2017/9/9
 */
public class PackTaskCallable implements Callable<PackURL> {

    private static final Logger LOG = LoggerFactory.getLogger(PackTaskCallable.class);

    private static final String bucketName = getProperty("bucketName");
    private static final String outPutFD = getProperty("outPutFD");

    public static int WAITBATPACKTIME =  Integer.valueOf(getProperty("WAITBATPACKTIME"));
    private static final String versionPackPath = getProperty("versionPackPath");

    private PackInfo packInfo;

    public PackTaskCallable(PackInfo packInfo){
        this.packInfo = packInfo;
    }

    public PackInfo getPackInfo(){
        return packInfo;
    }

    @Override
    //TODO 调用c++脚本,返回taskId|url
    public PackURL call() throws Exception {
        PackURL packURL = new PackURL();
        String versionPack = String.format(versionPackPath, packInfo.getVersionPack());
        String version = packInfo.getVersion();
        long ad = packInfo.getAd();
        LocalDateTime date = packInfo.getStartTime();
        // TODO 测试模式
        String newPackPath = buildNewPackPath(version, ad, date);
//        String newPackPath = "F:\\ccccccc\\time.txt";
        long batExecTime = System.currentTimeMillis();
        int i = BatCallUtil.execBat(versionPack, newPackPath, ad);
        Thread.sleep(WAITBATPACKTIME);
        LOG.info(String.format("batExec status code = %s batExecTime == %s", i, System.currentTimeMillis() - batExecTime));
//        int i = BatCallUtil.testbat();
        if (i != 0) {
            packURL.setStatus(-1);
            return packURL;
        }
//            Thread.sleep(7000);
        String cosUrl = version + "/" + newPackPath.split("\\\\")[3];
        String cosFilePath = "/" + cosUrl;
        String uploadFileRet = CosUtil.uploadVersionPack(bucketName, newPackPath, cosFilePath);
        LOG.info(String.format("uploadFileRet - %s", uploadFileRet));
        try {
            LOG.info(String.format("access url = %s", CosUtil.getCosUrlByRet(uploadFileRet)));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            cosUrl = "uploadCOS error " + uploadFileRet;
            packURL.setStatus(-1);
        }
        packURL.setCosUrl(cosUrl);
        packURL.setTaskId(packInfo.getTaskId());
        return packURL;
    }

    /**
     * @return E:\version\date\round6-version-ad-date.exe
     */
    private String buildNewPackPath(String version, long ad, LocalDateTime date) {
        // create new pack dir
        String fileDirStr = String.format("%s\\%s\\%s", outPutFD, version, date.withNano(0).toString().split("T")[0]);

        LOG.info(String.format("fileDirStr == %s",fileDirStr));

        File fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                throw new RuntimeException(String.format("create dir fail dir==%s", fileDirStr));
            }
        }

        String filePathExe = fileDirStr + "\\%s-%s-%s-%s.exe";
        try {
            filePathExe = String.format(filePathExe,
                    String.valueOf((int)((Math.random()*9+1)*100000)),
                    version,
                    String.valueOf(ad),
                    date.withNano(0).toString().replaceAll(":", ""));

            LOG.info(String.format("filePathExe == %s",filePathExe));
            return filePathExe;
        } catch (Exception e) {
            LOG.error(String.format("buildNewPackPath fail - version==%s - ad==%s - exception==%s", version, ad, e.getMessage()), e);
        }
        return null;
    }

    public String toJsonString() {
        return JSONObject.toJSONString(packInfo);
    }

}