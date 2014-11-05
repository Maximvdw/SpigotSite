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
	private List<ResourceCategory> resourceCategories = new ArrayList<ResourceCategory>();

	public Resource getResourceById(int resourceid) {
		return getResourceById(resourceid, null);
	}

	public Resource getResourceById(int resourceid, User user) {
		try {
			String url = "http://www.spigotmc.org/resources/" + resourceid;
			Map<String, String> params = new HashMap<String, String>();
			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.GET)
					.data(params)
					.cookies(
							user == null ? new HashMap<String, String>()
									: ((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();

			SpigotResource resource = new SpigotResource();
			String resourceName = doc.title().replace(
					" | SpigotMC - High Performance Minecraft", "");
			resource.setResourceName(resourceName);
			resource.setResourceId(resourceid);

			Element resourceInfo = doc.select("div.resourceInfo").get(0);
			resource.setLastVersion(resourceInfo.select("span.muted").get(0)
					.text());

			Element downloadLink = doc.select("label.downloadButton").get(0)
					.select("a.inner").get(0);
			resource.setDownloadURL("http://www.spigotmc.org/"
					+ downloadLink.attr("href"));

			Element categoryLink = doc.select("a.crumb").last();

			Element author = doc.select("dl.author").first();
			SpigotUser authorUser = new SpigotUser();
			authorUser.setUsername(author.select("a").first().text());
			authorUser.setUserId(Integer.parseInt(StringUtils.getStringBetween(
					author.select("a").first().attr("href"), "\\.(.*?)/")));
			resource.setAuthor(authorUser);
			return resource;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public List<Resource> getResourcesByUser(User user) {
		List<Resource> resources = getResourcesByUser(user.getUserId());
		for (Resource resource : resources) {
			((SpigotResource) resource).setAuthor(user);
		}
		return resources;
	}

	public List<Resource> getResourcesByUser(int userid) {
		List<Resource> createdResources = new ArrayList<Resource>();
		try {
			String url = "http://www.spigotmc.org/resources/authors/" + userid;
			Map<String, String> params = new HashMap<String, String>();

			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.GET)
					.data(params)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			String username = StringUtils
					.getStringBetween(doc.title(),
							"Resources from (.*?) | SpigotMC - High Performance Minecraft");
			SpigotUser user = new SpigotUser(username);
			user.setUserId(userid);

			Elements resourceBlocks = doc.select("li.resourceListItem");
			for (Element resourceBlock : resourceBlocks) {
				int id = Integer.parseInt(resourceBlock.id().replace(
						"resource-", ""));
				Element resourceLink = resourceBlock.select("h3.title").get(0)
						.getElementsByTag("a").get(0);
				SpigotResource resource = new SpigotResource(
						resourceLink.text());
				resource.setAuthor(user);
				resource.setResourceId(id);
				createdResources.add(resource);
			}
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return createdResources;
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
		if (this.resourceCategories.size() > 0)
			return this.resourceCategories;
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

			this.resourceCategories = resourceCategories;
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

	public ResourceCategory getResourceCategoryById(int id) {
		if (this.resourceCategories.size() == 0)
			getResourceCategories();
		for (ResourceCategory category : resourceCategories) {
			if (category.getCategoryId() == id)
				return category;
		}
		return null;
	}

}
