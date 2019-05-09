package be.maximvdw.spigotsite.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.exceptions.PermissionException;
import be.maximvdw.spigotsite.api.forum.ProfilePost;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserStatistics;

public class SpigotUser implements User, Serializable {
    private int id = 0;
    private String username = "";
    private Map<String, String> cookies = new HashMap<String, String>();
    private String totpSecret = null;
    private boolean authenticated = false;
    private UserStatistics statistics = null;
    private String token = "";
    private long loginDate = new Date().getTime();
    private String lastActivity = "";

    public SpigotUser() {

    }

    public SpigotUser(String username) {
        setUsername(username);
    }

    public SpigotUser(int id) {
        setUserId(id);
    }

    public int getUserId() {
        return id;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
        this.authenticated = true;
    }

    public List<Resource> getPurchasedResources()
            throws ConnectionFailedException {
        return SpigotSite.getAPI().getResourceManager()
                .getPurchasedResources(this);
    }

    public List<Resource> getCreatedResources() throws ConnectionFailedException {
        return SpigotSite.getAPI().getResourceManager()
                .getResourcesByUser(this);
    }

    public UserStatistics getUserStatistics() {
        return statistics;
    }

    public void setUserStatistics(UserStatistics statistics) {
        this.statistics = statistics;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpigotUser)) {
            return false;
        }
        User user = (User) obj;
        if (user.getUserId() != 0 && getUserId() != 0) {
            if (user.getUserId() == getUserId()) {
                return true;
            }
        } else if (user.getUsername().equalsIgnoreCase(getUsername())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Conversation> getConversations() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getCurrentDate() {
        return new Date().getTime();
    }

    public boolean requiresRefresh() {
        long cur = new Date().getTime();
        if (cur > (getLoginDate() + (24 * 60 * 60 * 1000))) {
            return true;
        }
        return false;
    }

    public void refresh() {
        try {
            String url = "http://www.spigotmc.org/";
            Map<String, String> params = new HashMap<String, String>();

            Connection.Response res = Jsoup
                    .connect(url)
                    .cookies(getCookies())
                    .method(Method.GET)
                    .data(params)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
                    .execute();
            Document doc = res.parse();
            setToken(doc.select("input[name=_xfToken]").get(0).attr("value"));
            setLoginDate(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public boolean hasTwoFactorAuthentication() {
        return totpSecret != null;
    }

    public List<ProfilePost> getProfilePosts(User user, int i) throws ConnectionFailedException, PermissionException {
        return SpigotSite.getAPI().getUserManager().getProfilePosts(user,this,i);
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }
}
