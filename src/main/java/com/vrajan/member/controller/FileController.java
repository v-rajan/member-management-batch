package com.vrajan.member.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vrajan.member.dto.JobParametersMsg;

@RestController
@RequestMapping("/")
public class FileController {

	@Value("${batch.process.dir}")
	private String batchPath;

	@Autowired
	protected CamelContext camelContext;

	@Autowired
	private ProducerTemplate producerTemplate;

	/**
	 * Multiple files to upload
	 * 
	 * @param extraField
	 * @param uploadfiles
	 * @return
	 */
	@RequestMapping(value = "/api/upload/multiplefiles", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam("files") MultipartFile[] uploadfiles) {

		String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
				.filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

		if (StringUtils.isEmpty(uploadedFileName) || uploadfiles.length == 0) 
			return new ResponseEntity<String>("please select files!", HttpStatus.OK);


		String transactionIdentifier;
		
		try {
			/** File will get saved to file system */
			transactionIdentifier = saveUploadedFiles(Arrays.asList(uploadfiles));
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(String.format("Transaction Id : %s", transactionIdentifier), HttpStatus.OK);

	}

	/**
	 * Files will get saved to file system and database
	 * 
	 * @param files
	 * @throws IOException
	 */
	private String saveUploadedFiles(List<MultipartFile> files) throws IOException {

		JobParametersMsg jobParametersMsg = new JobParametersMsg();
		String copyFolder = UUID.randomUUID().toString();

		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			byte[] bytes = file.getBytes();

			// Create directory with random string and copy the files in for processing with
			// conflicting with other transactionIdentifier files.
			File dir = new File(this.batchPath + "/" + copyFolder);
			dir.mkdir();
			Path path = Paths
					.get(this.batchPath + "/" + copyFolder + "/" + file.getOriginalFilename());
			Files.write(path, bytes);

			if (path.toString().contains(".csv"))
				jobParametersMsg.setCsvFile(path.toString());
			else if (path.toString().contains(".xml"))
				jobParametersMsg.setXmlFile(path.toString());

		}

		// Create the JSON file name based on the transactionIdentifier and current date
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String line = Files.readAllLines(Paths.get(jobParametersMsg.getCsvFile())).get(1);
		String transactionIdentifier = line.substring(0, line.indexOf(","));
		String jsonFile = transactionIdentifier + "-" + formatter.format(date) + ".json";

		jobParametersMsg.setJsonFile(this.batchPath + "/" + copyFolder + "/" + jsonFile);
		jobParametersMsg.setTransactionIdentifier(transactionIdentifier);

		Endpoint endpoint = this.camelContext.getEndpoint("seda:registerJobBatch");
		producerTemplate.setDefaultEndpoint(endpoint);
		producerTemplate.sendBody(jobParametersMsg);
		
		return transactionIdentifier;

	}

}
