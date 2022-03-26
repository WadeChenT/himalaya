package com.otc.himalaya.security;

import com.otc.himalaya.controller.dto.ResObject;
import com.otc.himalaya.exception.HimalayaException;
import com.otc.himalaya.utils.JsonUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Lazy(false)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static LinkedHashSet<String> logoutList = new LinkedHashSet<>();
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public static void putBlackList(@NotNull String token) {
        logoutList.add(token);
    }

    @Scheduled(fixedDelay = 5000,
               initialDelay = 5000)
    public void logoutListMaintainer() {
        if (!CollectionUtils.isEmpty(logoutList)) {

            for (String token : logoutList) {
                try {
                    tokenProvider.validateToken(token);
                } catch (Exception ex) {
                    logoutList.remove(token);
                }
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = tokenProvider.getJwtFromRequest(request);

            if (StringUtils.isNotBlank(jwt) && tokenProvider.validateToken(jwt)) {
                String userUuid = tokenProvider.getUserIdFromJwt(jwt);

                UserDetails userPrincipal = customUserDetailsService.loadUserById(userUuid);

                // 不可以拿refreshToken來操作api
                String tokenType = Optional.ofNullable(tokenProvider.getClaimsFromToken(jwt)
                                                                    .get("type"))
                                           .orElseThrow(() -> HimalayaException.occur(HimalayaException.HiOtcErrorEnum.TOKEN_ERROR))
                                           .toString();
                if (TokenType.isFreshToken(tokenType)) {
                    throw HimalayaException.occur(HimalayaException.HiOtcErrorEnum.TOKEN_ERROR);
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                                     .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(
                            JsonUtil.toJson(
                                    ResObject.of(HimalayaException.occur(HimalayaException.HiOtcErrorEnum.TOKEN_EXPIRED_OR_NOT_VALID_YET))
                            )
                    );
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(
                            JsonUtil.toJson(
                                    ResObject.of(HimalayaException.occur(HimalayaException.HiOtcErrorEnum.TOKEN_ERROR))
                            )
                    );
        }
    }
}
