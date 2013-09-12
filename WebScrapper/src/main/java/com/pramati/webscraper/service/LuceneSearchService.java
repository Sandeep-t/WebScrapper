package com.pramati.webscraper.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.delegate.LuceneTaskDelegate;

public class LuceneSearchService {
	
	
	@Autowired
	LuceneTaskDelegate luceneDelegate;
	
	
	public void searchForData(String queryString) throws ParseException, IOException{
		
		List<Document> responseList=luceneDelegate.searchForString(queryString);
		
	}
	
	public void startIndexing(){
		luceneDelegate.startIndexer();
	}

}
