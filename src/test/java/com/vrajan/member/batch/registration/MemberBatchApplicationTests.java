package com.vrajan.member.batch.registration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.vrajan.member.dto.JobParametersMsg;
import com.vrajan.member.service.JobLauncherService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MemberBatchApplicationTests {

	@Autowired
	private JobLauncherService jobLauncherService;

	@Autowired
	private Job memberRegistrationJob;

	@Test
	public void testMemberRegistration() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, InterruptedException, IOException, JSONException {

		// Current directory to add to the source and target files
		String path = Paths.get("").toAbsolutePath().toString();

		// Create the JSON file name based on the transactionIdentifier and current date
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String line = Files.readAllLines(Paths.get("src/test/resources/payload/sample001/sample.csv")).get(1);
		String transactionIdentifier = line.substring(0, line.indexOf(","));
		String jsonFile = transactionIdentifier + "-" + formatter.format(date) + ".json";

		JobParametersMsg jobParametersMsg = new JobParametersMsg();
		jobParametersMsg.setCsvFile(path + "/src/test/resources/payload/sample001/sample.csv");
		jobParametersMsg.setXmlFile(path + "/src/test/resources/payload/sample001/sample.xml");
		jobParametersMsg.setJsonFile(path + "/target/" + jsonFile);
		jobParametersMsg.setTransactionIdentifier(transactionIdentifier);

		ExitStatus exitStatus = jobLauncherService.startJob(memberRegistrationJob, jobParametersMsg.toJobParameters());

		Assert.assertEquals(ExitStatus.COMPLETED, exitStatus);

		// Assert generated JSON
		String actual01 = new String(Files.readAllBytes(Paths.get(path + "/target/" + jsonFile)));
		String expected01 = new String(
				Files.readAllBytes(Paths.get(path + "/src/test/resources/payload/sample001/result.json")));
		JSONAssert.assertEquals(actual01, expected01, JSONCompareMode.STRICT);
	}

}
