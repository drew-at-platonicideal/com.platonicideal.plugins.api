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
		String[] curlCommands = curl.split(" ");
		ProcessBuilder pb = new ProcessBuilder(curlCommands);
		pb.redirectErrorStream(true);
		Process p = null;
    	try {
    		p = pb.start();
			try(InputStream inputStream = p.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));	) {
				StringBuilder sb = new StringBuilder();
				read.lines()
					.forEach(line -> {
						LOG.debug("curl>"+line);
					     sb.append(line);
					});
				
				boolean finish = p.waitFor(30, TimeUnit.SECONDS);
				if(finish) {
					int exitValue = p.exitValue();
					return "exit: " + exitValue + ", result: " + sb.toString();
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
