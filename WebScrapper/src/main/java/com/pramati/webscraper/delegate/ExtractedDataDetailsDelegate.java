package com.pramati.webscraper.delegate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.pramati.webscraper.db.model.ExtractedDataDetails;

public class ExtractedDataDetailsDelegate {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public void insertIntoExtractedDataDetails(ExtractedDataDetails details){
		
		mongoTemplate.save(details);
				
	}
	
	public List<ExtractedDataDetails> getAllRecordDetails(){
				
		return mongoTemplate.findAll(ExtractedDataDetails.class);
	}

}
