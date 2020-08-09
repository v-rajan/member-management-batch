package com.vrajan.member.controller;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)

public class FileControllerTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Value("${batch.process.dir}")
	private String inPath;

	@Test
	public void testFileUpload() throws IOException, InterruptedException {

		// Current directory to add to the source and target files
		String path = Paths.get("").toAbsolutePath().toString();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.add("files", new FileSystemResource(path + "/src/test/resources/payload/sample001/sample.csv"));
		requestMap.add("files", new FileSystemResource(path + "/src/test/resources/payload/sample001/sample.xml"));

		final ResponseEntity<String> exchange = this.testRestTemplate.exchange("/api/upload/multiplefiles",
				HttpMethod.POST, new HttpEntity<>(requestMap, headers), String.class);

		Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
		Assert.assertTrue(exchange.getBody().contains("Transaction Id"));

	}
	
	@Test
	public void testFileUploadNoFiles() throws IOException, InterruptedException {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();

		final ResponseEntity<String> exchange = this.testRestTemplate.exchange("/api/upload/multiplefiles",
				HttpMethod.POST, new HttpEntity<>(requestMap, headers), String.class);

		Assert.assertTrue(exchange.getStatusCode().is2xxSuccessful());
		Assert.assertTrue(exchange.getBody().contains("please select files!"));

	}

}
