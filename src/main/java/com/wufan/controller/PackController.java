package com.wufan.controller;

import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;
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

    /**
     *  通过广告ID和版本打包服务
     *
     * @param packInfo 打包信息|packInfo
     * @return         打包查询的taskId|打包后在服务器的URL
     */
    @PostMapping("/submit")
    public PackOutput packageByVersion(@ModelAttribute PackInfo packInfo) throws Exception{
        // TODO 需要直接刷一个taskId回去 , 还是等到url拿到后响应回去
        return packService.packageByVersion(packInfo);
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



}
