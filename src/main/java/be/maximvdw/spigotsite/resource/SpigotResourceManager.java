package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.exceptions.PermissionException;
import be.maximvdw.spigotsite.api.resource.*;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import be.maximvdw.spigotsite.http.HTTPResponse;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.user.SpigotUser;
import be.maximvdw.spigotsite.user.SpigotUserManager;
import be.maximvdw.spigotsite.utils.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotResourceManager implements ResourceManager {
    private List<ResourceCategory> resourceCategories = new ArrayList<ResourceCategory>();

    public Resource getResourceById(int resourceid) {
        return getResourceById(resourceid, null);
    }

    public Resource getResourceById(int resourceid, User user) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/" + resourceid;
            Map<String, String> params = new HashMap<String, String>();
            HTTPResponse res = Request.get(url,
                    user == null ? SpigotSiteCore.getBaseCookies() : ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();
            Element categoryLink = doc.select("a.crumb").last();
            SpigotResource resource = new SpigotResource();

            if (categoryLink.text().toLowerCase().contains("premium"))
                resource = new SpigotPremiumResource();

            String resourceName = doc.title().replace(" | SpigotMC - High Performance Minecraft", "");
            resource.setResourceName(resourceName);
            resource.setResourceId(resourceid);

            if(doc.select("div.resourceInfo").size() > 0 && doc.select("label.downloadButton").size() > 0 && doc.select("label.downloadButton").select("a.inner").size() > 0) {
                Element resourceInfo = doc.select("div.resourceInfo").get(0);
                resource.setLastVersion(resourceInfo.select("span.muted").get(0).text());

                Element downloadLink = doc.select("label.downloadButton").get(0).select("a.inner").get(0);
                resource.setDownloadURL(SpigotSiteCore.getBaseURL() + downloadLink.attr("href"));

                Element author = doc.select("dl.author").first();
                SpigotUser authorUser = new SpigotUser();
                authorUser.setUsername(author.select("a").first().text());
                authorUser.setUserId(Integer
                                             .parseInt(StringUtils.getStringBetween(author.select("a").first().attr("href"), "\\.(.*?)/")));
                resource.setAuthor(authorUser);
                resource.setResourceUpdates(getResourceUpdates(resourceid, user));
                return resource;
            }
        } catch (TwoFactorAuthenticationException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<ResourceUpdate> getResourceUpdates(int resourceid, User user) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/" + resourceid + "/updates";
            Map<String, String> params = new HashMap<String, String>();
            HTTPResponse res = Request.get(url,
                    user == null ? SpigotSiteCore.getBaseCookies() : ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();
            List<ResourceUpdate> updates = new ArrayList<ResourceUpdate>();

            Elements pages = doc.select("div.PageNav nav a");
            if (pages.size() != 0) {
                pages.remove(pages.size() - 1);
                for (Element page : pages) {
                    String newUrl = SpigotSiteCore.getBaseURL() + page.attr("href");
                    HTTPResponse newRes = Request.get(newUrl,
                            user == null ? SpigotSiteCore.getBaseCookies() : ((SpigotUser) user).getCookies(), params);
                    Document newDoc = newRes.getDocument();

                    Elements resourceBlocks = newDoc.select("li.primaryContent");
                    for (Element resourceBlock : resourceBlocks) {
                        ResourceUpdate resourceUpdate = new SpigotResourceUpdate();
                        resourceUpdate.setUpdateID(resourceBlock.attr("id"));
                        resourceUpdate.setUpdateLink(SpigotSiteCore.getBaseURL() + resourceBlock.select("h2.textHeading a").first().attr("href"));
                        resourceUpdate.setTextHeading(resourceBlock.select("h2.textHeading a").first().text());
                        resourceUpdate.setArticle(resourceBlock.select("article blockquote").first().text());
                        //resourceUpdate.setMessageMeta(resourceBlock.select("div.messageMeta span.item a span").first().attr("title"));

                        updates.add(resourceUpdate);
                    }
                }
            } else {
                Elements resourceBlocks = doc.select("li.primaryContent");
                for (Element resourceBlock : resourceBlocks) {
                    ResourceUpdate resourceUpdate = new SpigotResourceUpdate();
                    resourceUpdate.setUpdateID(resourceBlock.attr("id"));
                    resourceUpdate.setUpdateLink(url + resourceBlock.select("h2.textHeading a").first().attr("href"));
                    resourceUpdate.setTextHeading(resourceBlock.select("h2.textHeading a").first().text());
                    resourceUpdate.setArticle(resourceBlock.select("article blockquote").first().text());
                    updates.add(resourceUpdate);
                }
            }

            return updates;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<Resource> getResourcesByUser(User user) {
        if (user.isAuthenticated()) {
            return getResourcesByUser(user, user);
        } else {
            List<Resource> resources = getResourcesByUser(user.getUserId());
            for (Resource resource : resources) {
                ((SpigotResource) resource).setAuthor(user);
            }
            return resources;
        }
    }

    public List<Resource> getNewResources(int i) throws ConnectionFailedException {
        List<Resource> resources = new ArrayList<Resource>();
        try {
            // TODO: This is a no no - just for debugging
            int page = 1;
            while (true) {
                String url = SpigotSiteCore.getBaseURL() + "resources/?order=resource_date"
                        + "&page=" + page;
                Map<String, String> params = new HashMap<String, String>();

                HTTPResponse res = Request.get(url, SpigotSiteCore.getBaseCookies(), params);
                Document doc = res.getDocument();
                Elements resourceBlocks = doc.select("li.resourceListItem");
                if (resourceBlocks.size() == 0) {
                    break;
                }
                for (Element resourceBlock : resourceBlocks) {
                    int id = Integer.parseInt(resourceBlock.id().replace("resource-", ""));
                    Element resourceLink = resourceBlock.select("h3.title").get(0).getElementsByTag("a").get(0);
                    SpigotResource resource = new SpigotResource(resourceLink.text());
                    resource.setResourceId(id);
                    Element username = resourceBlock.select("a.username").first();
                    Element version = resourceBlock.select("span.version").first();
                    resource.setLastVersion(version.text());
                    SpigotUser user = new SpigotUser();
                    user.setUsername(username.text());
                    user.setUserId(Integer.parseInt(StringUtils.getStringBetween(username.attr("href"), "\\.(.*?)/")));
                    resource.setAuthor(user);

                    if (id < i) {
                        resources.add(resource);
                        return resources;
                    } else if (id == i) {
                        return resources;
                    } else {
                        resources.add(resource);
                    }
                }
                page++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resources;
    }

    public List<Resource> getResourcesByUser(User user, User loggedInUser) {
        List<Resource> resources = getResourcesByUser(user.getUserId(), loggedInUser);
        for (Resource resource : resources) {
            ((SpigotResource) resource).setAuthor(user);
        }
        return resources;
    }

    public List<Resource> getResourcesByUser(int userid, User loggedInUser) {
        List<Resource> createdResources = new ArrayList<Resource>();
        try {
            int page = 0;
            while (true) {
                String url = SpigotSiteCore.getBaseURL() + "resources/authors/" + userid + (page > 0 ? "?page=" + (page + 1) : "");
                page++;
                Map<String, String> params = new HashMap<String, String>();
                HTTPResponse res = Request.get(url,
                        loggedInUser == null ? SpigotSiteCore.getBaseCookies() : ((SpigotUser) loggedInUser).getCookies(),
                        params);
                // Handle two step
                res = SpigotUserManager.handleTwoStep(res, (SpigotUser) loggedInUser);
                Document doc = res.getDocument();
                String username = StringUtils.getStringBetween(doc.title(),
                        "Resources from (.*?) | SpigotMC - High Performance Minecraft");
                SpigotUser user = new SpigotUser(username);
                user.setUserId(userid);

                Elements resourceBlocks = doc.select("li.resourceListItem");
                if (resourceBlocks.size() < 1) {
                    break;
                }
                for (Element resourceBlock : resourceBlocks) {
                    int id = Integer.parseInt(resourceBlock.id().replace("resource-", ""));
                    Element resourceLink = resourceBlock.select("h3.title").get(0).getElementsByTag("a").get(0);

                    Element categoryLink = resourceBlock.select("div.resourceDetails").select("a").last();
                    SpigotResource resource = new SpigotResource();
                    if (categoryLink.text().toLowerCase().contains("premium"))
                        resource = new SpigotPremiumResource();

                    resource.setResourceName(resourceLink.text());
                    resource.setAuthor(user);
                    resource.setResourceId(id);
                    createdResources.add(resource);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return createdResources;
    }

    public List<Resource> getResourcesByUser(int userid) {
        return getResourcesByUser(userid, null);
    }

    public List<Resource> getPurchasedResources(User user) {
        List<Resource> boughtResources = new ArrayList<Resource>();
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/purchased";
            Map<String, String> params = new HashMap<String, String>();

            HTTPResponse res = Request.get(url, ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();
            Elements resourceBlocks = doc.select("li.resourceListItem");
            for (Element resourceBlock : resourceBlocks) {
                int id = Integer.parseInt(resourceBlock.id().replace("resource-", ""));
                Element resourceLink = resourceBlock.select("h3.title").get(0).getElementsByTag("a").get(0);
                SpigotResource resource = new SpigotResource(resourceLink.text());
                resource.setResourceId(id);
                boughtResources.add(resource);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return boughtResources;
    }

    public List<ResourceCategory> getResourceCategories() {
        List<ResourceCategory> resourceCategories = new ArrayList<ResourceCategory>();
        if (this.resourceCategories.size() > 0)
            return this.resourceCategories;
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/";
            Map<String, String> params = new HashMap<String, String>();

            HTTPResponse res = Request.get(url, SpigotSiteCore.getBaseCookies(), params);
            Document doc = res.getDocument();
            Element categoryList = doc.select("div.categoryList").first();
            Elements categories = categoryList.select("li");
            for (Element category : categories) {
                Element link = category.select("a").first();
                Element count = category.select("span.count").first();
                SpigotResourceCategory resourceCategory = new SpigotResourceCategory();
                resourceCategory.setCategoryName(link.text());
                String resourceCount = count.text().toString().replace(",", "");
                resourceCategory.setResourceCount(Integer.parseInt(resourceCount));
                resourceCategory
                        .setCategoryId(Integer.parseInt(StringUtils.getStringBetween(link.attr("href"), "\\.(.*?)/")));
                resourceCategories.add(resourceCategory);
            }

            this.resourceCategories = resourceCategories;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resourceCategories;
    }

    public List<Resource> getResourcesByCategory(ResourceCategory category) {
        List<Resource> resources = new ArrayList<Resource>();
        try {
            int lastPage = category.getResourceCount() / 20;
            if (category.getResourceCount() % 20 != 0)
                lastPage++;
            for (int i = lastPage; i >= 1; i--) {
                String url = SpigotSiteCore.getBaseURL() + "resources/categories/" + category.getCategoryId()
                        + "/?page=" + i;
                Map<String, String> params = new HashMap<String, String>();

                HTTPResponse res = Request.get(url, SpigotSiteCore.getBaseCookies(), params);
                Document doc = res.getDocument();
                Elements resourceBlocks = doc.select("li.resourceListItem");

                for (Element resourceBlock : resourceBlocks) {
                    int id = Integer.parseInt(resourceBlock.id().replace("resource-", ""));
                    Element resourceLink = resourceBlock.select("h3.title").get(0).getElementsByTag("a").get(0);
                    SpigotResource resource = new SpigotResource(resourceLink.text());
                    resource.setResourceId(id);
                    Element username = resourceBlock.select("a.username").first();
                    Element version = resourceBlock.select("span.version").first();
                    resource.setLastVersion(version.text());
                    SpigotUser user = new SpigotUser();
                    user.setUsername(username.text());
                    user.setUserId(Integer.parseInt(StringUtils.getStringBetween(username.attr("href"), "\\.(.*?)/")));
                    resource.setAuthor(user);

                    resources.add(resource);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resources;
    }

    public ResourceCategory getResourceCategoryById(int id) {
        if (this.resourceCategories.size() == 0)
            getResourceCategories();
        for (ResourceCategory category : resourceCategories) {
            if (category.getCategoryId() == id)
                return category;
        }
        return null;
    }

    public String getLastVersion(int resourceId) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
                    .openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resourceId)
                            .getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 7) {
                return version;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Buyer> getPremiumResourceBuyers(PremiumResource resource, User user) throws ConnectionFailedException {
        List<Buyer> buyers = new ArrayList<Buyer>();
        SpigotPremiumResource spigotResource = (SpigotPremiumResource) resource;
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/" + resource.getResourceId() + "/buyers";
            Map<String, String> params = new HashMap<String, String>();
            HTTPResponse res = Request.get(url, ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();

            // Get all available buyer pages
            Elements pageNav = doc.select("div.PageNav");
            int pages = 1;
            if (pageNav.size() != 0){
               pages = Integer.parseInt(pageNav.attr("data-last"));
            }
            for (int i = 1; i <= pages; i++) {
                String newUrl = url + "?page=" + i;
                HTTPResponse newRes = Request.get(newUrl,
                        user == null ? SpigotSiteCore.getBaseCookies() : ((SpigotUser) user).getCookies(), params);
                Document newDoc = newRes.getDocument();
                buyers.addAll(parseBuyerPage(newDoc));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        spigotResource.setBuyers(buyers);
        return buyers;
    }

    private List<Buyer> parseBuyerPage(Document doc) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' hh:mm a", Locale.ENGLISH);
        List<Buyer> buyers = new ArrayList<Buyer>();
        Elements buyersBlocks = doc.select(".memberListItem");
        for (Element buyersBlock : buyersBlocks) {
            try {
                Element memberNameBlock = buyersBlock.select("div.member").first();
                SpigotBuyer buyer = new SpigotBuyer();
                Element purchaseElement = buyersBlock.select("div.muted").first();
                if (purchaseElement != null) {
                    String purchaseString = purchaseElement.text();
                    if (purchaseString.contains("Purchased")) {
                        String regexPattern = "Purchased For: (.*?) ([a-zA-Z][a-zA-Z][a-zA-Z])";
                        Pattern p = Pattern.compile(regexPattern);
                        Matcher m = p.matcher(purchaseString);
                        if (m.find()) {
                            double price = Double.parseDouble(m.group(1));
                            String currency = m.group(2);
                            buyer.setPurchaseCurrency(currency);
                            buyer.setPurchasePrice(price);
                        }
                    }
                }
                try {
                    Element purchaseDateElement = buyersBlock.select(".DateTime.muted").first();
                    if (purchaseDateElement != null) {
                        if (purchaseDateElement.hasAttr("data-time")) {
                            Date date = new Date(Long.parseLong(purchaseDateElement.attr("data-time")) * 1000);
                            buyer.setPurchaseDate(date);
                        } else {
                            String title = purchaseDateElement.attr("title");
                            Date date = sdf.parse(title);
                            buyer.setPurchaseDate(date);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Elements userNameElements = memberNameBlock.select("a.username");
                if (userNameElements.size() == 0) {
                    continue;
                }
                Element userElement = userNameElements.get(0);
                buyer.setUsername(userElement.text());
                String userIdStr = StringUtils.getStringBetween(userElement.attr("href"), "\\.(.*?)/");
                if (userIdStr.equals("")) {
                    userIdStr = StringUtils.getStringBetween(userElement.attr("href"), "/(.*?)/");
                }
                buyer.setUserId(Integer.parseInt(userIdStr));
                buyers.add(buyer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return buyers;
    }

    public void addBuyer(PremiumResource resource, User user, User buyer) throws ConnectionFailedException {
        addBuyer(resource, user, buyer.getUsername());
    }

    public void addBuyer(PremiumResource resource, User user, int userid) throws ConnectionFailedException, PermissionException {
        User buyer = SpigotSite.getAPI().getUserManager().getUserById(userid);
        addBuyer(resource, user, buyer);
    }

    public void addBuyer(PremiumResource resource, User user, String username) throws ConnectionFailedException {
        addBuyers(resource, user, new String[]{username});
    }

    public void addBuyers(PremiumResource resource, User user, List<User> buyers) throws ConnectionFailedException {
        String[] usernames = new String[buyers.size()];
        for (int i = 0; i < buyers.size(); i++)
            usernames[i] = buyers.get(i).getUsername();
        addBuyers(resource, user, usernames);
    }

    public void addBuyers(PremiumResource resource, User user, String[] usernames) throws ConnectionFailedException {
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/" + resource.getResourceId() + "/add-buyer";
            Map<String, String> params = new HashMap<String, String>();
            String usernamesStr = "";
            for (int i = 0; i < usernames.length; i++)
                usernamesStr += usernames[i] + ",";
            params.put("usernames", usernamesStr);
            params.put("_xfRequestUri", "%2Fresources%2" + resource.getResourceId() + "%2Fadd-buyer");
            params.put("_xfToken", ((SpigotUser) user).getToken());
            params.put("_xfResponseType", "json");
            params.put("_xfNoRedirect", "1");
            params.put("save", "Save+Changes");
            params.put("_xfConfirm", "1");
            params.put("redirect", "/");

            HTTPResponse res = Request.post(url, ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeBuyer(PremiumResource premiumResource, User user, int buyer) throws ConnectionFailedException {
        // Removed by Spigot
        try {
            String url = SpigotSiteCore.getBaseURL() + "resources/" + premiumResource.getResourceId() + "/delete-buyer";
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(buyer));
            params.put("_xfToken", ((SpigotUser) user).getToken());
            params.put("_xfResponseType", "json");
            params.put("_xfNoRedirect", "1");
            params.put("_xfConfirm", "1");
            params.put("redirect", "/");

            HTTPResponse res = Request.post(url, ((SpigotUser) user).getCookies(), params);
            // Handle two step
            res = SpigotUserManager.handleTwoStep(res, (SpigotUser) user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
