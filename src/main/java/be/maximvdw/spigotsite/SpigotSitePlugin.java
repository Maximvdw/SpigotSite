package be.maximvdw.spigotsite;

import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.exceptions.AuthenticationFailureException;

public class SpigotSitePlugin extends JavaPlugin implements SpigotSiteAPI {

	@Override
	public void onEnable() {
		super.onEnable();

		// Set Site API
		SpigotSite.setAPI(this);
	}

	public User authenticate(String username, String password)
			throws AuthenticationFailureException {
		// TODO Auto-generated method stub
		return null;
	}

	public void logOff(User user) {
		// TODO Auto-generated method stub

	}

}
