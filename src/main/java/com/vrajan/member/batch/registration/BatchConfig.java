package com.vrajan.member.batch.registration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.vrajan.member.common.Constants;
import com.vrajan.member.model.Registration;

/**
 * 
 * The registration batch job. CSV,XML --> Database --> JSON
 * 
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

	/** The job builder factory. */
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	/** The data source. */
	@Autowired
	public DataSource dataSource;

	/** The entity manager factory. */
	@Autowired
	EntityManagerFactory emf;

	/**
	 * Task executor.
	 *
	 * @return the task executor
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Constants.CORE_POOL_SIZE);
		executor.setMaxPoolSize(Constants.MAX_POOL_SIZE);
		return executor;
	}

	/**
	 * Registration job.
	 *
	 * @param splitFlow             the split flow
	 * @param registrationMergeStep the registration merge step
	 * @param registrationListener  the registration listener
	 * @return the job
	 */
	@Bean
	public Job registrationJob(Flow splitFlow, Step registrationMergeStep,
			JobExecutionListener registrationListener) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return jobBuilderFactory
				.get(funcName)
				.listener(registrationListener)
				.start(splitFlow)
				.next(registrationMergeStep)
				.end()
				.build();
	}

	/**
	 * Split flow to run XML and CSV extraction processes in parallel
	 *
	 * @param registrationCsvFlow the registration CSV flow
	 * @param registrationXmlFlow the registration XML flow
	 * @return the flow
	 */
	@Bean
	public Flow splitFlow(Flow registrationCsvFlow, Flow registrationXmlFlow) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new FlowBuilder<SimpleFlow>(funcName)
				.split(new SimpleAsyncTaskExecutor())
				.add(registrationCsvFlow, registrationXmlFlow)
				.build();
	}

	/**
	 * Extract the data from the database and write to the JSON file.
	 *
	 * @param registrationMergeReader the registration merge reader
	 * @param registrationMergeWriter the registration merge writer
	 * @return the step
	 */
	@Bean
	public Step registrationMergeStep(ItemReader<Registration> registrationMergeReader,
			ItemWriter<Registration> registrationMergeWriter) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return stepBuilderFactory
				.get(funcName)
				.<Registration, Registration>chunk(Constants.CHUNNK_SIZE)
				.reader(registrationMergeReader)
				.writer(registrationMergeWriter)
				.build();
	}

	/**
	 * Writer to write the final result to JSON.
	 *
	 * @param jsonFile the JSON file
	 * @return the JSON file item writer
	 */
	@Bean
	@StepScope
	public JsonFileItemWriter<Registration> registrationMergeWriter(
			@Value("#{jobParameters['jsonFile']}") String jsonFile) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new JsonFileItemWriterBuilder<Registration>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<Registration>())
				.resource(new FileSystemResource(jsonFile))
				.name(funcName)
				.build();
	}

	/**
	 * Reader to extract data from database.
	 *
	 * @return the JDBC cursor item reader
	 * @throws Exception the exception
	 */
	@Bean
	@StepScope
	public JdbcCursorItemReader<Registration> registrationMergeReader(
			@Value("#{jobParameters['transactionIdentifier']}") String transactionIdentifier) throws Exception {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		String SQL = String.format("SELECT id, employerabn, employer_names, fund_identifier, transaction_identifier "
				+ "FROM registration WHERE  transaction_fk = '%s' ORDER BY fund_identifier, employerabn ",
				transactionIdentifier);

		return new JdbcCursorItemReaderBuilder<Registration>()
				.dataSource(dataSource)
				.name(funcName)
				.sql(SQL)
				.rowMapper(new MergeDbRowMapper())
				.build();

	}

	/**
	 * Writer to add the data read from the files to the database.
	 *
	 * @return the JPA item writer
	 */
	@Bean
	public JpaItemWriter<com.vrajan.member.model.Registration> registrationWriter() {

		return new JpaItemWriterBuilder<com.vrajan.member.model.Registration>()
				.entityManagerFactory(emf)
				.build();
	}

	/**
	 * Job end listener to perform post job activities.
	 *
	 * @return the job execution listener
	 */
	@Bean
	public JobExecutionListener registrationListener() {
		return new JobEndListener();
	}

	/**
	 * Write date from the CSV file to the database.
	 *
	 * @param registrationCsvReader the registration csv reader
	 * @param registrationWriter    the registration writer
	 * @return the step
	 */
	@Bean
	public Step registrationCsvStep(ItemReader<com.vrajan.member.model.Registration> registrationCsvReader,
			JpaItemWriter<com.vrajan.member.model.Registration> registrationWriter) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return stepBuilderFactory
				.get(funcName).<com.vrajan.member.model.Registration, com.vrajan.member.model.Registration>chunk(
						Constants.CHUNNK_SIZE)
				.reader(registrationCsvReader)
				.writer(registrationWriter)
				.taskExecutor(taskExecutor())
				.build();
	}

	/**
	 * CSV extraction flow.
	 *
	 * @param registrationCsvStep the registration CSV step
	 * @return the flow
	 */
	@Bean
	public Flow registrationCsvFlow(Step registrationCsvStep) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new FlowBuilder<SimpleFlow>(funcName)
				.from(registrationCsvStep)
				.build();

	}

	/**
	 * Reader to read data from the CSV file.
	 *
	 * @param csvFile the CSV file
	 * @return the flat file item reader
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<com.vrajan.member.model.Registration> registrationCsvReader(
			@Value("#{jobParameters['csvFile']}") String csvFile) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new FlatFileItemReaderBuilder<com.vrajan.member.model.Registration>()
				.name(funcName)
				.resource(new FileSystemResource(csvFile)).linesToSkip(1)
				.delimited()
				.names(new String[] { "transactionIdentifier", "employerNames", "employerABN", "fundIdentifier" })
				.fieldSetMapper(new CsvFieldSetMapper())
				.build();
	}

	/**
	 * XML extraction flow.
	 *
	 * @param registrationXmlStep the registration XML step
	 * @return the flow
	 */
	@Bean
	public Flow registrationXmlFlow(Step registrationXmlStep) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new FlowBuilder<SimpleFlow>(funcName)
				.from(registrationXmlStep)
				.build();

	}

	/**
	 * Reader to read data from the XML file.
	 *
	 * @param xmlFile the XML file
	 * @return the flat file item reader
	 */
	@Bean
	@StepScope
	public StaxEventItemReader<Registration> registrationXmlReader(
			@Value("#{jobParameters['xmlFile']}") String xmlFile, Jaxb2Marshaller jaxb2Marshaller) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return new StaxEventItemReaderBuilder<Registration>()
				.name(funcName)
				.resource(new FileSystemResource(xmlFile))
				.addFragmentRootElements("Registration")
				.unmarshaller(jaxb2Marshaller)
				.build();
	}

	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] {
				Registration.class
		});

		return marshaller;
	}

	/**
	 * Write date from the XML file to the database.
	 *
	 * @param registrationXmlReader the registration XML reader
	 * @param registrationWriter    the registration writer
	 * @return the step
	 */
	@Bean
	public Step registrationXmlStep(
			StaxEventItemReader<com.vrajan.member.model.Registration> registrationXmlReader,
			JpaItemWriter<com.vrajan.member.model.Registration> registrationWriter) {

		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();

		return stepBuilderFactory
				.get(funcName).<com.vrajan.member.model.Registration, com.vrajan.member.model.Registration>chunk(
						Constants.CHUNNK_SIZE)
				.reader(registrationXmlReader)
				.processor(new XmlItemProcessor())
				.writer(registrationWriter)
				.build();
	}

}
