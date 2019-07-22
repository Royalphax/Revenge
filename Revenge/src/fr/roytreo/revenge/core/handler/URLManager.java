package fr.roytreo.revenge.core.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.plugin.Plugin;

/**
 * @author Roytreo28
 */
public class URLManager {

	private URL url;
	private static String latestVersion;

	static {
		latestVersion = "null";
	}

	public enum Link {
		BASE_URL("roytreo28.ddns.net"),
		
		DB_ACCESS("https://roytreo28.github.io/Revenge/auto-updater/db_acc.txt"), 
		DB_SEQUENCE("https://roytreo28.github.io/Revenge/auto-updater/db_seq.txt"), 
		
		GITHUB_PATH("https://roytreo28.github.io/Revenge/auto-updater"), 
		BUNGEE_ANNOUNCE_PATH("http://roytreo28.ddns.net/home/projects/plugins/Revenge");

		private String url;

		private Link(String url) {
			this.url = url;
		}

		public String getURL() {
			return this.url;
		}
	}
	
	public URLManager(Link link, Boolean localhost) throws MalformedURLException {
		this(link.getURL(), localhost);
	}

	public URLManager(String url, Boolean localhost) throws MalformedURLException {
		String urlCopy = url;
		String[] urlSplit = url.split("/");
		if (localhost && (!urlSplit[2].equals("localhost"))) {
			urlCopy = urlCopy.replaceAll(urlSplit[2].toString(), "localhost");
		}
		for (Link link : Link.values()) {
			if (urlCopy.contains("%" + link.toString() + "%"))
				urlCopy = urlCopy.replaceAll("%" + link.toString() + "%", link.getURL());
		}
		this.url = new URL(urlCopy);
	}

	public String read() throws IOException {
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		String body = new String(baos.toByteArray(), encoding);
		return body;
	}

	public boolean download(Plugin plugin, String newVersion) {
		plugin.getLogger().info("Updating " + plugin.getDescription().getName() + " ...");
		FileOutputStream fos = null;
		try {
			ReadableByteChannel rbc = Channels.newChannel(this.url.openStream());
			fos = new FileOutputStream("plugins/" + plugin.getDescription().getName() + "-" + newVersion + ".jar");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			plugin.getLogger().warning("Update aborted: " + e.getMessage());
		} finally {
			try {
				fos.close();
				plugin.getLogger().info(plugin.getDescription().getName() + " is now up-to-date.");
				return true;
			} catch (NullPointerException | IOException e) {
				plugin.getLogger().warning("Update aborted: " + e.getMessage());
			}
		}
		return false;
	}

	public static Boolean checkVersion(String version, Boolean localhost, Link URLPath) {
		Boolean isUpdated = true;
		String content;
		try {
			content = new URLManager(URLPath.getURL() + "/version.txt", localhost).read();
			if (!content.trim().equals(version.split("-")[0].trim())) {
				latestVersion = content.trim();
				isUpdated = false;
			}
		} catch (IOException e) {
			return true;
		}
		return isUpdated;
	}

	public static String getLatestVersion() {
		return latestVersion;
	}

	public static boolean update(Plugin plugin, String newVersion, Boolean localhost, Link URLPath) {
		try {
			return new URLManager(URLPath.getURL() + "/latest.jar", localhost).download(plugin, newVersion.trim());
		} catch (MalformedURLException e) {
			plugin.getLogger().warning("Update aborted: " + e.getMessage());
		}
		return false;
	}
}
