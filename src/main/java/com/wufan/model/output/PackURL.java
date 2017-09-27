package com.wufan.model.output;

import lombok.Data;


/**
 * Created by 7cc on 2017/9/8
 */

@Data
public class PackURL {

    private int status;
    private String taskId;
    private String cosUrl;
    private String callBackUrl;
}
