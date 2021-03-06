package com.otc.himalaya.config;

import com.otc.himalaya.controller.dto.ResObject;
import com.otc.himalaya.exception.HimalayaException;
import com.otc.himalaya.security.CustomUserDetailsService;
import com.otc.himalaya.security.JwtAuthenticationFilter;
import com.otc.himalaya.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off

        http.cors()
            .and()
            .csrf()
            .disable()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
                log.error(JsonUtil.toJsonLog(Collections.list(request.getHeaderNames())
                                                        .stream()
                                                        .collect(Collectors.toMap(Function.identity(), h -> Collections.list(request.getHeaders(h))))
                          )
                );
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                response.getWriter()
                        .write(JsonUtil.toJson(
                                        ResObject.of(HimalayaException.occur(HimalayaException.HiOtcErrorEnum.UNAUTHORIZED))
                               )
                        );
            })
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api").permitAll()
            .antMatchers("/version").permitAll()
            .antMatchers("/common/**").permitAll()
            .antMatchers("/auth/*").permitAll()

                .antMatchers("/user").permitAll()

            .anyRequest()
            .authenticated()
//            .accessDecisionManager(accessDecisionManager())
        ;

        //@formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(encoder());
    }

    @Override
    public void configure(WebSecurity web) {
        // web.debug(true);

        web.ignoring()
           .antMatchers("/v3/api-docs")
           .antMatchers("/v3/api-docs/**")
           .antMatchers("/swagger-ui.html")
           .antMatchers("/swagger-ui/**")
           .antMatchers("/himalayaOtcDoc.html");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //??????????????????
                registry.addMapping("/**")
                        //?????????????????????
                        .allowedOriginPatterns("*")
                        //????????????Cookie??????
                        .allowCredentials(true)
                        //?????????????????????(????????????)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                //?????????????????????(????????????)
                //.allowedHeaders("*")
                //????????????????????????????????????????????????????????????????????????????????????
                //.exposedHeaders("Header1", "Header2")
                ;
            }
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
    public AccessDecisionManager accessDecisionManager() {
        //@formatter:off
        // StringBuilder sb = new StringBuilder();
        // sb.append("ROLE_ADMIN > ROLE_USER").append(System.lineSeparator());
        // RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // roleHierarchy.setHierarchy(sb.toString());
        //@formatter:on

        return new AffirmativeBased(
                Arrays.asList(
                        new WebExpressionVoter(),
                        new AuthenticatedVoter()
                        // ,
                        // new RoleHierarchyVoter(roleHierarchy),
                )
        );
    }

}
