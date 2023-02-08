package com.platonicideal.plugins.api.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CurlParserTest {

	CurlParser parser = new CurlParser();
	
	@Test
	public void testParseSimple() {
		String curl = "curl --location --request POST http://209.127.3.178:8080/v1/submission";
		assertEquals(Arrays.asList("curl", "--location", "--request", "POST", "http://209.127.3.178:8080/v1/submission"), parser.parse(curl));
	}
	
	@Test
	public void testParseWithData() {
		String curl = "curl --location --data-raw '{\"message\":{\"from\":\"Confirmation <confirmation@memberperklist.com>\",\"subject\":\"Sweepstakes offer: Confirm your Sweepstakes entry\"}}' --request POST http://209.127.3.178:8080/v1/submission";
		assertEquals(Arrays.asList("curl", "--location", "--data-raw", "'{\"message\":{\"from\":\"Confirmation <confirmation@memberperklist.com>\",\"subject\":\"Sweepstakes offer: Confirm your Sweepstakes entry\"}}'", "--request", "POST", "http://209.127.3.178:8080/v1/submission"), parser.parse(curl));
	}
	
}
