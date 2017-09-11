package com.wufan.util;

import com.wufan.model.entity.PackInfo;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * Created by 7cc on 2017/9/7
 */
public class Validator {

    public static void checkParamIsNull(Object object) throws Exception {
        if (object == null) {
            throw new Exception("param is null");
        }
        if (object instanceof Collection) {
            if (((Collection)object).isEmpty()) {
                throw new Exception("collection is empty");
            }
        }
    }

    public static void checkPackInfo(PackInfo packInfo) throws Exception {
        checkParamIsNull(packInfo);
        String versionPack = packInfo.getVersionPack();
        if(StringUtils.isBlank(versionPack)) {
            throw new Exception("versionPack is blank");
        }
        String callBackUrl = packInfo.getCallBackUrl();
        if (StringUtils.isBlank(callBackUrl)) {
            throw new Exception("callBackUrl is blank");
        }
    }

}
