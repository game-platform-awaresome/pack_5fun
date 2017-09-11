package com.wufan.service;

import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Created by 7cc on 2017/9/7
 */
public interface PackService {
    PackOutput submitPack(PackInfo packInfo) throws Exception;
    PackOutput packageWaitByVersion(PackInfo packInfo) throws Exception;
    PackOutput getPackingTaskDetail() throws Exception;
    PackOutput getPackStatusByTaskId(String taskId) throws Exception;
}
