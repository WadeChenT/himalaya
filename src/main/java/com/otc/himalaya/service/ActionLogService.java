package com.otc.himalaya.service;


import com.otc.himalaya.entity.ActionLog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionLogService {
    ActionLog save(ActionLog log);

    void logRequest(HttpServletRequest httpServletRequest,
                    Object body);

    void logResponse(HttpServletRequest httpServletRequest,
                     HttpServletResponse httpServletResponse,
                     Object body);
}
