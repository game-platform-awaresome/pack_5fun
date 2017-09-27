package com.wufan.service.impl;

import com.wufan.actor.FutureResponse;
import com.wufan.actor.PackTaskSender;
import com.wufan.handle.PackTaskCallable;
import com.wufan.handle.PackTaskHandler;
import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackOutput;
import com.wufan.model.output.PackStatus;
import com.wufan.model.output.PackURL;
import com.wufan.service.PackService;
import com.wufan.util.Validator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.wufan.model.output.PackStatus.*;

/**
 * Created by 7cc on 2017/9/7
 */
@Service
public class PackServiceImpl implements PackService{

    private PackTaskSender packTaskSender = PackTaskSender.getPackTaskSender();

    @Override
    public PackOutput submitPack(PackInfo packInfo) throws Exception {
        Validator.checkPackInfo(packInfo);
        String taskId = UUID.randomUUID().toString();
        packInfo.setTaskId(taskId);
        new FutureResponse(packInfo.getTaskId(), packInfo.getCallBackUrl());
        // is priority ?
        if (packInfo.getPriority() == 0) {
            PackTaskSender.addPackInfoIntoQueue(packInfo);
        } else if (packInfo.getPriority() == 1) {
            PackTaskSender.addPackInfoIntoPriorityQueue(packInfo);
        }
        return PackOutput.ok(taskId);
    }

    @Override
    public PackOutput packageWaitByVersion(PackInfo packInfo) throws Exception {
        Validator.checkPackInfo(packInfo);
        packInfo.setTaskId(UUID.randomUUID().toString());
        FutureResponse futureResponse = new FutureResponse(packInfo.getTaskId());
        // is priority ?
        if (packInfo.getPriority() == 0) {
            PackTaskSender.addPackInfoIntoQueue(packInfo);
        } else if (packInfo.getPriority() == 1) {
//            PackTaskHandler.priorityPackage(new PackTaskCallable(packInfo));
        }
        PackURL packURL = futureResponse.get();
        return PackOutput.ok(packURL);
    }

    @Override
    public PackOutput getPackingTaskDetail() throws Exception {
        Collection<PackInfo> waitingTasks = getWaitingTasks();
        Collection<PackInfo> runningTasks = getRunningTasks();
        Map<PackStatus, Collection<PackInfo>> map = new HashMap<>();
        map.put(WAITING, waitingTasks);
        map.put(RUNNING, runningTasks);
        return PackOutput.ok(map);
    }

    @Override
    public PackOutput getPackStatusByTaskId(String taskId) throws Exception {
        if (getTaskIdIsExistsByMethod(this::getRunningTasks, taskId)) {
            return PackOutput.ok(RUNNING);
        }
        if (getTaskIdIsExistsByMethod(this::getWaitingTasks, taskId)) {
            return PackOutput.ok(WAITING);
        }
        return PackOutput.ok(UNKNOWN);
    }

    private boolean getTaskIdIsExistsByMethod(PackingTasks packingTasks, String taskId) {
        return packingTasks.getPackingTasks().stream()
                .map(PackInfo::getTaskId)
                .anyMatch(id -> id.equals(taskId));
    }

    private Collection<PackInfo> getWaitingTasks() {
        return packTaskSender.getQueue();
    }

    private Collection<PackInfo> getRunningTasks() {
        List<Callable<PackURL>> runningList = packTaskSender.getRunningList();
        List<PackInfo> runningTasks = Collections.EMPTY_LIST;
        try {
            runningTasks = runningList.stream()
                    .map(callable -> ((PackTaskCallable) callable).getPackInfo())
                    .collect(Collectors.toList());
        } catch (Throwable e){
            e.printStackTrace();
        }
        return runningTasks;
    }


    private interface PackingTasks {
        /**
         *  拿到打包任务 (各种类型 running, waiting, unknown)
         * @return
         */
        Collection<PackInfo> getPackingTasks();
    }
}
