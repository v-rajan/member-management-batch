package com.vrajan.member.routes;

import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(CamelSpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JobLauncherRouteTest {


	@Test
	public void jobLauncherRoute() throws Exception {

	}

}
