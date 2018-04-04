package org.webdata.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class Good {

	private String name;
	private String brand;
	private String color;
	private String description;
	private String arcicleID;
	private String size;
	private BigDecimal price;
	private BigDecimal initialPrice;
	// Checked on the website
	private final String shippingCosts = "0.00";

}
