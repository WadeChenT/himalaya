package com.otc.himalaya.aop;

import com.otc.himalaya.service.ActionLogService;
import com.otc.himalaya.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ActionLogService logService;

    @Pointcut("execution(public * com.otc.himalaya.controller.*.*Controller.*(..))")
    private void pointCut() {}

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void AfterThrowing(Exception e) {
        StringWriter exTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(exTrace));
        var actionLog = ActionLogThreadLocal.get()
                                            .setExceptionTrace(exTrace.toString())
                                            .setCreateAt(DateUtils.getNowLocal())
                                            .setIp("")
                                            .setRestUrl("")
                                            .setHttpMethod("");
        actionLog = logService.save(actionLog);
    }

    /*
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass().getName());
        logger.trace("LogAspect start");
        var actionLog = new AdministratorActionLog();

        ActionLogThreadLocal.set(actionLog);

        if (HttpReqResUtil.getCurrentHttpRequest().isPresent()) {
            var req = HttpReqResUtil.getCurrentHttpRequest().get();
            var header = Collections.list(req.getHeaderNames())
                                    .stream()
                                    .collect(Collectors.toMap(Function.identity(),
                                                              h -> Collections.list(req.getHeaders(h))
                                    ));
            var bodys = Stream.of(pjp.getArgs())
                              .filter(w -> !(w instanceof MultipartFile))
                              .filter(x -> !(x instanceof HttpServletRequest))
                              .filter(y -> !(y instanceof HttpServletResponse))
                              .filter(z -> !(z instanceof UserPrincipal))
                              .collect(Collectors.toList());

            actionLog.setIp(HttpReqResUtil.getClientIpAddress())
                     .setRestUrl(req.getRequestURL().toString())
                     .setReqHeader(JsonUtil.toJsonUnFormattedLog(header))
                     .setReqBody(JsonUtil.toJsonUnFormattedLog(bodys))
                     .setCreateDate(DateUtils.getNowLocal());
            if (Objects.nonNull(req.getUserPrincipal())) {
                var operator = userService.findByLoginName(req.getUserPrincipal().getName());
                actionLog.setOpUserId(operator.getId())
                         .setOpLoginName(operator.getLoginName());
            }
            logger.info(JsonUtil.toJsonLog(actionLog));
            actionLog = logService.save(actionLog);
        }

        Object rtn = pjp.proceed();

        if (Objects.nonNull(rtn) && rtn instanceof ResponseEntity) {
            var res = (ResponseEntity) rtn;
            actionLog.setResHeader(JsonUtil.toJsonUnFormattedLog(res.getHeaders()));

            if (res.getBody() instanceof ByteArrayResource) {
                actionLog.setResBody("{}");
            } else {
                actionLog.setResBody(JsonUtil.toJsonUnFormattedLog(res.getBody()));
            }
            actionLog = logService.save(actionLog);
        }

        logger.trace("LogAspect end");
        return rtn;
    }
    */

}
