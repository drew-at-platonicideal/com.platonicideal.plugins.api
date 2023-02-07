package com.platonicideal.plugins.api.executors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurlExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(CurlExecutor.class);
	
	public String execute(String curl) throws IOException, InterruptedException {
		LOG.debug("Executing {}", curl);
		Process p = null;
    	try {
    		p = Runtime.getRuntime().exec(curl);
			try(InputStream inputStream = p.getInputStream();
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));	
				InputStream errorStream = p.getErrorStream();
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));	) {
				StringBuilder sb = new StringBuilder();
				inputReader.lines()
					.forEach(line -> {
						LOG.debug("curl >"+line);
					    sb.append(line);
					});
				StringBuilder err = new StringBuilder();
				errorReader.lines()
				.forEach(line -> {
					LOG.debug("curl 2>"+line);
					err.append(line);
				});
				
				boolean finish = p.waitFor(30, TimeUnit.SECONDS);
				if(finish) {
					int exitValue = p.exitValue();
					return "exit: " + exitValue + ", result: " + sb.toString() +", error: " + err.toString();
				} else {
					throw new IllegalStateException("Request did not finish sending within 30s");
				}
			}
		} catch (IOException | InterruptedException e) {
			throw e;
		} finally {
			if(p != null) {
				p.destroy();
			}
		}
	}
	
}
