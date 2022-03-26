package com.otc.himalaya;

import com.otc.himalaya.bean.ConfigConstant;
import com.otc.himalaya.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@SpringBootApplication
public class HimalayaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HimalayaApplication.class, args);
	}

	@Bean
	@DependsOn("configConstant")
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(ConfigConstant.mailHost);
		mailSender.setPort(ConfigConstant.mailPort);

		mailSender.setUsername(ConfigConstant.username);
		mailSender.setPassword(ConfigConstant.mailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", ConfigConstant.mailSmtpAuth);
		props.put("mail.smtp.starttls.enable", ConfigConstant.startTlsEnable);
		props.put("mail.smtp.starttls.required", ConfigConstant.startTlsRequired);
		props.put("mail.debug", "true");

		return mailSender;
	}

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludeClientInfo(true);
		filter.setIncludeHeaders(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		return filter;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		return builder
				.interceptors((request, body, execution) -> {
					log.info("RestTemplate Request URI: {}", request.getURI());
					log.info("RestTemplate Request Method: {}", request.getMethod());
					log.info("RestTemplate Headers: {}", request.getHeaders());
					log.info("RestTemplate Request Body: {}", JsonUtil.toJsonLog(body));
					return execution.execute(request, body);
				})
				.setConnectTimeout(Duration.ofMillis(20000))
				.setReadTimeout(Duration.ofMillis(20000))
				.build();
	}

}
