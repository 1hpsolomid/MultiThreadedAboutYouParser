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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arcicleID == null) ? 0 : arcicleID.hashCode());
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((initialPrice == null) ? 0 : initialPrice.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((shippingCosts == null) ? 0 : shippingCosts.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Good other = (Good) obj;
		if (arcicleID == null) {
			if (other.arcicleID != null)
				return false;
		} else if (!arcicleID.equals(other.arcicleID))
			return false;
		if (brand == null) {
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (initialPrice == null) {
			if (other.initialPrice != null)
				return false;
		} else if (!initialPrice.equals(other.initialPrice))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (shippingCosts == null) {
			if (other.shippingCosts != null)
				return false;
		} else if (!shippingCosts.equals(other.shippingCosts))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}
}
