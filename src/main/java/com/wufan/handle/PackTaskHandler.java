package com.wufan.handle;

import com.wufan.actor.FutureResponse;
import com.wufan.model.output.PackURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 7cc on 2017/9/7
 */
public class PackTaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(PackTaskHandler.class);

    public static final ExecutorService executor = Executors.newWorkStealingPool();
    public static final ExecutorService priorityExecutor = Executors.newWorkStealingPool();

    public static void handlePackTaskList(List<Callable<PackURL>> callables) throws Exception {
        executor.invokeAll(callables).stream().map(future -> {
            try {
                // TODO 假设这里等很久, 这里如何超时中断呢 ?
                return future.get();
            }  catch (Exception e) {
                logger.warn(String.format("thread interrupted exception %s", e.getMessage()));
            }
            return null;
        }).filter(x -> x != null).forEach(FutureResponse::receive);
    }

    public static void priorityPackage(Callable<PackURL> callable) throws Exception {
        Future<PackURL> future = priorityExecutor.submit(callable);
        FutureResponse.receive(future.get());
}

}
