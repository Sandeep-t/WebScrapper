package com.pramati.webscraper.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.delegate.WebScrapperDelegate;

public class WebScrapperService {

	@Autowired
	WebScrapperDelegate webScrapper;

	@Autowired
	ExecutorService executorService;

	@Autowired
	LuceneSearchService luceneSearch;

	@Autowired
	WebScrapperDbService dbService;

	private static final Logger LOGGER = Logger.getLogger(WebScrapperService.class);

	String url;

	public WebScrapperService(String url) {
		this.url = url;
	}

	public void startWebScrapping() throws FileNotFoundException, MalformedURLException, InterruptedException,
					ExecutionException, IOException, ParseException, JSONException {

		LOGGER.debug("Processing Url  " + url);

		luceneSearch.startIndexing();

		webScrapper.startDBInsertionPooler();

		webScrapper.stratHtmlDataPooler();

		webScrapper.startScrapping(url);

		List<JSONObject> seacrchResultJson = luceneSearch.searchForData("Anti-compaction");

	}

}
