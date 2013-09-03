/**
 * 
 */
package com.pramati.webscraper.delegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pramati.webscraper.dto.HtmlLink;
import com.pramati.webscraper.dto.Request;
import com.pramati.webscraper.dto.Response;
import com.pramati.webscraper.utils.HTMLLinkExtractor;

/**
 * @author sandeep-t Util Class for doing operations like url reading, url
 *         extraction and data extraction.
 * 
 */

public class WebScrapperDelegate {

	private static final Logger LOGGER = Logger.getLogger(WebScrapperDelegate.class);

	private FileOutputStream out;

	@Autowired
	HTMLLinkExtractor extractor;

	@Autowired
	ExecutorService executorService;

	private String destFilePathWithName;

	private boolean stopPooler = false;

	public BlockingQueue<Future<Response>> childFutureList = new LinkedBlockingQueue<Future<Response>>();

	public WebScrapperDelegate(String destDirPath, String destFileName) {
		if (StringUtils.hasLength(destDirPath) && !StringUtils.trimWhitespace(destDirPath).equals("")
						&& StringUtils.hasLength(destFileName) && !StringUtils.trimWhitespace(destFileName).equals("")) {
			if (!new File(destDirPath).isDirectory()) {
				LOGGER.error("Path mentioned for the property target.file.location is invaild directory path. Exiting System please set a valid value and RESTART !!!!");
				System.exit(0);
			}
			else {
				destFilePathWithName = destDirPath + System.getProperty("file.separator") + destFileName;
			}
		}
		else {
			LOGGER.error("Path mentioned for the property target.file.location/web.scrapped.file.name is invaild/null/improper. Exiting System please set a valid value and RESTART !!!!");
			System.exit(0);
		}
	}

	/**
	 * This method will take html data in form of String and will return a list
	 * of weblinks embedded in thathtml data
	 * 
	 * @param htmlData
	 * @return list of webaddress
	 */
	public void processWeblinksinPageData(String htmlData, String webLink) {
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

			LOGGER.debug("Link prepared finally " + stbr.toString());
			try {
				final Future<Response> response = getFutureAsResponse(stbr.toString());
				childFutureList.add(response);
			}
			catch (MalformedURLException e1) {
				LOGGER.error("MalformedURLException occured while parsing the URL " + stbr.toString(), e1);
			}

		}
		stopPooler = true;

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
	 * This fuction will be used to get the html response after parsing the data
	 * from future passed to the function.
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
	public void stratResponsePooler() throws FileNotFoundException {

		out = new FileOutputStream(destFilePathWithName);
		// final FileHelper writer = new FileHelper();

		Runnable pooler = new Runnable() {

			@Override
			public void run() {

				while (!stopPooler || childFutureList.size() > 0) {

					Future<Response> future;

					while ((future = childFutureList.poll()) != null) {

						Response response = null;

						FileLock lock = null;
						URL url = null;
						int responseCode = 0;
						try {

							response = future.get();
							url = response.getUrl();
							responseCode = response.getResponseCode();
							LOGGER.debug("Processing the response of the URL" + response.getUrl());
							InputStream stream = response.getBody();
							lock = out.getChannel().lock();
							IOUtils.copy(stream, out);

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
						finally {
							try {
								if (lock != null) {
									lock.release();
								}
							}
							catch (IOException cause) {

								LOGGER.error("Exception occured while releasing the lock, writing file for " + url,
												cause);

							}
						}

					}
				}
			}
		};
		
		executorService.execute(pooler);
	}

}
