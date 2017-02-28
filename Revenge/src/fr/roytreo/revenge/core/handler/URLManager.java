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

import org.bukkit.command.CommandSender;

import fr.roytreo.revenge.core.RevengePlugin;
import net.md_5.bungee.api.ChatColor;

public class URLManager {

	private URL url;
	private static String latestVersion;

	static {
		latestVersion = "null";
	}

	public enum Values {
		BASE_URL("roytreo28.ddns.net"), 
		SHEEPWARS_PATH("SheepWars_Released"), 
		BUNGEE_ANNOUNCE_PATH("Bungee_Announce"), 
		NIGHT_CLUB_PATH("Night_Club"), 
		SONAR_HEARING_PATH("Sonar_Hearing"), 
		REVENGE_PATH("Revenge");

		private String[] values;

		private Values(String... s) {
			this.values = s;
		}

		public String getValue() {
			return this.values[0];
		}
	}

	public URLManager(String url, Boolean localhost) throws MalformedURLException {
		new URL(url); // Check if URL is valid
		String urlCopy = url;
		String[] urlSplit = url.split("/");
		if (localhost && (!urlSplit[2].equals("localhost"))) {
			urlCopy = urlCopy.replaceAll(urlSplit[2].toString(), "localhost");
		}
		for (Values val : Values.values()) {
			if (urlCopy.contains("%" + val.toString() + "%"))
				urlCopy = urlCopy.replaceAll("%" + val.toString() + "%", val.getValue());
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

	public boolean download(RevengePlugin plugin, String newVersion, CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Updating " + plugin.getDescription().getName() + " ...");
		FileOutputStream fos = null;
		try {
			ReadableByteChannel rbc = Channels.newChannel(this.url.openStream());
			fos = new FileOutputStream("plugins/" + plugin.getDescription().getName() + "-" + newVersion + ".jar");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Update aborted: " + e.getMessage());
			return false;
		} finally {
			try {
				fos.close();
				sender.sendMessage(ChatColor.GREEN + plugin.getDescription().getName() + " is now up to date. Server will shutdown in 5 seconds.");
				return true;
			} catch (NullPointerException | IOException e) {
				sender.sendMessage(ChatColor.RED + "Update aborted: " + e.getMessage());
			}
		}
		return false;
	}

	public static Boolean checkVersion(String version, Boolean localhost, Values URLPath) {
		Boolean isUpdated = true;
		String content;
		try {
			content = new URLManager("http://%BASE_URL%/home/projects/plugins/" + URLPath.getValue() + "/version.txt",
					localhost).read();
			if (!content.equals(version)) {
				latestVersion = content;
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

	public static boolean update(RevengePlugin plugin, String newVersion, CommandSender sender, Boolean localhost,
			Values URLPath) {
		try {
			return new URLManager("http://%BASE_URL%/home/projects/plugins/" + URLPath.getValue() + "/latest.jar", localhost).download(plugin, newVersion, sender);
		} catch (MalformedURLException e) {
			sender.sendMessage(ChatColor.RED + "Update aborted: " + e.getMessage());
			return false;
		}
	}
}
