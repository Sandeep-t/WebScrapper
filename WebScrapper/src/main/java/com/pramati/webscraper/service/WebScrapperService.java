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


	@Autowired
	ExecutorService executorService;

	private static final Logger LOGGER = Logger.getLogger(WebScrapperService.class);

	String url;

	public WebScrapperService(String url) {
		this.url = url;
	}

	public void startWebScrapping() throws FileNotFoundException, MalformedURLException, InterruptedException,
					ExecutionException, IOException {

		

		LOGGER.debug("Processing Url  " + url);

		webScrapper.stratResponsePooler();

		Future<Response> futureResponse = webScrapper.getFutureAsResponse(url);

		InputStream responseStream = futureResponse.get().getBody();

		final String pageData = webScrapper.getPageData(responseStream);
		
		String urlSubstring=url.substring(0, url.lastIndexOf('/'));
		
		LOGGER.debug("Constant part of the Weblink "+urlSubstring);

		webScrapper.processWeblinksinPageData(pageData, urlSubstring);
		
		//executorService.shutdown();
		

	}

}
