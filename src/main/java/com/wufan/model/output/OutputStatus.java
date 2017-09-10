package com.wufan.model.output;

/**
 * Created by 7cc on 2017/9/7
 */
public enum OutputStatus {
    SUCCESS("package success"),
    ERROR("service error"),
    THIRDPARTYERROR("depend service error"),
    PARAMERROR("param error")
    ;

    public final String info;

    private OutputStatus(String info) {
        this.info = info;
    }
}
