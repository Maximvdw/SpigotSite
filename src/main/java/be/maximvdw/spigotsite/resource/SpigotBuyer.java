package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.api.resource.Buyer;
import be.maximvdw.spigotsite.user.SpigotUser;

import java.util.Date;

/**
 * Spigot resource buyer
 */
public class SpigotBuyer extends SpigotUser implements Buyer {
    private Date purchaseDate = null;
    private String currency = "";
    private double price = -1;

    public void setPurchaseDate(Date date) {
        this.purchaseDate = date;
    }

    public void setPurchasePrice(double price) {
        this.price = price;
    }

    public void setPurchaseCurrency(String currency) {
        this.currency = currency;
    }

    public Date getPurchaseDateTime() {
        return purchaseDate;
    }

    public String getPurchaseCurrency() {
        return currency;
    }

    public double getPurchasePrice() {
        return price;
    }

    public boolean addedByAuthor() {
        return price == -1;
    }
}
