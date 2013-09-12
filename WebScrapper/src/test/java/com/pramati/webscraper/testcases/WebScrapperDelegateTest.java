/**
 * 
 */
package com.pramati.webscraper.testcases;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pramati.webscraper.delegate.WebScrapperDelegate;
import com.pramati.webscraper.dto.Response;

/**
 * @author sandeep-t
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/WebScrapperAppContext.xml" })
public class WebScrapperDelegateTest {

	private static final Logger LOGGER = Logger.getLogger(WebScrapperDelegateTest.class);

	@Autowired
	WebScrapperDelegate webScrapper;

	@Resource(name = "applicationProperties")
	private Properties applicationProperties;


	/**
	 * Test method for
	 * {@link com.pramati.webscraper.delegate.WebScrapperDelegate#processWeblinksinPageData(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */

	@Test
	public void testProcessWeblinksinPageData() throws InterruptedException, ExecutionException, IOException {

		String htmlData = FileUtils.readFileToString(
				        FileUtils.toFile(this.getClass().getResource("/input/FiletoExtraxtHtmlLinks.html")
				    ));
		
		webScrapper.stratResponsePooler();
		
		webScrapper.processWeblinksinPageData(htmlData,applicationProperties.getProperty("constant.url.link"));
		try {
			Thread.sleep(10000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("The Data File got created hence the test Passed ", (new File(applicationProperties.getProperty("web.scrapped.file.name.path"))
						.exists() && new File(applicationProperties.getProperty("web.scrapped.file.name.path")).length() > 0), true);
	}

	/**
	 * Test method for
	 * {@link com.pramati.webscraper.delegate.WebScrapperDelegate#getFutureAsResponse(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetFutureAsResponse() {
		Future<Response> htmlResponse = null;
		try {
			String str = applicationProperties.getProperty("web.scrapper.url");
			htmlResponse = webScrapper.getFutureAsResponse(str);
		}
		catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException occured with the url "
							+ applicationProperties.getProperty("web.scrapper.url"), e);
			htmlResponse = null;
		}
		assertEquals("This test passed so URL seems to be OK ", htmlResponse != null, true);
	}

	/**
	 * Test method for
	 * {@link com.pramati.webscraper.delegate.WebScrapperDelegate#getPageData(java.io.InputStream)}
	 * .
	 * 
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */

	@Test
	public void testGetPageData() throws InterruptedException, ExecutionException, IOException {
		String htmlData = FileUtils.readFileToString(
				        FileUtils.toFile(this.getClass().getResource("/input/FiletoExtraxtHtmlLinks.html")
				    ));
		InputStream dataStream = new ByteArrayInputStream(htmlData.getBytes());
		assertEquals("This test passed hence stream to string conversion is working fine ",
						(webScrapper.getPageData(dataStream) != null || webScrapper.getPageData(dataStream).equals("")),
						true);

	}

}
