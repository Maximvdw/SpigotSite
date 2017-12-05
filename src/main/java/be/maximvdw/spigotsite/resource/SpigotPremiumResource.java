package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.resource.Buyer;
import be.maximvdw.spigotsite.api.resource.PremiumResource;
import be.maximvdw.spigotsite.api.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpigotPremiumResource extends SpigotResource implements
        PremiumResource {
    private double price = 0.0;
    private List<Buyer> buyers = new ArrayList<Buyer>();

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

    public List<Buyer> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<Buyer> users) {
        this.buyers = users;
    }

    public void addBuyer(User user, User buyer) throws ConnectionFailedException {
        SpigotSite.getAPI().getResourceManager().addBuyer(this, user, buyer);
    }

    public void addBuyer(User user, int userid) throws ConnectionFailedException {
        SpigotSite.getAPI().getResourceManager().addBuyer(this, user, userid);
    }

    public void addBuyer(User user, String username) throws ConnectionFailedException {
        SpigotSite.getAPI().getResourceManager().addBuyer(this, user, username);
    }

    public void addBuyers(User user, List<User> buyers) throws ConnectionFailedException {
        SpigotSite.getAPI().getResourceManager().addBuyers(this, user, buyers);
    }

    public void addBuyers(PremiumResource resource, User user,
                          String[] usernames) throws ConnectionFailedException {
        SpigotSite.getAPI().getResourceManager()
                .addBuyers(this, user, usernames);
    }

    public void addBuyers(User user, String[] usernames) throws ConnectionFailedException {
        addBuyers(this, user, usernames);
    }

    public boolean isBuyer(User user) {
        for (Buyer buyer : buyers) {
            if (buyer.equals(user))
                return true;
        }
        return false;
    }

    public Buyer getBuyerByName(String s) {
        for (Buyer buyer : buyers) {
            if (buyer.getUsername().equalsIgnoreCase(s))
                return buyer;
        }
        return null;
    }

    public Buyer getBuyerByUserId(int i) {
        for (Buyer buyer : buyers) {
            if (buyer.getUserId() == i)
                return buyer;
        }
        return null;
    }

}
