package com.wufan.model.output;

import lombok.Data;

import static com.wufan.model.output.OutputStatus.ERROR;
import static com.wufan.model.output.OutputStatus.SUCCESS;

/**
 * Created by 7cc on 2017/9/7
 */
@Data
public class PackOutput {
    private OutputStatus status;
    private String info;
    private Object data;

    public static PackOutput ok(Object data){
        PackOutput packOutput = new PackOutput();
        packOutput.status = SUCCESS;
        packOutput.info = SUCCESS.info;
        packOutput.data = data;
        return packOutput;
    }

    public static PackOutput error(String exceptionMsg){
        PackOutput packOutput = new PackOutput();
        packOutput.status = ERROR;
        packOutput.info = exceptionMsg;
        packOutput.data = null;
        return packOutput;
    }

}
