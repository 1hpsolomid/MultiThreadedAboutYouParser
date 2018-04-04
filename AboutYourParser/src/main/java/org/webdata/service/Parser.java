package org.webdata.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.webdata.model.Good;

public class Parser {

	private final static Logger log = Logger.getLogger(Parser.class.getName());

	private final String url = "https://www.aboutyou.de/suche?category=20201&term=";

	private volatile List<Good> goods = new LinkedList<Good>();

	private volatile int httpParserRequests = 0;

	public List<Good> startParsing(String keyword) {

		int pageNumber = 1;
		String encodedKeyWord = null;
		Document checkSearchResultPage = null;
		Document searchResultPage = null;

		try {
			encodedKeyWord = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String checkedUrl = redirectChecker(encodedKeyWord, url);

		try {

			for (int i = 0; i < 20; i++) {
				checkSearchResultPage = Jsoup.connect(checkedUrl).timeout(0).get();
				httpParserRequests++;
				if (checkSearchResultPage != null)
					break;
			}

			// Checking whether the goods were found
			if (checkSearchResultPage.getElementsByClass("xxl split-search-headline").first() == null) {
				do {
					try {
						for (int i = 0; i < 20; i++) {

							searchResultPage = Jsoup.connect(checkedUrl + "&page=" + pageNumber).timeout(0).get();
							httpParserRequests++;
							if (searchResultPage != null) {
								log.log(Level.INFO, "Connected to " + pageNumber + " page! URL: " + checkedUrl
										+ "&page=" + pageNumber);
								break;
							}
						}
						PageStage pageThread = new PageStage(searchResultPage, goods);
						pageThread.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					pageNumber++;
				} while (searchResultPage.getElementsByClass("next").first() != null
						|| searchResultPage.getElementsByClass("nextButton_3hmsj").first() != null);
			} else {
				log.log(Level.INFO,
						"Search for " + keyword + " did not match any results. The program is shutting down!");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Waiting, when all threads, except main thread will stop
		do {

		} while (Thread.activeCount() > 1);

		return goods;
	}

	public int getHttpParserRequests() {
		return httpParserRequests;
	}

	private String redirectChecker(String encodedKeyWord, String url) {
		HttpURLConnection con = null;
		int responseCode = 0;
		log.log(Level.INFO, "Checking URL...");
		try {
			con = (HttpURLConnection) (new URL(url + encodedKeyWord).openConnection());
			con.setInstanceFollowRedirects(false);
			con.connect();

			responseCode = con.getResponseCode();
			log.log(Level.INFO, "HTTP status code is: " + responseCode);

			if (responseCode == 302) {
				log.log(Level.INFO, "Redirecting to: " + con.getHeaderField("Location"));
			} else {
				log.log(Level.INFO, "No redirect!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}

		return responseCode == 302 ? con.getHeaderField("Location") : url + encodedKeyWord;
	}
}
