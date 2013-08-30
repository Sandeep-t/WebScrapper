package com.pramati.webscraper.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.delegate.WebScrapperDelegate;
import com.pramati.webscraper.dto.Response;
import com.pramati.webscraper.executors.ThreadExecutor;

public class WebScrapperService {

	@Autowired
	WebScrapperDelegate webScrapper;

	@Autowired
	ThreadExecutor executor;

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

		webScrapper.processWeblinksinPageData(pageData, urlOfMainPage.substring(0, urlOfMainPage.lastIndexOf('/')));

	}

}
