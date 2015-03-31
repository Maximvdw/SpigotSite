package be.maximvdw.spigotsite.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.UserRank;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.http.HTTPResponse;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.utils.StringUtils;

public class SpigotUserManager implements UserManager {

	public User getUserById(int userid) {
		return getUserById(userid, null);
	}

	public User getUserById(int userid, User user) {
		try {
			String url = "http://www.spigotmc.org/members/" + userid;
			Map<String, String> params = new HashMap<String, String>();
			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.POST)
					.data(params)
					.cookies(
							user == null ? SpigotSiteCore.getBaseCookies()
									: ((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			SpigotUser reqUser = new SpigotUser();
			reqUser.setUsername(doc.select("h1.username").get(0).text());
			reqUser.setUserId(userid);
			if (doc.select("dl.lastActivity").size() != 0)
				if (doc.select("dl.lastActivity").get(0).select("dd").size() != 0)
					reqUser.setLastActivity(doc.select("dl.lastActivity")
							.get(0).select("dd").get(0).text());
			return reqUser;
		} catch (Exception ex) {

		}

		return null;
	}

	public User authenticate(String username, String password)
			throws InvalidCredentialsException {
		try {
			String url = "http://www.spigotmc.org/login/login";
			Map<String, String> params = new HashMap<String, String>();
			// Login parameters
			params.put("login", username);
			params.put("password", password);
			params.put("register", "0");
			params.put("remember", "0"); // No need to remember
			params.put("cookie_check", "0"); // Fix error Cookies required
			params.put("_xfToken", "");
			params.put("redirect", "/");
			HTTPResponse res = Request.post(url,
					SpigotSiteCore.getBaseCookies(), params);
			if (res.getHtml().contains("Incorrect password. Please try again.")) {
				// Password incorrect

				throw new InvalidCredentialsException();
			}
			Document doc = res.getDocument();

			SpigotUser user = new SpigotUser(username);
			user.setCookies(res.getCookies());

			// Fetch data
			user.setUsername(doc.select("a.username.NoOverlay").text());
			user.setUserId(Integer.parseInt(StringUtils.getStringBetween(
					res.getHtml(), "member\\?user_id=(.*?)\">Your Content")));
			user.setToken(doc.select("input[name=_xfToken]").get(0)
					.attr("value"));
			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public void logOff(User user) {
		// Kill cookies
		SpigotUser spigotUser = (SpigotUser) user;
		spigotUser.getCookies().clear();
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
			String url = "http://www.spigotmc.org/index.php?members/find&_xfResponseType=json";
			Map<String, String> params = new HashMap<String, String>();
			// Login parameters
			params.put("q", name);
			params.put("_xfToken", "");
			params.put("_xfNoRedirect", "1");
			params.put("_xfResponseType", "json");

			HTTPResponse res = Request.post(url,
					SpigotSiteCore.getBaseCookies(), params);

			Document doc = res.getDocument();
			System.out.println(doc.text());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return users;
	}
}
