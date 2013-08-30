/**
 * 
 */
package com.pramati.webscraper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.pramati.webscraper.dto.HtmlLink;

/**
 * Class will serve the purpose of hyperlink validation and varification.
 * 
 * 
 * @author sandeep-t
 * 
 */
public class HTMLLinkExtractor {

	private final Pattern patternTag, patternLink;
	private Matcher matcherTag, matcherLink;
	private static final Logger LOGGER = Logger.getLogger(HTMLLinkExtractor.class);
	private static final String HTML_TAG_PATTERN = "(?i)<a([^>]+)>(.*?)</a>";
	private static final String HTML_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

	public HTMLLinkExtractor() {
		patternTag = Pattern.compile(HTML_TAG_PATTERN);
		patternLink = Pattern.compile(HTML_HREF_TAG_PATTERN);
	}

	/**
	 * 
	 * Validate html with regular expression and return the list of htmllinks
	 * extracted from html data passed.
	 * 
	 * @param html
	 *            html content for validation
	 * @return List links and link text
	 */
	public List<HtmlLink> grabHTMLLinks(final String html) {

		List<HtmlLink> result = new ArrayList<HtmlLink>();

		matcherTag = patternTag.matcher(html);

		while (matcherTag.find()) {

			String href = matcherTag.group(1); 
			LOGGER.info("Group 1 " + href);

			matcherLink = patternLink.matcher(href);

			while (matcherLink.find()) {

				String linkAddress = matcherLink.group(1); // link

				LOGGER.info("link  " + linkAddress);

				final HtmlLink links = new HtmlLink(linkAddress);

				result.add(links);

			}

		}

		return result;
	}
}
