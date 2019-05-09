package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.UserDebugging;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.resource.*;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ResourceManagerTest {

    @Before
    public void init() {
        new SpigotSiteCore();
    }

    @Test(timeout = 15000)
    public void getResourceByIdTest() throws ConnectionFailedException {
        System.out.println("Testing 'getResourceById 578' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        // Test Tab plugin
        Resource resource = resourceManager.getResourceById(578);
        assertNotNull(resource);
        assertNotNull(resource.getAuthor());

        System.out.println("Resource name: " + resource.getResourceName());
        System.out.println("Resource id: " + resource.getResourceId());
        System.out.println("Resource author: " + resource.getAuthor().getUsername() + " ["
                + resource.getAuthor().getUserId() + "]");
        System.out.println("Resource version: " + resource.getLastVersion());
        System.out.println("Resource URL: " + resource.getDownloadURL());
        assertEquals("Auto-In", resource.getResourceName());
        assertEquals(578, resource.getResourceId());
        assertEquals("GoToFinal", resource.getAuthor().getUsername());
    }

    @Test(timeout = 15000)
    public void getResourceCategoriesTest() throws ConnectionFailedException {
        System.out.println("Testing 'getResourceCategories' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        List<ResourceCategory> categories = resourceManager.getResourceCategories();
        for (ResourceCategory category : categories) {
            System.out.println(category.getCategoryName() + " [" + category.getCategoryId() + "] - Count: "
                    + category.getResourceCount());
        }
        assertNotNull(categories);
    }

    @Test(timeout = 15000)
    public void getResourceCategoryByIdTest() throws ConnectionFailedException {
        System.out.println("Testing 'getResourceCategoryById 2' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        ResourceCategory category = resourceManager.getResourceCategoryById(2);
        assertNotNull(category);
    }

    @Test
    public void getResourcesByCategoryTest() throws ConnectionFailedException {
        System.out.println("Testing 'getResourcesByCategory 2' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        ResourceCategory category = resourceManager.getResourceCategoryById(2);
        List<Resource> resources = resourceManager.getResourcesByCategory(category);
        assertNotNull(resources);
        System.out.println("Expected count: " + category.getResourceCount() + " Fetched count: " + resources.size());
        // assertEquals(category.getResourceCount(), resources.size());
        for (Resource resource : resources) {
            System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
                    + resource.getLastVersion() + "\n\tBy " + resource.getAuthor().getUsername() + " ["
                    + resource.getAuthor().getUserId() + "]");
            assertNotNull(resource.getAuthor());
        }
    }

    @Test
    public void getNewResourcesTest() throws ConnectionFailedException {
        System.out.println("Testing 'getNewResourcesTest' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        List<Resource> resources = resourceManager.getNewResources(50000);
        assertNotNull(resources);
        for (Resource resource : resources) {
            System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
                    + resource.getLastVersion() + "\n\tBy " + resource.getAuthor().getUsername() + " ["
                    + resource.getAuthor().getUserId() + "]");
            assertNotNull(resource.getAuthor());
        }
    }

    @Test(timeout = 15000)
    public void getResourcesByUserTest() throws ConnectionFailedException {
        System.out.println("Testing 'getResourcesByUser 6687' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        List<Resource> resources = resourceManager.getResourcesByUser(6687);
        assertNotNull(resources);
        System.out.println("Resources by User 6687:");
        for (Resource resource : resources) {
            System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
                    + resource.getLastVersion());
            assertNotNull(resource.getAuthor());
        }
    }

    @Test
    public void getResourceBuyersCount() throws InvalidCredentialsException, TwoFactorAuthenticationException, ConnectionFailedException {
        User user = UserDebugging.getUser();
        List<Resource> resources = SpigotSite.getAPI().getResourceManager()
                .getResourcesByUser(user);
        for (Resource res : resources) {
            if (res instanceof PremiumResource) {
                System.out.println("\t" + res.getResourceName());
                try {
                    List<Buyer> resourceBuyers = SpigotSite
                            .getAPI()
                            .getResourceManager()
                            .getPremiumResourceBuyers(
                                    (PremiumResource) res, user);
                    System.out.println("\t\tBuyers: " + resourceBuyers.size());
                } catch (ConnectionFailedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    @Test(timeout = 15000)
    public void getPurchasedResourcesByUserTest() throws InvalidCredentialsException, ConnectionFailedException, TwoFactorAuthenticationException {
        System.out.println("Testing 'getPurchasedResourcesByUser' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();

        List<Resource> resources = resourceManager.getPurchasedResources(user);
        assertNotNull(resources);
        System.out.println("Purchased Resources by User:");
        for (Resource resource : resources) {
            System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
                    + resource.getLastVersion());
        }
    }

    @Test
    public void downloadResource() throws Exception {
        System.out.println("Testing 'downloadResource 578' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        Resource res = resourceManager.getResourceById(578);
        File tmpFile = File.createTempFile("resource-", ".jar");
        res.downloadResource(null, tmpFile);
        System.out.println(tmpFile.length());
        if (tmpFile.length() < 5000) {
            throw new Exception("File size is wrong!");
        }
        tmpFile.delete();
    }

    @Test
    public void downloadPremiumResource() throws InvalidCredentialsException, IOException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'downloadPremiumResource 1458' ...");
        User user = UserDebugging.getUser();
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        Resource res = resourceManager.getResourceById(1458, user);
        File tmpFile = File.createTempFile("resource-", ".jar");
        res.downloadResource(user, tmpFile);
        tmpFile.delete();
    }

    //@Test
    public void addAndRemoveFromBuyers() throws InvalidCredentialsException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'addAndRemoveFromBuyers 13370' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();
        Resource resource = resourceManager.getResourceById(13370, user);
        PremiumResource premiumResource = (SpigotPremiumResource) resource;
        List<Buyer> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        for (Buyer b : buyers) {
            if (b.getUsername().equalsIgnoreCase("Maximvdw")) {
                fail("User already in buyers");
            }
        }

        resourceManager.addBuyer(premiumResource, user, "Maximvdw");
        buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        boolean found = false;
        for (Buyer b : buyers) {
            if (b.getUsername().equalsIgnoreCase("Maximvdw")) {
                found = true;
            }
        }
        if (!found) {
            fail("User was not added to the buyers!");
        }

        resourceManager.removeBuyer(premiumResource, user, user.getUserId());
        buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        found = false;
        for (Buyer b : buyers) {
            if (b.getUsername().equalsIgnoreCase("Maximvdw")) {
                found = true;
            }
        }
        if (found) {
            fail("User was not removed from the buyers!");
        }
    }

    @Test//(timeout = 15000)
    public void getBuyers() throws InvalidCredentialsException, ConnectionFailedException, TwoFactorAuthenticationException {
        System.out.println("Testing 'getBuyers 2691' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();
        Resource resource = resourceManager.getResourceById(2691, user);
        PremiumResource premiumResource = (SpigotPremiumResource) resource;
        List<Buyer> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        List<Buyer> buyers2 = resourceManager.getPremiumResourceBuyers(premiumResource, user);

        if (!buyers.isEmpty())
            assertEquals(buyers.get(0), buyers2.get(0));

        System.out.println("Buyers of " + resource.getResourceName() + ":");
        for (Buyer buyer : buyers) {
            System.out.println("\t" + buyer.getUsername() + " [" + buyer.getUserId() + "]");
        }

        System.out.println("Testing 'getBuyers 2175' ...");
        resource = resourceManager.getResourceById(2175, user);
        premiumResource = (SpigotPremiumResource) resource;
        buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        buyers2 = resourceManager.getPremiumResourceBuyers(premiumResource, user);

        if (!buyers.isEmpty())
            assertEquals(buyers.get(0), buyers2.get(0));

        System.out.println("Buyers of " + resource.getResourceName() + ":");
        for (User buyer : buyers) {
            System.out.println("\t" + buyer.getUsername() + " [" + buyer.getUserId() + "]");
        }

        System.out.println("Testing 'getBuyers 33640' ...");
        resource = resourceManager.getResourceById(33640, user);
        premiumResource = (SpigotPremiumResource) resource;
        buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
        buyers2 = resourceManager.getPremiumResourceBuyers(premiumResource, user);

        if (!buyers.isEmpty())
            assertEquals(buyers.get(0), buyers2.get(0));

        System.out.println("Buyers of " + resource.getResourceName() + ":");
        System.out.println("String[] data = {");
        for (User buyer : buyers) {
            System.out.print("\"" + buyer.getUserId() + "\",");
        }
        System.out.println("}");
    }

    @Test//(timeout = 15000)
    public void getUpdates() throws InvalidCredentialsException, ConnectionFailedException, TwoFactorAuthenticationException {
        System.out.println("Testing 'getUpdates 3663' ...");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();
        Resource resource = resourceManager.getResourceById(3663, user);
        PremiumResource premiumResource = (SpigotPremiumResource) resource;
        List<ResourceUpdate> updates = premiumResource.getResourceUpdates();

        System.out.println("Updates of " + resource.getResourceName() + ":");
        System.out.println("Number of Updates: " + updates.size());

        System.out.println(updates.get(0).getTextHeading() + "\n"
                + updates.get(0).getUpdateID() + " " + updates.get(0).getMessageMeta() + " " + updates.get(0).getUpdateLink()
                + updates.get(0).getArticle() + "\n");
    }

    @Test
    public void getDuplicateBuyers() throws TwoFactorAuthenticationException, ConnectionFailedException, InvalidCredentialsException {
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();
        // Get your resources
        for (Resource resource : resourceManager.getResourcesByUser(user)) {
            // Check if the resource is premium
            if (resource instanceof PremiumResource) {
                PremiumResource premiumResource = (PremiumResource) resource;
                // Get the people who bought that plugin
                List<Buyer> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
                List<Buyer> checked = new ArrayList<Buyer>();
                for (Buyer b : buyers) {
                    if (checked.contains(b)) {
                        System.out.println("Duplicate buyer: " + b.getUsername() + " in " + resource.getResourceName());
                    }
                    checked.add(b);
                }
            }
        }
    }

    @Test
    public void getTopBuyers() throws InvalidCredentialsException, ConnectionFailedException, TwoFactorAuthenticationException {
        System.out.println("Testing 'get the buyers that bought all my plugins'");
        ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
        User user = UserDebugging.getUser();

        List<Buyer> favoriteBuyers = new ArrayList<Buyer>();

        // Get your resources

        double totalPrice = 0;
        for (Resource resource : resourceManager.getResourcesByUser(user)) {
            // Check if the resource is premium
            if (resource instanceof PremiumResource) {
                PremiumResource premiumResource = (PremiumResource) resource;
                // Get the people who bought that plugin
                List<Buyer> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
                double price = 0;
                String currency = "";
                for (Buyer b : buyers) {
                    if (b.getPurchasePrice() != -1) {
                        price += (Math.round(b.getPurchasePrice() * 100) / 100.);
                        currency = b.getPurchaseCurrency();
                    }
                }
                price = (Math.round(price * 100) / 100.);
                System.out.println(
                        "The plugin " + premiumResource.getResourceName() + " got " + buyers.size() + " buyers.  [" + currency + " " + price + "]");
                for (int year = 2014 ; year < 2020 ; year++){
                    price = 0;
                    for (Buyer b : buyers) {
                        if (b.getPurchasePrice() != -1) {
                            if (b.getPurchaseDateTime() == null) {
                                continue;
                            }
                            Calendar startDate = Calendar.getInstance();
                            startDate.clear();
                            startDate.set(Calendar.YEAR, year);
                            Calendar endDate = Calendar.getInstance();
                            endDate.clear();
                            endDate.set(Calendar.YEAR, year+1);
                            if (b.getPurchaseDateTime().after(startDate.getTime()) && b.getPurchaseDateTime().before(endDate.getTime())) {
                                price += (Math.round(b.getPurchasePrice() * 100) / 100.);
                            }
                        }
                    }
                    price = (Math.round(price * 100) / 100.);
                    System.out.println("\tEarnings in " + year + " = " + currency + " " + price);
                    totalPrice += price;
                }

                if (favoriteBuyers.size() == 0)
                    favoriteBuyers = buyers;
                else {
                    List<Buyer> newFavorites = new ArrayList<Buyer>();
                    for (Buyer buyer : buyers) {
                        if (favoriteBuyers.contains(buyer))
                            newFavorites.add(buyer);
                    }
                    favoriteBuyers = newFavorites;
                }
            }
        }


        System.out.println("Total earnings: USD " + (Math.round(totalPrice * 100) / 100.));

        // Your favorite buyers
        System.out.println("People who bought all your plugins:");
        for (User favBuyer : favoriteBuyers) {
            System.out.println("\t" + favBuyer.getUsername() + "  <3");
        }
    }
}
