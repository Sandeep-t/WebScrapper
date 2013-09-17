/**
 * 
 */
package com.pramati.webscraper.delegate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.webscraper.db.model.ExtractedDataDetails;
import com.pramati.webscraper.dto.HtmlLink;
import com.pramati.webscraper.dto.Request;
import com.pramati.webscraper.dto.Response;
import com.pramati.webscraper.service.WebScrapperDbService;
import com.pramati.webscraper.utils.HTMLLinkExtractor;

/**
 * @author sandeep-t Util Class for doing operations like url reading, url
 *         extraction and data extraction.
 * 
 */

public class WebScrapperDelegate {

	private static final Logger LOGGER = Logger.getLogger(WebScrapperDelegate.class);

	@Autowired
	HTMLLinkExtractor extractor;

	@Autowired
	ExecutorService executorService;

	@Autowired
	WebScrapperDbService dbService;

	@Autowired
	LuceneTaskDelegate helper;

	//private String destFilePathWithName;


	private BlockingQueue<Future<Response>> childFutureList = new LinkedBlockingQueue<Future<Response>>();

	public void addToList(Future<Response> response) {
		childFutureList.add(response);

	}

	private Map<String, String> furtherProcesshtmlLinks = new HashMap<String, String>();
	private Map<String, String> doNotProcesshtmlLinks = new HashMap<String, String>();

	private Map<String, String> htmlLinkTextMap = new HashMap<String, String>();

	private BlockingQueue<String> nextPageDataQueue = new LinkedBlockingQueue<String>();

	public WebScrapperDelegate() {

		furtherProcesshtmlLinks.put("Earlier messages", null);
		furtherProcesshtmlLinks.put("Later messages", null);
		furtherProcesshtmlLinks.put("Thread", null);
		doNotProcesshtmlLinks.put("Later messages", null);
		doNotProcesshtmlLinks.put("Thread", null);

	}

	/**
	 * This method will take html data in form of String and will return a list
	 * of weblinks embedded in thathtml data
	 * 
	 * @param htmlData
	 * @return list of webaddress
	 * @throws InterruptedException
	 */
	public void processWeblinksinPageData(String htmlData, String webLink) throws InterruptedException {
		/**
		 * Replace all one or more space characters with " "
		 * 
		 */
		String updatedhtmlData = htmlData.replaceAll("\\s+", " ");

		final List<HtmlLink> links = extractor.grabHTMLLinks(updatedhtmlData);

		for (HtmlLink link : links) {

			LOGGER.debug("Processing link  " + link.getLink());

			StringBuilder stbr = new StringBuilder();

			stbr.append(webLink).append("/").append(link.getLink());

			if (!furtherProcesshtmlLinks.keySet().contains(link.getLinkText())) {

				LOGGER.debug("Link prepared finally " + stbr);
				try {
					final Future<Response> response = getFutureAsResponse(stbr.toString());
					childFutureList.add(response);
				}
				catch (MalformedURLException e1) {
					LOGGER.error("MalformedURLException occured while parsing the URL " + stbr, e1);
				}
			}
			else {
				StringBuilder stbro = new StringBuilder();

				stbro.append(webLink).append("/").append(link.getLink());

				if (doNotProcesshtmlLinks.keySet().contains(link.getLinkText())) {

					LOGGER.debug("Not processing the link  " + stbro + " as it is not required ");
				}
				else {
					if (htmlLinkTextMap.get(stbro.toString()) == null) {
						LOGGER.debug("PROCESSING Earlier Message  " + stbro);
						htmlLinkTextMap.put(stbro.toString(), link.getLink());
						nextPageDataQueue.put(stbro.toString());
					}
					else {
						LOGGER.debug("Weblink" + stbro + " already insered into HashMap ");
					}
				}

			}

		}

	}

	/**
	 * This function serves the purpose of hitting the the given URL and get the
	 * response out of it. If in case any exception occurs the exception will be
	 * thrown up and the system is going to halt.
	 * 
	 * @param urlOfMainPage
	 * @return Future as the response
	 * @throws Exception
	 */
	public Future<Response> getFutureAsResponse(String urlString) throws MalformedURLException {
		try {
			LOGGER.debug("Processing url " + urlString);
			URL url = new URL(urlString);
			Request task = new Request(url);
			return executorService.submit(task);
		}
		catch (MalformedURLException e1) {
			LOGGER.error("MalformedURLException occured while parsing the URL " + urlString, e1);
			throw new MalformedURLException("MalformedURLException occured while parsing the URL " + urlString);
		}
	}

