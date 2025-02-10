package com.cpplab;

import com.cpplab.domain.community.config.PostMetricConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@SpringBootApplication
@Import({PostMetricConfig.class})
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing
public class CpplabApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpplabApplication.class, args);
	}

	// HTTP 요청과 응답 과거 기록을 확인
	@Bean
	public InMemoryHttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}

}
