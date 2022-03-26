package com.otc.himalaya.exception;

import com.otc.himalaya.aop.ActionLogThreadLocal;
import com.otc.himalaya.controller.dto.ResObject;
import com.otc.himalaya.service.ActionLogService;
import com.otc.himalaya.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExHandler extends ResponseEntityExceptionHandler {

    protected static String ERROR_MSG_TITLE = "Tcs portal Server Error:";
    private final ActionLogService logService;

    @ResponseBody
    @ExceptionHandler(HimalayaException.class)
    public ResponseEntity<ResObject> tcsExceptionHandling(HttpServletRequest req,
                                                          HttpServletResponse res,
                                                          HimalayaException e) {
//        log.error(ERROR_MSG_TITLE, e);

        ResponseEntity<ResObject> resEntity = ResponseEntity.ok()
                                                            .body(ResObject.of(e));

        return getResObjectResponseEntity(resEntity);
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResObject> tcsExceptionHandling(HttpServletRequest req,
                                                          HttpServletResponse res,
                                                          RuntimeException e) {
//        log.error(ERROR_MSG_TITLE, e);

        ResponseEntity<ResObject> resEntity = ResponseEntity.badRequest()
                                                            .body(ResObject.of(e));

        return getResObjectResponseEntity(resEntity);
    }

    private ResponseEntity<ResObject> getResObjectResponseEntity(ResponseEntity<ResObject> resEntity) {

        saveActionLog(resEntity);

        return resEntity;
    }

    private void saveActionLog(ResponseEntity<ResObject> resEntity) {
        var actionLog = ActionLogThreadLocal.get()
                .setResHeader(JsonUtil.toJsonUnFormattedLog(resEntity.getHeaders()))
                .setResBody(JsonUtil.toJsonUnFormattedLog(resEntity.getBody()));

        logService.save(actionLog);
    }
}
