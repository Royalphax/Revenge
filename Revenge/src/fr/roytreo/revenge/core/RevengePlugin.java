package fr.roytreo.revenge.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.event.entity.EntityDamageByEntity;
import fr.roytreo.revenge.core.event.player.PlayerDamage;
import fr.roytreo.revenge.core.event.player.PlayerDeath;
import fr.roytreo.revenge.core.event.player.PlayerInteractAtEntity;
import fr.roytreo.revenge.core.event.player.PlayerJoin;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.handler.URLManager;
import fr.roytreo.revenge.core.stats.DataRegister;
import fr.roytreo.revenge.core.util.ReflectionUtils;
import fr.roytreo.revenge.core.version.IParticleSpawner;
import fr.roytreo.revenge.core.version.IPathEntity;
import net.md_5.bungee.api.ChatColor;

public class RevengePlugin extends JavaPlugin {
	public IParticleSpawner IParticleSpawner;
	public IPathEntity IPathEntity;
	public Boolean meleeModeEnabled;
	public Double radius;
	public Boolean update;
	public Boolean localhost;
	public Boolean trackedInfoEnabled;
	public Boolean randomBehavior;
	public Boolean animalsBlood;
	public String trackedDescription;
	public String lastDamagerMetadata;
	public String revengeMobMetadata;
	public String revengeTrackedInfoMetadata;
	public ArrayList<World> disableWorlds;
	public HashMap<String, Object> softDepends;
	public Particles.RevengeParticle revengeParticle;
	public static RevengePlugin instance;
	public static final String uid = "%%__USER__%%";
	
	@Override
	@SuppressWarnings("unchecked")
	public void onEnable() {
		instance = this;
		this.localhost = true;
		this.update = false;
		this.lastDamagerMetadata = "revengeLastDamager";
		this.revengeMobMetadata = "revengeMob";
		this.revengeTrackedInfoMetadata = "revengeArmorStand";
		this.disableWorlds = new ArrayList<>();
		this.softDepends = new HashMap<>();
		this.revengeParticle = null;
		
		if (!setupNMS())
		{
			this.getLogger().warning("Revenge doesn't support this version of Spigot. (If you think that's a mistake => contact the developer)");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
		registerListeners(EntityDamageByEntity.class, PlayerDeath.class, PlayerJoin.class, PlayerDamage.class, PlayerInteractAtEntity.class);
		
		setupSoftDepend("PvPManager");
		setupConfig(true);
		
		for (World world : Bukkit.getWorlds())
			for (Entity ent : world.getEntities())
				if (ent instanceof ArmorStand && ent.hasMetadata(this.revengeTrackedInfoMetadata))
					ent.remove();
					
		new BukkitRunnable()
		{
			public void run()
			{
				if (!URLManager.checkVersion(getDescription().getVersion(), localhost, URLManager.Values.REVENGE_PATH))
				{
					getLogger().warning("A new version more efficient of the plugin is available. Do '/rev update' to automatically update the plugin.");
					update = true;
				}
				new DataRegister(instance, localhost);
			}
		}.runTaskAsynchronously(this);
	}
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelAllTasks();
	}

