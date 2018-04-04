package org.webdata;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.webdata.model.Good;
import org.webdata.service.GoodStage;
import org.webdata.service.Parser;
import org.webdata.service.XMLWriter;

/**
 * 
 * @author Bogdan Cherkas
 *
 */
public class ApplicationRunner {

	private static final Logger log = Logger.getLogger(Parser.class.getName());

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			log.log(Level.INFO, "No keyword(s) inputted, the program is shutting down! ");
			return;
		}
		XMLWriter xmlWriter = new XMLWriter();
		Parser goodsParser = new Parser();
		List<Good> extractedGoods = new LinkedList<Good>();

		long start = System.currentTimeMillis();
		log.log(Level.INFO, "Parsing begins...");

		extractedGoods.addAll(goodsParser.startParsing(args[0]));
		xmlWriter.goodsToXML(extractedGoods);
		
		log.log(Level.INFO, "Amount of triggered HTTP requests: " + (GoodStage.getHttpGoodRequests() + goodsParser.getHttpParserRequests()));
		log.log(Level.INFO, "Run-time: " + (System.currentTimeMillis() - start) / 1000 + "seconds");
		log.log(Level.INFO, "Memory Footprint: "
				+ ((double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + " mb");
		log.log(Level.INFO, "Amount of extracted products: " + extractedGoods.size());
		log.log(Level.INFO, "The program finished!");
	}
}
