package com.pramati.webscraper.testcases;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pramati.webscraper.dto.HtmlLink;
import com.pramati.webscraper.utils.HTMLLinkExtractor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/WebScrapperAppContext.xml" })
public class HTMLLinkExtractorTest {

	@Autowired 
	HTMLLinkExtractor linkExtractor;

	private static String htmlData;
	


	@Test
	public void testGrabHTMLLinks() throws IOException {
		htmlData=FileUtils.readFileToString(
				        FileUtils.toFile(this.getClass().getResource("/input/FiletoExtraxtHtmlLinks.html")
				        )
				    );
		List<HtmlLink> result = linkExtractor.grabHTMLLinks(htmlData);
		assertEquals("The Data File got created hence the test Passed ", result.size(), 205);

	}

}
