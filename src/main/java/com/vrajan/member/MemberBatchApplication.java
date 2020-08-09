package com.vrajan.member;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MemberBatchApplication {

	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			public void beforeApplicationStart(CamelContext context) {
				context.setUseMDCLogging(true);
			}

			public void afterApplicationStart(CamelContext camelContext) {
				// noop
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MemberBatchApplication.class, args);
	}

}
