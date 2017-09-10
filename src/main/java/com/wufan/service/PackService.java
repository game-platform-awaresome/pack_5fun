package com.wufan.service;

import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;

/**
 * Created by 7cc on 2017/9/7
 */
public interface PackService {
    PackOutput packageByVersion(PackInfo packInfo) throws Exception;
    PackOutput getPackingTaskDetail() throws Exception;
    PackOutput getPackStatusByTaskId(String taskId) throws Exception;
}
