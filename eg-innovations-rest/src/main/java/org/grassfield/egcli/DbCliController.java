package org.grassfield.egcli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.grassfield.egcli.util.DBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbCliController {
	static Logger logger = LoggerFactory.getLogger(DbCliController.class);
	 @Autowired
	 private Environment env;

	/**
	 * @param testName test name like DiskActivity
	 * @param startTime	Date time in 2019-09-14 00:00:00.0 format
	 * @param endTime Date time in 2019-09-14 00:05:00.0 format
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@GetMapping("/rest/v1/data/test/{testName}/{startTime}/{endTime}")
    List<?> findAll(
    		@PathVariable String testName,
    		@PathVariable String startTime,
    		@PathVariable String endTime
    		) throws IOException, InterruptedException {
		logger.info("Received request");
		System.out.println("Received request");
		String cliHome = this.env.getProperty("egcli.home");
		String cliAccount = this.env.getProperty("egcli.account");
		String cliExec = this.env.getProperty("egcli.executable");
		
		Map<String, String> paramMap = new Hashtable<String, String>();
		paramMap.put("-test", testName);
		paramMap.put("-startDate", startTime);
		paramMap.put("-endDate", endTime);
		
		List<String> commandList = new ArrayList<String>();
		commandList.add(cliExec);
		commandList.add("-managerid");
		commandList.add(cliAccount);
		commandList.add("-format");
		commandList.add("csv");
		commandList.add("-filename");
		commandList.add(paramMap.get("-test"));
		for (String key:paramMap.keySet()) {
			commandList.add(key);
			commandList.add(paramMap.get(key));
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandList);
		Process p = processBuilder.start();
		InputStream inputStream = p.getInputStream();
		InputStream errorStream = p.getErrorStream();
		List<String> out = read (inputStream);
		List<String> err = read (errorStream);
		p.waitFor();
		System.out.println(out);
		System.out.println(err);
		Path path = Paths.get(cliHome+"/bin/"+paramMap.get("-test")+".csv");
		List<String> lines = Files.readAllLines(path);
		List<Map<String,String>> restOut = DBUtilities.format(lines);
		return restOut;
    }
	
	@PostMapping("/rest/v1/data/query")
    List<?> query(
    		@RequestBody  Map<String, String> paramMap
    		) throws IOException, InterruptedException {
		logger.info("Received request /rest/v1/data/query");
		System.out.println("Received request");
		String cliHome = this.env.getProperty("egcli.home");
		String cliAccount = this.env.getProperty("egcli.account");
		String cliExec = this.env.getProperty("egcli.executable");
		String cliCsvOutputFolder = this.env.getProperty("egcli.csv.output.folder");
		
		String outputFile = "query-output";
		
		List<String> commandList = new ArrayList<String>();
		commandList.add(cliExec);
		commandList.add("-managerid");
		commandList.add(cliAccount);
		commandList.add("-format");
		commandList.add("csv");
		commandList.add("-filename");
		commandList.add(outputFile);
		commandList.add("-query");
		commandList.add("\""+paramMap.get("query")+"\"");
		
		System.out.println("commandList:"+commandList);
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandList);
		Process p = processBuilder.start();
		InputStream inputStream = p.getInputStream();
		InputStream errorStream = p.getErrorStream();
		List<String> out = read (inputStream);
		List<String> err = read (errorStream);
		p.waitFor();
		System.out.println(out);
		System.out.println(err);
		Path path = Paths.get(cliCsvOutputFolder+"/"+outputFile+".csv");
		List<String> lines = Files.readAllLines(path);
		List<Map<String,String>> restOut = DBUtilities.format(lines);
		return restOut;
	}

	private List<String> read(InputStream inputStream) throws IOException {
		List<String> result = new ArrayList<String>();
		BufferedReader reader = 
                new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while ( (line = reader.readLine()) != null) {
			result.add(line);
		}
		return result;
	}
}
