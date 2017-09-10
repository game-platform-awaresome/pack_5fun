package com.wufan.handle;

import com.wufan.model.output.PackOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 7cc on 2017/9/10
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public PackOutput jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        LOG.error(String.format("GlobalException - %s", e.getMessage()));
        return PackOutput.error(e.getMessage());
    }

}
