package com.vrajan.member.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.vrajan.member.data.TransactionRepository;
import com.vrajan.member.model.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobLauncherService {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private TransactionRepository transactionRepository;

	public ExitStatus startJob(Job job, JobParameters jobParameters) throws JobParametersInvalidException,
			JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {

		// Insert transaction in database for registration grouping.
		Transaction transation = new Transaction();
		transation.setTransactionIdentifier(jobParameters.getString("transactionIdentifier"));
		transactionRepository.save(transation);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start(job.getName());

		JobExecution run = jobLauncher.run(job, jobParameters);

		StringBuffer stringBuffer = new StringBuffer();
		Collection<StepExecution> stepExecutions = run.getStepExecutions();
		stepExecutions.forEach(stepExecution -> {
			stringBuffer.append("step:" + stepExecution.getStepName() + ",");
			stringBuffer.append("readCount:" + stepExecution.getCommitCount() + ",");
			stringBuffer.append("commitCount:" + stepExecution.getCommitCount() + ",");
			stringBuffer.append("writeCount:" + stepExecution.getWriteCount());
		});
		stopWatch.stop();

		ExitStatus exitStatus = run.getExitStatus();

		String returnStr = System.lineSeparator() + "resultCount: " + stringBuffer.toString() + System.lineSeparator()
				+ "exitStatus: " + exitStatus + System.lineSeparator() + "timeInfo: " + stopWatch.prettyPrint();
		log.info(returnStr);

		return exitStatus;
	}

}
