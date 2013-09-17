package com.pramati.webscraper.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import net.sf.json.JSONException;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.db.model.ExtractedDataDetails;
import com.pramati.webscraper.delegate.WebScrapperDelegate;
import com.pramati.webscraper.utils.AMQPJsonUtil;

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

		//luceneSearch.startIndexing();

		//webScrapper.startDBInsertionPooler();

		//webScrapper.stratHtmlDataPooler();

		//webScrapper.startScrapping(url);

		List<String> searchResultJson = luceneSearch.searchForData("Anti-compaction");
		for(String jsonString:searchResultJson){
			LOGGER.debug("Object to JSON conversion "+ jsonString.toString());
			
			
		}

	}

}
