package com.pramati.webscraper.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.delegate.WebScrapperDelegate;
import com.pramati.webscraper.dto.Response;

public class WebScrapperService {

	@Autowired
	WebScrapperDelegate webScrapper;



	private static final Logger LOGGER = Logger.getLogger(WebScrapperService.class);

	String url;

	public WebScrapperService(String url) {
		this.url = url;
	}

	public void startWebScrapping() throws FileNotFoundException, MalformedURLException, InterruptedException,
					ExecutionException, IOException {

		String urlOfMainPage = url;

		LOGGER.debug("Processing Url  " + urlOfMainPage);

		webScrapper.stratResponsePooler();

		Future<Response> futureResponse = webScrapper.getFutureAsResponse(urlOfMainPage);

		InputStream responseStream = futureResponse.get().getBody();

		final String pageData = webScrapper.getPageData(responseStream);
		
		LOGGER.debug("Constant part of the Weblink "+urlOfMainPage.substring(0, urlOfMainPage.lastIndexOf('/')));

		webScrapper.processWeblinksinPageData(pageData, urlOfMainPage.substring(0, urlOfMainPage.lastIndexOf('/')));

	}

}
