package com.wufan.handle;

import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackURL;
import java.util.concurrent.Callable;

/**
 * 调用c++脚本,返回taskId 和 url
 *
 * Created by 7cc on 2017/9/9
 */
public class PackTaskCallable implements Callable<PackURL> {
    private PackInfo packInfo;

    public PackTaskCallable(PackInfo packInfo){
        this.packInfo = packInfo;
    }

    public PackInfo getPackInfo(){
        return packInfo;
    }

    @Override
    public PackURL call() throws Exception {
        //TODO 调用c++脚本,返回taskId|url
//            Thread.sleep(7000);
        PackURL packURL = new PackURL();
        packURL.setTaskId(packInfo.getTaskId());
        packURL.setCosUrl(packInfo.getAd() + "  -  返回的Url");
        return packURL;
    }
}