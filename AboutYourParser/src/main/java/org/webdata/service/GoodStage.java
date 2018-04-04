package org.webdata.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.webdata.model.Good;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoodStage extends Thread {

	private final static Logger log = Logger.getLogger(GoodStage.class.getName());

	private final String goodURL;

	private final JsonParser jParser = new JsonParser();

	private static volatile int httpGoodRequests = 0;

	private List<Good> goods = new LinkedList<Good>();

	private final Semaphore available;

	@Override
	public void run() {

		try {
			available.acquire();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		Document goodPage = null;
		try {
			for (int i = 0; i < 20; i++) {
				goodPage = Jsoup.connect(goodURL).timeout(0).get();
				if (goodPage != null) {
					httpGoodRequests++;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Getting JSON
		String json = goodPage.html();
		json = json.split("window.__INITIAL_STATE__=")[1];
		json = json.substring(0, json.indexOf(";</script>"));

		// Parsing JSON
		JsonObject jsonObject = (JsonObject) jParser.parse(json);

			JsonObject jsonGood = jsonObject.get("adpPage").getAsJsonObject().get("product").getAsJsonObject();
			JsonArray variantsArray = jsonGood.get("variants").getAsJsonArray();

			String goodName = jsonGood.get("data").getAsJsonObject().get("name").getAsString();
			String goodBrand = jsonGood.get("brand").getAsJsonObject().get("name").getAsString();
			String goodArticleID = jsonGood.get("productInfo").getAsJsonObject().get("articleNumber").getAsString();
			String goodColor = jsonGood.get("styles").getAsJsonArray().get(0).getAsJsonObject().get("color")
					.getAsString();
			String goodDescription = deleteSymbols(goodPage.getElementsByClass("wrapper_1w5lv0w").html());

			// Tried to parse good description in this way, but in 90% times, got just "-"
			// in description.
			// String goodDescription =
			// jsonGood.get("productInfo").getAsJsonObject().get("description").getAsString();

			for (JsonElement jsonElement : variantsArray) {

				// Checking whether the good is available
				if (jsonElement.getAsJsonObject().get("quantity").getAsInt() == 0)
					continue;

				Good good = new Good();
				good.setName(goodName);
				good.setBrand(goodBrand);
				good.setColor(goodColor);
				good.setPrice(jsonElement.getAsJsonObject().get("price").getAsJsonObject().get("current")
						.getAsBigDecimal().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
				good.setInitialPrice(jsonElement.getAsJsonObject().get("price").getAsJsonObject().get("old")
						.getAsBigDecimal().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
				good.setDescription(goodDescription);
				good.setSize(jsonElement.getAsJsonObject().get("sizes").getAsJsonObject().get("shop").getAsString());
				good.setArcicleID(goodArticleID);
				goods.add(good);
				log.log(Level.INFO, "Parsed the good! URL: " + goodURL);
			}
		available.release();
	}

	private String deleteSymbols(String html) {
		return html.replaceAll("[<](/)?div[^>]*[>]", "").replaceAll(" class?=\"[^>]*[\"]", "")
				.replaceAll(" data-reactid?=\"[^>]*[\"]", "").replaceAll("<!--.*?-->", "").replaceAll("<p>", "")
				.replaceAll("</p>", "").replaceAll("<li>", "").replaceAll("</li>", " ").replaceAll("<ul>", "")
				.replaceAll("</ul>", "").replaceAll(" +", " ").replaceAll("\n ", "\n").replaceAll("\n+", "");
	}

	public static int getHttpGoodRequests() {
		return httpGoodRequests;
	}

	public GoodStage(String goodURL, List<Good> goods, Semaphore available) {
		this.goodURL = goodURL;
		this.goods = goods;
		this.available = available;
	}
}
