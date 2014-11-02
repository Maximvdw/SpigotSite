package be.maximvdw.spigotsite.user;

import java.util.HashMap;
import java.util.Map;

import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.AuthenticationFailureException;
import be.maximvdw.spigotsite.ui.SendConsole;
import be.maximvdw.spigotsite.utils.HttpResponse;
import be.maximvdw.spigotsite.utils.HttpUtils;

public class SpigotUserManager implements UserManager {

	public User getUserById(int userid) {
		return getUserById(userid, null);
	}

	public User getUserById(int userid, User user) {
		try {
			String url = "http://www.spigotmc.org/members/" + userid;
			Map<String, String> params = new HashMap<String, String>();
			HttpResponse response = HttpUtils.sendPostRequest(url, params,
					user == null ? null : ((SpigotUser) user).getCookies());
		} catch (Exception ex) {

		}

		return null;
	}

	public User authenticate(String username, String password)
			throws AuthenticationFailureException {
		try {
			String url = "http://www.spigotmc.org/login/login";
			Map<String, String> params = new HashMap<String, String>();
			// Login parameters
			params.put("login", username);
			params.put("password", password);

			HttpResponse response = HttpUtils
					.sendPostRequest(url, params, null);

			SpigotUser user = new SpigotUser(username);
			user.setCookies(response.getCookies());
			SendConsole.info(response.getSource());

			return user;
		} catch (Exception ex) {

		}

		return null;
	}

	public void logOff(User user) {
		// TODO Auto-generated method stub

	}

}
