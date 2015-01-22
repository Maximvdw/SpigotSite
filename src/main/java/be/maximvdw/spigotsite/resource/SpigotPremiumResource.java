package be.maximvdw.spigotsite.resource;

import java.util.ArrayList;
import java.util.List;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.PremiumResource;
import be.maximvdw.spigotsite.api.user.User;

public class SpigotPremiumResource extends SpigotResource implements
		PremiumResource {
	private double price = 0.0;
	private List<User> buyers = new ArrayList<User>();

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

	public List<User> getBuyers() {
		return buyers;
	}

	public void setBuyers(List<User> users) {
		this.buyers = users;
	}

	public void addBuyer(User user, User buyer) {
		SpigotSite.getAPI().getResourceManager().addBuyer(this, user, buyer);
	}

	public void addBuyer(User user, int userid) {
		SpigotSite.getAPI().getResourceManager().addBuyer(this, user, userid);
	}

	public void addBuyer(User user, String username) {
		SpigotSite.getAPI().getResourceManager().addBuyer(this, user, username);
	}

	public void addBuyers(User user, List<User> buyers) {
		SpigotSite.getAPI().getResourceManager().addBuyers(this, user, buyers);
	}

	public void addBuyers(PremiumResource resource, User user,
			String[] usernames) {
		SpigotSite.getAPI().getResourceManager()
				.addBuyers(this, user, usernames);
	}

	public void addBuyers(User user, String[] usernames) {
		// TODO Auto-generated method stub

	}

}
