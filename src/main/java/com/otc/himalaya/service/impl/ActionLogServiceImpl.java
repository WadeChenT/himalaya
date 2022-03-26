package com.otc.himalaya.service.impl;

import com.otc.himalaya.aop.ActionLogThreadLocal;
import com.otc.himalaya.entity.ActionLog;
import com.otc.himalaya.repo.ActionLogRepository;
import com.otc.himalaya.security.UserPrincipal;
import com.otc.himalaya.service.ActionLogService;
import com.otc.himalaya.utils.DateUtils;
import com.otc.himalaya.utils.HttpReqResUtil;
import com.otc.himalaya.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ActionLogServiceImpl implements ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private final BinaryOperator<String> mapValueAppender = (v1, v2) -> String.join(", ", v1, v2);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ActionLog save(ActionLog entity) {
        try {
            entity = actionLogRepository.save(entity);
        } catch (Exception e) {
            log.error("opLog Exception", e);
            log.error(entity.toString());
        }
        return entity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequest(HttpServletRequest httpServletRequest,
                           Object body) {
        var actionLog = ActionLogThreadLocal.get();
        actionLog.setHttpMethod(httpServletRequest.getMethod());
        actionLog.setRestUrl(httpServletRequest.getRequestURL().toString());
        actionLog.setReqHeader(JsonUtil.toJsonUnFormattedLog(buildHeadersMap(httpServletRequest)));
        if (Objects.nonNull(body))
            actionLog.setReqBody(JsonUtil.toJsonUnFormattedLog(body));
        actionLog.setIp(HttpReqResUtil.getClientIpAddress());
        actionLog.setCreateAt(DateUtils.getNowLocal());
        actionLog = save(actionLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logResponse(HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse,
                            Object body) {
        var actionLog = ActionLogThreadLocal.get();

        if (StringUtils.isBlank(actionLog.getHttpMethod()))
            actionLog.setHttpMethod(httpServletRequest.getMethod());
        if (StringUtils.isBlank(actionLog.getRestUrl()))
            actionLog.setRestUrl(httpServletRequest.getRequestURL().toString());
        if (StringUtils.isBlank(actionLog.getIp()))
            actionLog.setIp(HttpReqResUtil.getClientIpAddress());
        if (Objects.isNull(actionLog.getCreateAt()))
            actionLog.setCreateAt(DateUtils.getNowLocal());

        actionLog.setResHeader(JsonUtil.toJsonUnFormattedLog(buildHeadersMap(httpServletResponse)));
        if (Objects.nonNull(body)) {
                actionLog.setResBody(JsonUtil.toJsonUnFormattedLog(body));
        }

//        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
//            UserPrincipal op = null;
//            try {
//                op = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (Objects.nonNull(op)) {
//                actionLog.setUserName(op.getUsername());
//                actionLog.setUserId(op.getId());
//            }
//        }
        actionLog = save(actionLog);
    }

    private Map<String, String> buildParametersMap(HttpServletRequest request) {

        return Collections.list(request.getHeaderNames())
                          .stream()
                          .collect(Collectors.toMap(Function.identity(),
                                                    request::getHeader,
                                                    mapValueAppender));
    }

    private Map<String, List<String>> buildHeadersMap(HttpServletRequest request) {

        return Collections.list(request.getHeaderNames())
                          .stream()
                          .collect(Collectors.groupingBy(Function.identity(),
                                                         Collectors.mapping(request::getHeader,
                                                                            Collectors.toList())));
    }

    private Map<String, List<String>> buildHeadersMap(HttpServletResponse response) {

        return response.getHeaderNames()
                       .stream()
                       .collect(Collectors.groupingBy(Function.identity(),
                                                      Collectors.mapping(response::getHeader,
                                                                         Collectors.toList())));
    }
}
