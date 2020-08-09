package com.vrajan.member.dto;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import com.vrajan.member.common.Constants;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Object used invoke the JMS service.
 */
public class JobParametersMsg {

	/** The csv file. */
	@Getter
	@Setter
	String csvFile;

	/** The xml file. */
	@Getter
	@Setter
	String xmlFile;

	/** The json file. */
	@Getter
	@Setter
	String jsonFile;

	/** The transaction identifier. */
	@Getter
	@Setter
	String transactionIdentifier;

	/** The job parameters. */
	@Getter
	@Setter
	JobParameters jobParameters;

	/**
	 * Creates batch job JobParameters.
	 *
	 * @return the job parameters
	 */
	public JobParameters toJobParameters() {
		return new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString(Constants.CSV_FILE, this.getCsvFile())
				.addString(Constants.XML_FILE, this.getXmlFile())
				.addString(Constants.JSON_FILE, this.getJsonFile())
				.addString(Constants.TRANS_ID, this.getTransactionIdentifier())
				.toJobParameters();

	}

}
