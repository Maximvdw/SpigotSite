package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.api.resource.PremiumResource;

public class SpigotPremiumResource extends SpigotResource implements
		PremiumResource {
	private double price = 0.0;
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPriceCurrency() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPriceCurrency(String currency) {
		// TODO Auto-generated method stub

	}

}
