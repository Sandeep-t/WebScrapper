package com.pramati.webscraper.testcases;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
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

	private static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		return stringBuilder.toString();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		htmlData = readFile("D:/Test/Test.html");

	}

	@Test
	public void testGrabHTMLLinks() {
		List<HtmlLink> result = linkExtractor.grabHTMLLinks(htmlData);
		assertEquals("The Data File got created hence the test Passed ", result.size(), 205);

	}

}
