package be.maximvdw.spigotsite.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.user.SpigotUser;

public class SpigotResourceManager implements ResourceManager {

	public Resource getResourceById(int resourceid) {
		return getResourceById(resourceid, null);
	}

	public Resource getResourceById(int resourceid, User user) {
		try {
			String url = "http://www.spigotmc.org/resources/" + resourceid;
			Map<String, String> params = new HashMap<String, String>();
			SpigotResource resource = new SpigotResource();

			return resource;
		} catch (Exception ex) {

		}

		return null;
	}

	public List<Resource> getResourcesByUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Resource> getBoughtResources(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ResourceCategory> getResourceCategories() {
		// TODO Auto-generated method stub
		return null;
	}

}
