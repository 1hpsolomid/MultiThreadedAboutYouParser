package org.webdata.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.webdata.model.Good;

public class XMLWriter {

	private final static Logger log = Logger.getLogger(XMLWriter.class.getName());;
	private static final String FILE_PATH = System.getProperty("user.dir") + System.getProperty("file.separator")
			+ "Offers.xml";

	public void goodsToXML(List<Good> goods) {
		String startLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<offers>";
		String endLine = "\n</offers>";
		FileWriter writer = null;
		try {
			writer = new FileWriter(FILE_PATH, true);
			writer.write(startLine);
			
			for (Good good : goods) {
				writer.write("\n\t<offer>\n\t\t<name>" + good.getName() + "</name>" + "\n\t\t<brand>" + good.getBrand()
						+ "</brand>" + "\n\t\t<color>" + good.getColor() + "</color>" + "\n\t\t<price>"
						+ good.getPrice() + "</price>" + "\n\t\t<initialPrice>" + good.getInitialPrice()
						+ "</initialPrice>" + "\n\t\t<description>" + good.getDescription() + "</description>"
						+ "\n\t\t<size>" + good.getSize() + "</size>" + "\n\t\t<articleID>" + good.getArcicleID()
						+ "</articleID" + "\n\t\t<shippingCosts>" + good.getShippingCosts() + "</shippingCosts"
						+ "\n\t</offer>");
			}
			
			writer.write(endLine);
			writer.flush();
			log.log(Level.INFO, "Wrote offers to the XML file");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
