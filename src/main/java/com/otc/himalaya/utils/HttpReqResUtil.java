package com.otc.himalaya.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Component
public class HttpReqResUtil {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static String getClientIpAddress() {
        Optional<HttpServletRequest> req = HttpReqResUtil.getCurrentHttpRequest();
        if (req.isPresent()) {
            HttpServletRequest request = req.get();

            for (String header : IP_HEADER_CANDIDATES) {
                String ipList = request.getHeader(header);
                if (StringUtils.isNotBlank(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                    String ip = ipList.split(",")[0];
                    log.debug("getClientIpAddress:{}", ip);
                    return ip;
                }
            }
            return request.getRemoteAddr();
        }
        log.trace("Not called in the context of an HTTP request");
        return "0.0.0.0";
    }

    public static Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                       .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                       .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                       .map(ServletRequestAttributes::getRequest);
    }

    public static String getLocalNameUrl() {
        Optional<HttpServletRequest> req = HttpReqResUtil.getCurrentHttpRequest();
        if (req.isPresent()) {
            HttpServletRequest request = req.get();
            return  request.getLocalName();
        } else {
            return "0.0.0.0";
        }
    }
}
