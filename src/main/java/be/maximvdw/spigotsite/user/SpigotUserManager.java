package be.maximvdw.spigotsite.user;

import java.security.GeneralSecurityException;
import java.util.*;

import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import be.maximvdw.spigotsite.utils.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.jsoup.nodes.Document;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.UserRank;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.http.HTTPResponse;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.utils.StringUtils;
import org.jsoup.nodes.Element;

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
        return authenticate(username, password, null);
    }

    public User authenticate(String username, String password, String totpSecret)
            throws InvalidCredentialsException, TwoFactorAuthenticationException {
        try {
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
            HTTPResponse res = Request.post(url,
                    SpigotSiteCore.getBaseCookies(), params);
            if (res.getHtml().contains("Incorrect password. Please try again.")) {
                // Password incorrect

                throw new InvalidCredentialsException();
            }
            Document doc = res.getDocument();
            Element totpField = doc.getElementById("ctrl_totp_code");
            if (totpField != null && totpSecret == null) {
                throw new TwoFactorAuthenticationException();
            }

            SpigotUser user = new SpigotUser(username);
            user.setCookies(res.getCookies());
            user.setTotpSecret(totpSecret);
            if (totpField != null) {
                user = totpVerification(user);
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
            return user;
        } catch (TwoFactorAuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private SpigotUser totpVerification(SpigotUser user) throws GeneralSecurityException {
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
        // Kill cookies
        SpigotUser spigotUser = (SpigotUser) user;
        spigotUser.getCookies().clear();
    }

    public boolean isLoggedIn(User user) {
        try {
            HTTPResponse res = Request.get(SpigotSiteCore.getBaseURL(),
                    ((SpigotUser) user).getCookies(), new HashMap<String, String>());
            Document doc = res.getDocument();
            ((SpigotUser) user).setUsername(doc.select("a.username.NoOverlay").first().text());
            return true;
        }catch (Exception ex){
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

    public List<User> getUsersByName(String name) {
        List<User> users = new ArrayList<User>();
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

            Document doc = res.getDocument();
            System.out.println(doc.text());

        } catch (Exception ex) {

        }
        return users;
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
}
