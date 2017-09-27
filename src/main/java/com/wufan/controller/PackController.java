package com.wufan.controller;

import com.alibaba.fastjson.JSONObject;
import com.wufan.handle.PackTaskCallable;
import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;
import com.wufan.model.output.PackURL;
import com.wufan.service.PackService;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 7cc on 2017/9/7
 */

@RestController
@RequestMapping("/pack")
public class PackController {

    private static final Logger LOG = LoggerFactory.getLogger(PackController.class);

    @Autowired
    private PackService packService;

    /**
     *  通过广告ID和版本打包服务 (直接返回, 等待回调)
     *
     * @param packInfo 打包信息|packInfo
     * @return         打包查询的taskId|回调返回服务器的URL
     */
    @ApiOperation("打包")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ad", value = "广告ID"),
            @ApiImplicitParam(name = "version", value = "版本ID"),
            @ApiImplicitParam(name = "versionPack", value = "版本包(绝对路径)"),
            @ApiImplicitParam(name = "priority", value = "优先打包:1|正常:0"),
            @ApiImplicitParam(name = "callBackUrl", value = "回调url"),
            @ApiImplicitParam(name = "taskId", value = "不填"),
            @ApiImplicitParam(name = "startTime", value = "不填")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "当前打包任务的taskId <br/> callBack 返回 status 成功:0|失败:-1 , taskId, 打好包文件的 url", response = PackOutput.class)
    })
    @PostMapping("/submit")
    public PackOutput submitPack(@RequestBody PackInfo packInfo) throws Exception{
        LOG.info(String.format("request - %s", JSONObject.toJSONString(packInfo)));
        return packService.submitPack(packInfo);
    }

    /**
     * 拿到当前打包任务队列信息
     *
     * @return    打包中的taskId|等待打包的taskId
     */
    @ApiOperation("拿到当前打包任务队列信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "打包中的taskId|等待打包的taskId", response = PackOutput.class)
    })
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
    @ApiOperation("查询task状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", required = true, value = "taskId")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "任务Id的打包状态", response = PackOutput.class)
    })
    @GetMapping("/getPackStatusByTaskId/{taskId}")
    public PackOutput getPackStatusByTaskId(@PathVariable String taskId) throws Exception {
        if(StringUtils.isBlank(taskId)) {
            throw new Exception("error , taskId is blank !!!");
        }
        return packService.getPackStatusByTaskId(taskId);
    }

    @ApiOperation("设置打包等待时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "waitBatPackTime", required = true, value = "打包等待时间ms")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "打包等待时间ms")
    })
    @GetMapping("/setWaitBatPackTime/{waitBatPackTime}")
    public int setWaitBatPackTime(@PathVariable String waitBatPackTime) throws Exception {
        int intWaitBatPackTime = Integer.valueOf(waitBatPackTime);
        if (intWaitBatPackTime < 1 || intWaitBatPackTime > 60000) {
            throw new Exception(String.format("setWaitBatPackTime out of range waitBatPackTime==%s", waitBatPackTime));
        }
        PackTaskCallable.WAITBATPACKTIME = intWaitBatPackTime;
        return PackTaskCallable.WAITBATPACKTIME;
    }


}
