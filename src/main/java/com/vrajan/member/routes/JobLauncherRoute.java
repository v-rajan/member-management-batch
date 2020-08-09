package com.vrajan.member.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vrajan.member.dto.JobParametersMsg;
import com.vrajan.member.service.JobLauncherService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Routes to manage the batch jobs. .
 */
@Slf4j
@Component
public class JobLauncherRoute extends RouteBuilder {

	/** The in path. */
	@Value("${batch.process.dir}")
	private String inPath;

	/** The camel vm queue consumer. */
	@Value("${batch.process.queue.consumer}")
	private String consumer;

	/** The job launcher service. */
	@Autowired
	private JobLauncherService jobLauncherService;

	/** The member registration job. */
	@Autowired
	private Job memberRegistrationJob;

	/**
	 * Routes to invoke batch processes.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void configure() throws Exception {

		// JMS to invoke batch jobs through a queue.
		from("seda:registerJobBatch?concurrentConsumers=" + consumer)
				.process(new Processor() {

					public void process(Exchange exchange) throws Exception {

						System.out.println(exchange.getIn().getBody());
						ExitStatus exitStatus = jobLauncherService
								.startJob(memberRegistrationJob,
										((JobParametersMsg) exchange.getIn().getBody())
												.toJobParameters());

						log.info(exitStatus.toString());
					}
				});

	}
}
