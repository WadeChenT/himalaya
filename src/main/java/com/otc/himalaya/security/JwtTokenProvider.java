package com.otc.himalaya.security;

import com.otc.himalaya.bean.ConfigConstant;
import com.otc.himalaya.utils.DateUtils;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private String tokenBuilder(String userUuid, Date startDate, Date expiryDate, TokenType tokenType) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("type", tokenType.getType());

        return Jwts.builder()
                   .setIssuer(ServletUriComponentsBuilder.fromCurrentContextPath()
                                                         .build()
                                                         .toUriString())
                   .setSubject(userUuid)
                   .setIssuedAt(startDate)
                   .setExpiration(expiryDate)
                   .addClaims(extra)
                   .signWith(SignatureAlgorithm.HS512, ConfigConstant.jwtSecret)
                   .compact();
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId());
    }

    public String generateToken(String userUuid) {

        Date expiryDate = new Date(DateUtils.getNowDate()
                                            .getTime() + ConfigConstant.jwtExpirationInMs);

        return tokenBuilder(userUuid,
                            DateUtils.getNowDate(),
                            expiryDate,
                            TokenType.LOGIN);
    }

    public String generateFreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateFreshToken(userPrincipal.getId());
    }

    public String generateFreshToken(String userUuid) {

        Date refreshStartDate = new Date(DateUtils.getNowDate()
                                                  .getTime() + ConfigConstant.jwtExpirationInMs - ConfigConstant.jwtReFreshInMs);
        // 隔日凌晨00:00
        LocalDateTime tomorrowMidnight = LocalDateTime.of(DateUtils.getNowLocal()
                                                                   .toLocalDate()
                                                                   .plusDays(1),
                                                          LocalTime.MIDNIGHT);

        return tokenBuilder(userUuid,
                            refreshStartDate,
                            Date.from(tomorrowMidnight.atZone(ZoneId.systemDefault())
                                                      .toInstant()),
                            TokenType.REFRESH);
    }

    public String getUserIdFromJwt(String token) {
        log.info("get token:{}", token);
        Claims claims = getClaimsFromToken(token);
        return String.valueOf(claims.getSubject());
    }

    public Claims getClaimsFromToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                                 .setSigningKey(ConfigConstant.jwtSecret)
                                 .parseClaimsJws(StringUtils.removeStart(token, "Bearer "));

        if (claims.getBody().getIssuedAt().after(DateUtils.getNowDate()))
            throw new ExpiredJwtException(claims.getHeader(), claims.getBody(), "not valid yet.");

        return claims.getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            getClaimsFromToken(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.startsWithIgnoreCase(bearerToken, "Bearer ")) {
            return StringUtils.remove(bearerToken, "Bearer ");
        }
        return null;
    }
}
