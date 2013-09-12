package com.pramati.webscraper.service;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.db.model.ExtractedDataDetails;
import com.pramati.webscraper.delegate.ExtractedDataDetailsDelegate;
import com.pramati.webscraper.delegate.LuceneTaskDelegate;

public class WebScrapperDbService {
	
	
	private static final Logger LOGGER= Logger.getLogger(WebScrapperDbService.class);
	
	@Autowired
	ExtractedDataDetailsDelegate detailsDelegate;
	
	@Autowired
	LuceneTaskDelegate lucHelper;
	
	public void insertExtractedData(ExtractedDataDetails detail) throws IOException, InterruptedException{
		detailsDelegate.insertIntoExtractedDataDetails(detail);
		lucHelper.addTodataDetailsQueue(detail);
		
	}
	
	
	public List<ExtractedDataDetails> getAllRecords(){
		return detailsDelegate.getAllRecordDetails();
		
	}
	
	
	
	

}
