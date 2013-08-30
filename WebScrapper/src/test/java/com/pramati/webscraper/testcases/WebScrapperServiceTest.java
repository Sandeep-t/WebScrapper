package com.pramati.webscraper.testcases;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pramati.webscraper.service.WebScrapperService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/WebScrapperAppContext.xml" })
public class WebScrapperServiceTest {

	@Autowired
	WebScrapperService webScrapperService;

	@Test
	public void testStartWebScrapping() throws FileNotFoundException, MalformedURLException, InterruptedException,
					ExecutionException, IOException {

		webScrapperService.startWebScrapping();
		Thread.sleep(10000);
		assertEquals("The Data File got created hence the test Passed ", (new File("D:/Test/WebScrappedData1.html")
						.exists() && new File("D:/Test/WebScrappedData1.html").length() > 0), true);
	}

}
