package org.webdata.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.webdata.model.Good;

public class PageStage extends Thread {

	private final String BASE_URL = "https://www.aboutyou.de";

	private Document searchResultPage;

	private List<Good> goods = new LinkedList<Good>();

	private final Semaphore available = new Semaphore(15, true);

	@Override
	public void run() {
		// Current website has got two kinds of the search results page.
		try {
			available.acquire();
			Element items = searchResultPage
					.getElementsByAttributeValueMatching("class", "row list-wrapper product-image-list").first();
			Elements selectedItems = items.getElementsByAttributeValueMatching("class", "col-xs-4 isLayout3");
			for (Element itemElement : selectedItems) {
				GoodStage goodThread = new GoodStage(
						UrlChecker(itemElement.getElementsByTag("a").attr("href"), BASE_URL), goods, available);
				goodThread.start();
			}
		} catch (Exception e) {
			Element items = searchResultPage.getElementsByAttributeValueMatching("class", "wrapper_8yay2a").first();
			Elements selectedItems = items.getElementsByAttributeValueMatching("class", "col-sm-6 col-md-4");
			for (Element itemElement : selectedItems) {
				if (itemElement.getElementsByClass("wrapper_1eu800j").first() == null)
					continue;
				GoodStage goodThread = new GoodStage(
						UrlChecker(itemElement.getElementsByTag("a").attr("href"), BASE_URL), goods, available);
				goodThread.start();
			}
		}
		available.release();
	}

	public PageStage(Document searchResultPage, List<Good> goods) {
		this.searchResultPage = searchResultPage;
		this.goods = goods;
	}

	private String UrlChecker(String goodUrl, String baseUrl) {
		return !goodUrl.startsWith(BASE_URL) ? baseUrl + goodUrl : goodUrl;
	}
}
