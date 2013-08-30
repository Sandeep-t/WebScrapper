package com.pramati.webscraper.dto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class to read the inputstream sent across in the Future.
 * 
 * @author sandeep-t
 * 
 */
public class Response {

	final private InputStream body;

	final private URL url;

	final private int responseCode;

	public Response(InputStream body, URL url, int responseCode) {
		this.body = body;
		this.url = url;
		this.responseCode = responseCode;
	}

	public InputStream getBody() {
		return body;
	}

	public int getResponseCode() throws IOException {
		return responseCode;
	}

	public URL getUrl() throws IOException {
		return url;
	}

}