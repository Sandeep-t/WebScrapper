package com.pramati.webscraper.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pramati.webscraper.service.WebScrapperService;


/**
 * Main method for webScrapper to start.
 * 
 **/
public final class WebScrapperApp {

	public static void main(String[] args) throws Exception {

		ApplicationContext  ctx = new ClassPathXmlApplicationContext("WebScrapperAppContext.xml");

		WebScrapperService webScraperService = (WebScrapperService) ctx.getBean("webScraperService");
	
		webScraperService.startWebScrapping();
		
	
	

	}

}