	/**
	 * 
	 * This function will be used to get the html response after parsing the
	 * data from future passed to the function.
	 * 
	 * @param responseList
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public String getPageData(final InputStream body) throws InterruptedException, ExecutionException, IOException {

		final BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(body));

		String output;

		StringBuilder fileContent = new StringBuilder();

		try {
			while ((output = responseBuffer.readLine()) != null) {
				fileContent.append(output);
			}
		}
		catch (IOException e) {
			LOGGER.error("Exception occured while reading data from Response", e);
			throw new IOException("Exception occured while reading data from Response", e);

		}
		finally {
			responseBuffer.close();
		}

		return fileContent.toString();
	}

	/**
	 * A pooler that will keep pooling for the data in childFutureList queue and
	 * will write the data received into a file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void startDBInsertionPooler() throws FileNotFoundException {

		Runnable pooler = new Runnable() {

			@Override
			public void run() {

				while (!Thread.interrupted()) {

					Future<Response> future;

					while ((future = childFutureList.poll()) != null) {

						Response response = null;
						URL url = null;
						int responseCode = 0;
						try {
							response = future.get();
							url = response.getUrl();
							responseCode = response.getResponseCode();
							LOGGER.debug("Processing the response of the URL" + response.getUrl());

							InputStream body = response.getBody();
							ExtractedDataDetails details = new ExtractedDataDetails(url.toString(), responseCode, IOUtils.toString(body), new Date(), "WebScrapper");

							dbService.insertExtractedData(details);

						}
						catch (IOException cause) {
							LOGGER.error("IOException occred while writing file for the URL " + url, cause);
						}
						catch (InterruptedException ie) {
							LOGGER.error("InterruptedException occured while processing the Request " + url + "  "
											+ "with status code " + responseCode, ie);

						}
						catch (ExecutionException ee) {

							if (ee.getCause() instanceof FileNotFoundException) {

								LOGGER.error("FileNotFoundException occured while processing the Request " + url + "  "
												+ "with status code " + responseCode, ee);

							}
							else {
								LOGGER.error("ExecutionException occured while processing the Request " + url + "  "
												+ "with status code " + responseCode, ee);
							}

						}
					}
				}
			}
		};

		executorService.execute(pooler);
	}

	public void stratHtmlDataPooler() throws FileNotFoundException {

		Runnable pooler = new Runnable() {

			@Override
			public void run() {

				while (!Thread.interrupted()) {

					String webUrl;

					while ((webUrl = nextPageDataQueue.poll()) != null) {
						try {

							LOGGER.debug("Processing the link " + webUrl + " of earlier message ");
							startScrapping(webUrl);
						}
						catch (MalformedURLException cause) {
							LOGGER.error("MalformedURLException occred while writing file for the URL " + webUrl, cause);
							cause.printStackTrace();
						}
						catch (InterruptedException e) {
							LOGGER.error("InterruptedException occred while writing file for the URL " + webUrl, e);
							e.printStackTrace();
						}
						catch (ExecutionException e) {
							LOGGER.error("ExecutionException occred while writing file for the URL " + webUrl, e);
							e.printStackTrace();
						}
						catch (IOException e) {
							LOGGER.error("IOException occred while writing file for the URL " + webUrl, e);
							e.printStackTrace();
						}

					}
				}
			}
		};

		executorService.execute(pooler);
	}

	public void startScrapping(String url) throws MalformedURLException, InterruptedException, ExecutionException,
					IOException {
		Future<Response> futureResponse = getFutureAsResponse(url);

		final String pageData = getPageData( futureResponse.get().getBody());

		String urlSubstring = url.substring(0, url.lastIndexOf('/'));

		LOGGER.debug("Constant part of the Weblink "+ url+" is "+ urlSubstring);

		processWeblinksinPageData(pageData, urlSubstring);
	}

}
