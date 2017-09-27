package com.wufan.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wufan.util.ConfigurationManager.getProperty;

/**
 * Created by 7cc on 2017/9/12
 */
public class BatCallUtil {

    private static final Logger LOG = LoggerFactory.getLogger(BatCallUtil.class);

    private static final Runtime runtime = Runtime.getRuntime();

    private static final String batExecPre = getProperty("batExecPre");
    private static final String batPath = getProperty("batPath");


    /**
     *
     * @param newPackPath       新文件绝对路径    E:\version\date\round6-version-ad-date.exe
     * @param ad                广告id
     * @param versionPackPath   旧版本包绝对路径
     * @return
     */
    private static String buildCmdStr(String versionPackPath, String newPackPath, long ad){
        return new StringBuilder(batExecPre)
                .append(batPath).append(" ")
                .append(versionPackPath).append(" ")
                .append(ad).append(" ")
                .append(newPackPath).toString();
    }

    // test method
    public static int testbat(){
        Process ps = null;
        try {
            ps = runtime.exec("cmd.exe /C start /b F:\\zzzz.bat 1 2 3");
            ps.waitFor();
            return ps.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static int execBat(String versionPackPath, String newPackPath, long ad) {
        String batExecStr = buildCmdStr(versionPackPath, newPackPath, ad);
        Process ps = null;
        try {
            ps = runtime.exec(batExecStr);
            ps.waitFor();
            return ps.exitValue();
        } catch (Exception e) {
            LOG.error(String.format("execBat fail - batExecStr==%s - exception message==%s", batExecStr, e.getMessage()), e);
        }
        return -1;
//        if (i == 0) {
//            System.out.println("执行完成.") ;
//        } else {
//            System.out.println("执行失败.") ;
//        }
    }
}
