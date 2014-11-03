package be.maximvdw.spigotsite.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
		List<Resource> boughtResources = new ArrayList<Resource>();
		try {
			String url = "http://www.spigotmc.org/resources/purchased";
			Map<String, String> params = new HashMap<String, String>();

			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.GET)
					.data(params)
					.cookies(((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			Elements resourceBlocks = doc.select("li.resourceListItem");
			for (Element resourceBlock : resourceBlocks) {
				int id = Integer.parseInt(resourceBlock.id().replace(
						"resource-", ""));
				Element resourceLink = resourceBlock.select("h3.title").get(0)
						.getElementsByTag("a").get(0);
				SpigotResource resource = new SpigotResource(
						resourceLink.text());
				resource.setResourceId(id);
				boughtResources.add(resource);
			}
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return boughtResources;
	}

	public List<ResourceCategory> getResourceCategories() {
		// TODO Auto-generated method stub
		return null;
	}

}
