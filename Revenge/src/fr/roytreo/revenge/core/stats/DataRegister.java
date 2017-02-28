package fr.roytreo.revenge.core.stats;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import fr.roytreo.revenge.core.handler.Database;
import fr.roytreo.revenge.core.handler.MySQL;
import fr.roytreo.revenge.core.handler.URLManager;

public class DataRegister {

	public MySQL database;

	public DataRegister(final Plugin plugin, final Boolean localhost) {
		if (SpigotConfig.disableStatSaving)
			return;
		String java_column;
		Boolean javaCol = false;
		try {
			java_column = new URLManager("http://%BASE_URL%/home/core/java_column.txt", localhost).read();
			if (java_column.equals("true"))
				javaCol = true;
		} catch (Exception e) {
			return;
		}
		String content;
		try {
			content = new URLManager("http://%BASE_URL%/home/core/database.txt", localhost).read();
		} catch (Exception e) {
			return;
		}
		final String[] contentSplitted = content.split("_");
		this.database = new MySQL(plugin, (localhost ? "localhost" : URLManager.Values.BASE_URL.getValue()),
				contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4]);
		String serverIP;
		try {
			serverIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			serverIP = "unknown";
		}
		final String server_ip = serverIP;
		final Integer server_port = Bukkit.getServer().getPort();
		final String country = Locale.getDefault().getDisplayCountry(Locale.ENGLISH);
		final String server_location = (country.contains("?") ? "unknown" : country);
		final String ver = Bukkit.getServer().getVersion();
		final String[] verSplitted = ver.split(" ");
		final String server_version = "spigot-" + verSplitted[2].replace(")", "");
		final String os_name = System.getProperty("os.name");
		final String os_arch = System.getProperty("os.arch");
		final String os_version = System.getProperty("os.version");
		final String java_version = System.getProperty("java.version");
		final String plugin_version = plugin.getDescription().getVersion();
		File spigotFile = new File("spigot.yml");
		FileConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
		spigotConfig.options().copyDefaults(true);
		String id = "X";
		try {
			if (!spigotConfig.getString("stats.server-id").equals(null))
				id = spigotConfig.getString("stats.server-id");

		} catch (NullPointerException ex) {
			String uuid = UUID.randomUUID().toString();
			while (!isValidID(plugin, uuid, localhost, this.database)) {
				uuid = UUID.randomUUID().toString();
			}
			id = uuid;
			spigotConfig.set("stats.server-id", uuid);
			try {
				spigotConfig.save(spigotFile);
			} catch (IOException e) {
				return;
			}
		}
		try {
			database.openConnection();
			ResultSet res = database.querySQL("SELECT * FROM data WHERE server_id='" + id + "'");
			if (!res.first()) {
				database.updateSQL(
						"INSERT INTO data(server_id, server_ip, server_port, server_location, server_version, os_name, os_arch, os_version, "
								+ (javaCol ? "java_version, " : "")
								+ "plugin_name, plugin_version, updated_at, created_at) VALUES('" + id + "', '"
								+ server_ip + "', " + server_port + ", '" + server_location + "', '" + server_version
								+ "', '" + os_name + "', '" + os_arch + "', '" + os_version + "', '"
								+ (javaCol ? java_version + "', '" : "") + plugin.getDescription().getName() + "', '"
								+ plugin_version + "', NOW(), NOW())");
			} else {
				database.updateSQL("UPDATE data SET server_ip='" + server_ip + "', server_port=" + server_port
						+ ", server_location='" + server_location + "', server_version='" + server_version
						+ "', os_version='" + os_version + "', "
						+ (javaCol ? "java_version='" + java_version + "', " : "") + "plugin_name='"
						+ plugin.getDescription().getName() + "', plugin_version='" + plugin_version
						+ "', updated_at=NOW() WHERE server_id='" + id + "'");
			}
		} catch (ClassNotFoundException | SQLException e) {
			return;
		}
	}

	public String getMAC() throws SocketException {
		String firstInterface = null;
		Map<String, String> addressByNetwork = new HashMap<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface network = networkInterfaces.nextElement();

			byte[] bmac = network.getHardwareAddress();
			if (bmac != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < bmac.length; i++) {
					sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
				}

				if (sb.toString().isEmpty() == false) {
					addressByNetwork.put(network.getName(), sb.toString());
				}

				if (sb.toString().isEmpty() == false && firstInterface == null) {
					firstInterface = network.getName();
				}
			}
		}

		if (firstInterface != null) {
			return addressByNetwork.get(firstInterface);
		}

		return "null";
	}

	public static Boolean isValidID(Plugin plugin, final String id, final Boolean localhost, final Database db) {
		Boolean retur = false;
		try {
			db.openConnection();
			ResultSet res = db.querySQL("SELECT * FROM data WHERE server_id='" + id + "'");
			if (!res.first())
				retur = true;
		} catch (ClassNotFoundException | SQLException e) {
			return retur;
		}
		return retur;
	}
}