	private void registerListeners(@SuppressWarnings("unchecked") Class<? extends EventListener>... classes) {
		try {
			for (Class<? extends EventListener> clazz : classes) {
				Constructor<? extends EventListener> constructor = clazz.getConstructor(new Class[] { RevengePlugin.class });
				Bukkit.getPluginManager().registerEvents((Listener) constructor.newInstance(new Object[] { this }), this);
			}
		} catch (Throwable ex) {
			try {
				throw ex;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("revenge"))
		{
			if (sender.isOp())
			{
				if (args.length == 1)
				{
					if (args[0].equals("reload")) {
						setupConfig(false);
						sender.sendMessage(ChatColor.GREEN + "Revenge config was successfully reloaded!");
					} else if (args[0].equals("update")) {
						if (this.update) {
							sender.sendMessage(ChatColor.AQUA + "Stay informed about what the update bring new at https://www.spigotmc.org/resources/revenge-1-7-1-8-1-9-1-10.18235/updates");
							sender.sendMessage(ChatColor.GOLD + "The updating task will start in 10 seconds, then your server will shutdown to complete the updating process.");
							new BukkitRunnable()
							{
								public void run()
								{
									if (URLManager.update(instance, URLManager.getLatestVersion(), sender, localhost, URLManager.Values.REVENGE_PATH)) {
										new BukkitRunnable()
										{
											public void run()
											{
												getFile().delete();
												getFile().deleteOnExit();
												Bukkit.getServer().shutdown();
											}
										}.runTaskLater(instance, 20*5);
									}
								}
							}.runTaskLater(this, 20*10);
						} else {
							sender.sendMessage(ChatColor.RED + "Revenge is already up to date.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Usage: /rev [reload/update]");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Usage: /rev [reload/update]");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You are not permitted to do that.");
			}
		}
		return super.onCommand(sender, command, label, args);
	}
	
	public void setupConfig(Boolean onStart)
	{
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			this.getLogger().info("Thanks " + getUsernameById(uid) + " for having downloaded Revenge ! If you find any bugs, feel free to contact the developer.");
			this.saveDefaultConfig();
		}
		if (!onStart) this.reloadConfig();
		this.radius = getConfig().getDouble("melee-mode.radius");
		this.meleeModeEnabled = getConfig().getBoolean("melee-mode.enable");
		this.trackedInfoEnabled = getConfig().getBoolean("tracked-info.enable");
		this.trackedDescription = ChatColor.translateAlternateColorCodes('&', getConfig().getString("tracked-info.description"));
		this.randomBehavior = getConfig().getBoolean("random-behavior");
		this.animalsBlood = getConfig().getBoolean("animals-blood");

        setupParticle();
		setupDisableWorlds();
		
		if (onStart) {
			ConfigurationSection section = this.getConfig().getConfigurationSection("moblist");
			for (String s : section.getKeys(false))
				new Mob(s, this);
		} else {
			Mob.updateMobs(this);
		}
	}
	
	public void setupParticle()
	{
		try {
			String particle = "";
			particle = getConfig().getString("hit-particles");
			if (particle.equals("null") || particle.equals(""))
			{
				return;
			}
			this.revengeParticle = new Particles.RevengeParticle(this, particle);
		} catch (NullPointerException | IllegalArgumentException ex) {
			getLogger().warning("An error occured when reading the 'hit-particles' field in config.yml. Please review your syntax.");
			return;
		}
	}
	
	public void setupDisableWorlds()
	{
		ArrayList<String> worlds = new ArrayList<>();
		for (String world : getConfig().getStringList("disable-worlds"))
		{
			if (Bukkit.getWorld(world) != null)
			{
				this.disableWorlds.add(Bukkit.getWorld(world));
				worlds.add(world);
			} else {
				getLogger().info("Disable Worlds: It appears that you have no world named \"" + world + "\" on your server.");
			}
		}
		if (!worlds.isEmpty())
			getLogger().info("Disable Worlds: " + worlds.toString().replace("[", "").replace("]", ""));
	}

	public Boolean setupNMS()
	{
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException unsuportedVersion) {
			return false;
		}
		
		try {
			IParticleSpawner = (IParticleSpawner) ReflectionUtils.instantiateObject(Class.forName("fr.roytreo.revenge." + version + ".ParticleSpawner"));
			IPathEntity = (IPathEntity) ReflectionUtils.instantiateObject(Class.forName("fr.roytreo.revenge." + version + ".PathEntity"));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | ClassNotFoundException e) {
			getLogger().warning(e.getMessage());
			return false;
		}
		
		return (IParticleSpawner != null && IPathEntity != null);
	}
	
	public String getUsernameById(String uid) {
		try {
			Integer userid = Integer.parseInt(uid);
			try {
				URL url = new URL("https://www.spigotmc.org/members/" + userid);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String code = "", line = "";
				while ((line = br.readLine()) != null) {
					code = code + line;
				}
				
				return code.split("<title>")[1].split("</title>")[0].split(" | ")[0];
				
			} catch (IOException e) {
				this.getLogger().severe(e.getMessage());
				return "";
			}
		} catch (NumberFormatException ex) {
			return "Robot";
		}
	}
	
	public Boolean setupSoftDepend(String softDepend)
	{
		try {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(softDepend);
			if (plugin == null) return false;
			softDepends.put(softDepend, ReflectionUtils.instantiateObject(Class.forName("fr.roytreo.revenge.core.softdepend." + softDepend)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Object getSoftDepend(String softDepend)
	{
		return this.softDepends.get(softDepend);
	}
}
