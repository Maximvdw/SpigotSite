package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.UserRank;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import be.maximvdw.spigotsite.http.HTTPResponse;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.utils.StringUtils;
import be.maximvdw.spigotsite.utils.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpigotUserManager implements UserManager {

    public User getUserById(int userid) {
        return getUserById(userid, null);
    }

    public User getUserById(int userid, User user) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "members/" + userid;
            Map<String, String> params = new HashMap<String, String>();

            HTTPResponse res = Request.get(url,
                    user == null ? SpigotSiteCore.getBaseCookies()
                            : ((SpigotUser) user).getCookies(), params);
            res = handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();
            SpigotUser reqUser = new SpigotUser();
            reqUser.setUsername(doc.select("h1.username").get(0).text());
            reqUser.setUserId(userid);
            if (doc.select("dl.lastActivity").size() != 0)
                if (doc.select("dl.lastActivity").get(0).select("dd").size() != 0)
                    reqUser.setLastActivity(doc.select("dl.lastActivity")
                            .get(0).select("dd").get(0).text());
            return reqUser;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public User authenticate(String username, String password)
            throws InvalidCredentialsException, TwoFactorAuthenticationException {
        SpigotUser user = new SpigotUser(username);
        return authenticate(username, password, user);
    }

    public User authenticate(String username, String password, String totpSecret)
            throws InvalidCredentialsException, TwoFactorAuthenticationException {
        SpigotUser user = new SpigotUser(username);
        user.setTotpSecret(totpSecret);
        return authenticate(username, password, user);
    }

    public User authenticate(String username, String password, User user) throws InvalidCredentialsException, TwoFactorAuthenticationException {
        try {
            SpigotUser spigotUser = ((SpigotUser) user);
            String url = SpigotSiteCore.getBaseURL() + "login/login";
            Map<String, String> params = new HashMap<String, String>();
            // Login parameters
            params.put("login", username);
            params.put("password", password);
            params.put("register", "0");
            params.put("remember", "1"); // No need to remember
            params.put("cookie_check", "1"); // Fix error Cookies required
            params.put("_xfToken", "");
            params.put("redirect", SpigotSiteCore.getBaseURL() + "");
            HTTPResponse res = Request.post(url, spigotUser.getCookies().size() != 0 ? spigotUser.getCookies() :
                    SpigotSiteCore.getBaseCookies(), params);
            if (res.getHtml().contains("Incorrect password. Please try again.")) {
                // Password incorrect
                throw new InvalidCredentialsException();
            }

            if (res.getResponseURL().toString().startsWith("https://www.spigotmc.org/login/two-step")) {
                // Two step verification page
                HTTPResponse totpResponse = res;
                Document doc = res.getDocument();
                Element providerInput = doc.select("input[name=provider").first();
                if (providerInput == null || !providerInput.val().equalsIgnoreCase("totp")) {
                    // Redirect to TOTP Two step (not email or whatever ,...)
                    totpResponse = Request.get("https://www.spigotmc.org/login/two-step?remember=1&provider=totp",
                            spigotUser.getCookies(), new HashMap<String, String>());
                }

                doc = totpResponse.getDocument();

                Element totpField = doc.getElementById("ctrl_totp_code");
                if (totpField != null && spigotUser.getTotpSecret() == null) {
                    throw new TwoFactorAuthenticationException();
                }

                spigotUser.setCookies(totpResponse.getCookies());
                if (totpField != null) {
                    spigotUser = totpVerification(spigotUser);
                    if (spigotUser == null) {
                        throw new TwoFactorAuthenticationException();
                    }
                }
            } else {
                // Fetch data
                Document doc = res.getDocument();

                spigotUser.setUsername(doc.select("a.username.NoOverlay").first().text());
                spigotUser.setUserId(Integer.parseInt(StringUtils.getStringBetween(
                        res.getHtml(), "member\\?user_id=(.*?)\">")));
                spigotUser.setToken(doc.select("input[name=_xfToken]").get(0)
                        .attr("value"));
                spigotUser.setCookies(res.getCookies());
            }
            return spigotUser;
        } catch (TwoFactorAuthenticationException ex) {
            throw ex;
        } catch (InvalidCredentialsException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static SpigotUser totpVerification(SpigotUser user) throws GeneralSecurityException {
        byte[] keyBytes = new Base32().decode(user.getTotpSecret());
        StringBuilder sb = new StringBuilder();
        for (byte b : keyBytes) {
            sb.append(String.format("%02X", b));
        }
        String key = sb.toString().toLowerCase();
        String code = TOTP.generateTOTP(key, 6);
        try {
            String url = SpigotSiteCore.getBaseURL() + "login/two-step";
            Map<String, String> params = new HashMap<String, String>();
            // Login parameters
            params.put("code", code);
            params.put("trust", "1");
            params.put("provider", "totp");
            params.put("_xfConfirm", "1");
            params.put("remember", "1");
            params.put("save", "Confirm");
            params.put("redirect", SpigotSiteCore.getBaseURL());
            HTTPResponse res = Request.post(url,
                    user.getCookies(), params);
            Document doc = res.getDocument();
            user.setCookies(res.getCookies());
            // Fetch data
            user.setUsername(doc.select("a.username.NoOverlay").first().text());
            user.setUserId(Integer.parseInt(StringUtils.getStringBetween(
                    res.getHtml(), "member\\?user_id=(.*?)\">")));
            user.setToken(doc.select("input[name=_xfToken]").get(0)
                    .attr("value"));
            return user;
        } catch (Exception ex) {
            // Error
        }
        return null;
    }

    public void logOff(User user) {
        logOff(user, false);
    }

    public void logOff(User user, boolean force) {
        if (force) {
            // Kill cookies
            SpigotUser spigotUser = (SpigotUser) user;
            spigotUser.getCookies().clear();
        } else {
            try {
                HTTPResponse res = Request.get(SpigotSiteCore.getBaseURL() + "/logout?_xfToken=" + ((SpigotUser) user).getToken(),
                        ((SpigotUser) user).getCookies(), new HashMap<String, String>());
                SpigotUser spigotUser = (SpigotUser) user;
                spigotUser.setCookies(res.getCookies());
                return;
            } catch (Exception ex) {

            }
        }
    }

    public boolean isLoggedIn(User user) {
        try {
            HTTPResponse res = Request.get(SpigotSiteCore.getBaseURL(),
                    ((SpigotUser) user).getCookies(), new HashMap<String, String>());
            res = handleTwoStep(res, (SpigotUser) user);
            Document doc = res.getDocument();
            ((SpigotUser) user).setUsername(doc.select("a.username.NoOverlay").first().text());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean untrustThisDevice(User user) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("_xfToken", ((SpigotUser) user).getToken());
            params.put("provider", "backup");
            HTTPResponse res = Request.post(SpigotSiteCore.getBaseURL() + "account/two-step/trusted-disable",
                    ((SpigotUser) user).getCookies(), params);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<UserRank> getUserRanks() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<User> getUsersByRank(UserRank rank) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getUsernamesByName(String name) {
        List<String> users = new ArrayList<String>();
        try {
            String url = SpigotSiteCore.getBaseURL() + "index.php?members/find&_xfResponseType=json";
            Map<String, String> params = new HashMap<String, String>();
            // Login parameters
            params.put("q", name);
            params.put("_xfNoRedirect", "1");
            params.put("_xfRequestUri", "/members/");
            params.put("_xfResponseType", "json");

            HTTPResponse res = Request.post(url,
                    SpigotSiteCore.getBaseCookies(), params);
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(res.getDocument().text());
            JSONObject results = (JSONObject) root.get("results");
            for (Object userObj : results.values()) {
                String username = (String) ((JSONObject) userObj).get("username");
                users.add(username);
            }
        } catch (Exception ex) {

        }
        return users;
    }

    public User getUserByName(String s) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "members/?username=" + s;
            Map<String, String> params = new HashMap<String, String>();

            HTTPResponse res = Request.get(url, SpigotSiteCore.getBaseCookies(), params);
            Document doc = res.getDocument();
            SpigotUser reqUser = new SpigotUser();
            reqUser.setUsername(s);
            Element importantMessage = doc.getElementsByClass("importantMessage").first();
            if (importantMessage != null) {
                return null;
            }
            Element topLinkElement = doc.getElementsByClass("topLink").first();
            String linkProfile = topLinkElement.getElementsByTag("a").first().attr("href");
            String userIdStr = linkProfile.substring(0, linkProfile.lastIndexOf("/"));
            userIdStr = userIdStr.substring(userIdStr.lastIndexOf("/"));
            userIdStr = userIdStr.substring(userIdStr.indexOf(".") + 1);
            reqUser.setUserId(Integer.parseInt(userIdStr));
            if (doc.select("dl.lastActivity").size() != 0)
                if (doc.select("dl.lastActivity").get(0).select("dd").size() != 0)
                    reqUser.setLastActivity(doc.select("dl.lastActivity")
                            .get(0).select("dd").get(0).text());
            return reqUser;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<User> getOnlineUsers() {
        List<User> users = new ArrayList<User>();
        try {
            for (int i = 1; i <= 40; i++) {
                int pagenr = i;
                String url = SpigotSiteCore.getBaseURL() + "online/?type=registered&page="
                        + pagenr;
            }

        } catch (Exception ex) {

        }
        return users;
    }

    /**
     * Handle two step verification
     *
     * @param originalResponse HTTP response that could possible be a 2FA
     * @param user             User to use
     * @return success
     */
    public static HTTPResponse handleTwoStep(HTTPResponse originalResponse, SpigotUser user) throws TwoFactorAuthenticationException {
        if (user == null) {
            return originalResponse; // No user so no need to handle 2fa
        }
        if (originalResponse.getResponseURL().toString().startsWith("https://www.spigotmc.org/login/two-step")) {
            // Two step verification page
            HTTPResponse res = originalResponse;
            Document doc = res.getDocument();
            Element providerInput = doc.select("input[name=provider").first();
            if (providerInput == null || !providerInput.val().equalsIgnoreCase("totp")) {
                // Redirect to TOTP Two step (not email or whatever ,...)
                res = Request.get("https://www.spigotmc.org/login/two-step?remember=1&provider=totp",
                        user.getCookies(), new HashMap<String, String>());
            }

            doc = res.getDocument();
            Element totpField = doc.getElementById("ctrl_totp_code");
            if (totpField != null && user.getTotpSecret() == null) {
                throw new TwoFactorAuthenticationException();
            }

            user.setCookies(res.getCookies());
            user.setTotpSecret(user.getTotpSecret());
            if (totpField != null) {
                try {
                    user = totpVerification(user);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (user == null) {
                    throw new TwoFactorAuthenticationException();
                }
            } else {
                // Fetch data
                user.setUsername(doc.select("a.username.NoOverlay").first().text());
                user.setUserId(Integer.parseInt(StringUtils.getStringBetween(
                        res.getHtml(), "member\\?user_id=(.*?)\">")));
                user.setToken(doc.select("input[name=_xfToken]").get(0)
                        .attr("value"));
            }

            return originalResponse.getOriginalRequest().execute();
        } else {
            return originalResponse; // No need to handle two step
        }
    }
}
