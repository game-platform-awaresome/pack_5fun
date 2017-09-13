package com.wufan.controller;

import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;
import com.wufan.model.output.PackURL;
import com.wufan.service.PackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by 7cc on 2017/9/7
 */

@RestController
@RequestMapping("/pack")
public class PackController {

    @Autowired
    private PackService packService;

    private PackURL packURL;

    /**
     *  通过广告ID和版本打包服务 (直接返回, 等待回调)
     *
     * @param packInfo 打包信息|packInfo
     * @return         打包查询的taskId|回调返回服务器的URL
     */
    @PostMapping("/submit")
    public PackOutput submitPack(@ModelAttribute PackInfo packInfo) throws Exception{
        return packService.submitPack(packInfo);
    }

    /**
     *  通过广告ID和版本打包服务 (等待响应)
     *  TODO 可删
     * @param packInfo 打包信息|packInfo
     * @return         打包查询的taskId|打包后在服务器的URL
     */
    @PostMapping("/submitWait")
    public PackOutput packageWaitByVersion(@ModelAttribute PackInfo packInfo) throws Exception{
        return packService.packageWaitByVersion(packInfo);
    }

    /**
     * 拿到当前打包任务队列信息
     *
     * @return    打包中的taskId|等待打包的taskId
     */
    @GetMapping("/getPackingTaskDetail")
    public PackOutput getPackingTaskDetail() throws Exception {
        return packService.getPackingTaskDetail();
    }

    /**
     * 通过打包任务Id拿到打包状态
     *
     * @param taskId    打包任务Id|String
     * @return          任务Id的打包状态
     */
    @GetMapping("/getPackStatusByTaskId/{taskId}")
    public PackOutput getPackStatusByTaskId(@PathVariable String taskId) throws Exception {
        return packService.getPackStatusByTaskId(taskId);
    }


    @GetMapping("/test")
    public void test(@ModelAttribute PackURL packURL) throws Exception {
        this.packURL = packURL;
    }

    @GetMapping("/getTest")
    public PackOutput getTest() throws Exception {
        return PackOutput.ok(packURL);
    }

}
