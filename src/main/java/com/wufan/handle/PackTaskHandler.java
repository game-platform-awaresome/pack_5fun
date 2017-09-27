package com.wufan.handle;

import com.wufan.actor.FutureResponse;
import com.wufan.model.output.PackURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by 7cc on 2017/9/7
 */
public class PackTaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(PackTaskHandler.class);

    public static final ExecutorService executor = Executors.newWorkStealingPool();
    public static final ExecutorService priorityExecutor = Executors.newWorkStealingPool();



    public static void handlePackTaskList(List<Callable<PackURL>> callables) throws Exception {
        logger.info(String.format("handlePackTaskList task size = %s", callables.size()));
        executor.invokeAll(callables).stream().map(future -> {
            try {
                return future.get();
            }  catch (Exception e) {
                logger.warn(String.format("thread interrupted exception %s", e.getMessage()), e);
            }
            return null;
        }).filter(x -> x != null).forEach(FutureResponse::receiveCallBack);
    }

    public static class MyRejected implements RejectedExecutionHandler {


        public MyRejected(){
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.warn("当前被拒绝任务为：" + r.toString());

        }

    }


}
