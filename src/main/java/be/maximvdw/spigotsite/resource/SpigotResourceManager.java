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
import be.maximvdw.spigotsite.utils.StringUtils;

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

	public List<Resource> getPurchasedResources(User user) {
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
		List<ResourceCategory> resourceCategories = new ArrayList<ResourceCategory>();
		try {
			String url = "http://www.spigotmc.org/resources/";
			Map<String, String> params = new HashMap<String, String>();

			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.GET)
					.data(params)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			Element categoryList = doc.select("div.categoryList").first();
			Elements categories = categoryList.select("li");
			for (Element category : categories) {
				Element link = category.select("a").first();
				Element count = category.select("span.count").first();
				SpigotResourceCategory resourceCategory = new SpigotResourceCategory();
				resourceCategory.setCategoryName(link.text());
				resourceCategory
						.setResourceCount(Integer.parseInt(count.text()));
				resourceCategory.setCategoryId(Integer.parseInt(StringUtils
						.getStringBetween(link.attr("href"), "\\.(.*?)/")));
				resourceCategories.add(resourceCategory);
			}
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
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
				String url = "http://www.spigotmc.org/resources/categories/"
						+ category.getCategoryId() + "/?page=" + i;
				Map<String, String> params = new HashMap<String, String>();

				Connection.Response res = Jsoup
						.connect(url)
						.method(Method.GET)
						.data(params)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
						.execute();
				Document doc = res.parse();
				Elements resourceBlocks = doc.select("li.resourceListItem");
				for (Element resourceBlock : resourceBlocks) {
					int id = Integer.parseInt(resourceBlock.id().replace(
							"resource-", ""));
					Element resourceLink = resourceBlock.select("h3.title")
							.get(0).getElementsByTag("a").get(0);
					SpigotResource resource = new SpigotResource(
							resourceLink.text());
					resource.setResourceId(id);
					Element username = resourceBlock.select("a.username")
							.first();
					Element version = resourceBlock.select("span.version")
							.first();
					resource.setLastVersion(version.text());
					SpigotUser user = new SpigotUser();
					user.setUsername(username.text());
					user.setUserId(Integer.parseInt(StringUtils
							.getStringBetween(username.attr("href"),
									"\\.(.*?)/")));
					resource.setAuthor(user);

					resources.add(resource);
				}
			}
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resources;
	}

}
