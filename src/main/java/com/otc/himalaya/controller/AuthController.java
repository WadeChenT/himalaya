package com.otc.himalaya.controller;

import com.otc.himalaya.controller.dto.ResObject;
import com.otc.himalaya.controller.dto.auth.CreateUserReq;
import com.otc.himalaya.controller.dto.auth.JwtAuthenticationRes;
import com.otc.himalaya.controller.dto.auth.LoginReq;
import com.otc.himalaya.entity.User;
import com.otc.himalaya.exception.HimalayaException;
import com.otc.himalaya.security.JwtTokenProvider;
import com.otc.himalaya.security.UserPrincipal;
import com.otc.himalaya.service.UserService;
import com.otc.himalaya.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Tag(name = "Auth", description = "Auth API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JavaMailSender mailSender;

    private final UserService userService;

    @Operation(method = "POST", description = "User login")
    @PostMapping("/login")
    public ResponseEntity<ResObject> login(@Validated @RequestBody LoginReq loginReq) {
        log.trace("current login:{}", loginReq.toString());

        Authentication authentication;
        try {
            authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(),
                                    loginReq.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();

            throw HimalayaException.occur(HimalayaException.HiOtcErrorEnum.WRONG_EMAIL_OR_PASSWORD);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User thisUser = principal.getUser();

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        //token
        String loginToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateFreshToken(authentication);
        log.trace("current login token:{}, refreshToken:{}", loginToken, refreshToken);
        JwtAuthenticationRes jwtToken = new JwtAuthenticationRes(loginToken, refreshToken);

        //user
//        UserRes userRes = new UserRes(thisUser, company).setCompanyGroupRole(companyGroupRole);


        return ResponseEntity.ok()
                .body(ResObject.of()
                        .addResult("jwt", jwtToken)
                        .addResult("user", thisUser)
                );
    }

    @Operation(method = "POST", description = "reset password")
    @PostMapping(value = "/create")
    public ResponseEntity<ResObject> passwordChange(@RequestBody @Validated CreateUserReq req) throws MessagingException, IOException {
        User user = new User()
                .setName(req.getUserName())
                .setEmail(req.getEmail())
                .setPassword(passwordEncoder.encode(req.getPassword()))
                .setRole(3)
                .setUpdatedAt(DateUtils.getNowLocal())
                .setCreateAt(DateUtils.getNowLocal());

            userService.createNewUsers(user);

        return ResponseEntity.ok()
                    .body(ResObject.of());

    }
}
