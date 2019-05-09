package fr.roytreo.revenge.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import fr.roytreo.revenge.core.event.player.PlayerMove;
import fr.roytreo.revenge.core.event.server.PluginDisable;
import fr.roytreo.revenge.core.event.server.PluginEnable;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.handler.URLManager;
import fr.roytreo.revenge.core.hook.base.HookManager;
import fr.roytreo.revenge.core.hook.base.Hooks;
import fr.roytreo.revenge.core.stats.DataRegister;
import fr.roytreo.revenge.core.util.ReflectionUtils;
import fr.roytreo.revenge.core.version.I13Helper;
import fr.roytreo.revenge.core.version.INMSUtils;
import fr.roytreo.revenge.core.version.IParticleSpawner;
import fr.roytreo.revenge.core.version.Pre13Helper;
import fr.roytreo.revenge.v1_13_R1.Post13Helper;
import net.md_5.bungee.api.ChatColor;

public class RevengePlugin extends JavaPlugin {

	public static String user_id = "%%__USER__%%";
	public static String download_id = "%%__NONCE__%%";

	public IParticleSpawner IParticleSpawner;
	public INMSUtils INMSUtils;
	public I13Helper I13Helper;
	public Boolean meleeModeEnabled;
	public Boolean onlySameSpecies;
	public Boolean update;
	public Boolean localhost;
	public Boolean trackedInfoEnabled;
	public Boolean randomBehavior;
	public Boolean animalsBlood;
	public Boolean angryMood;
	public Boolean globalRevenge;
	public String trackedDescription;
	public String lastDamagerMetadata;
	public String revengeMobMetadata;
	public String revengeTrackedInfoMetadata;
	public Double meleeModeRadius;
	public Double globalRevengeRadius;
	public ArrayList<World> disableWorlds;
	public HashMap<Hooks, HookManager> hooks;
	public Particles.RevengeParticle revengeParticle;
	public static RevengePlugin instance;

	@Override
	@SuppressWarnings("unchecked")
	public void onEnable() {
		instance = this;
		this.localhost = false;
		this.update = false;
		this.lastDamagerMetadata = "revengeLastDamager";
		this.revengeMobMetadata = "revengeMob";
		this.revengeTrackedInfoMetadata = "revengeArmorStand";
		this.disableWorlds = new ArrayList<>();
		this.hooks = new HashMap<>();
		this.revengeParticle = null;

		if (!setupNMS()) {
			this.getLogger()
					.warning("Revenge doesn't support this version of Spigot. (If you think that's a mistake => contact the developer)");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
		registerListeners(EntityDamageByEntity.class, PlayerDeath.class, PlayerJoin.class, PlayerDamage.class, PlayerInteractAtEntity.class, PlayerMove.class, PluginEnable.class, PluginDisable.class);

		setupConfig(true);

		for (World world : Bukkit.getWorlds())
			for (Entity ent : world.getEntities())
				if (ent instanceof ArmorStand) {
					ArmorStand as = (ArmorStand) ent;
					if (!as.isVisible() && !as.hasBasePlate() && as.getBoots() == null && as.getChestplate() == null
							&& as.getLeggings() == null && as.getHelmet() != null) {
						ent.remove();
					}
				}

		new BukkitRunnable() {
			public void run() {
				if (!URLManager.checkVersion(getDescription().getVersion(), false, URLManager.Link.GITHUB_PATH)) {
					getLogger()
							.warning("A new version more efficient of the plugin is available. Do '/rev update' to automatically update the plugin.");
					update = true;
				}
			}
		}.runTaskAsynchronously(this);
		new DataRegister(instance, localhost, false);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}

	private void registerListeners(@SuppressWarnings("unchecked") Class<? extends EventListener>... classes) {
		try {
			for (Class<? extends EventListener> clazz : classes) {
				Constructor<? extends EventListener> constructor = clazz
						.getConstructor(new Class[] { RevengePlugin.class });
				Bukkit.getPluginManager()
						.registerEvents((Listener) constructor.newInstance(new Object[] { this }), this);
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
		if (command.getName().equals("revenge")) {
			if (sender.isOp()) {
				if (args.length == 1) {
					if (args[0].equals("reload")) {
						setupConfig(false);
						sender.sendMessage(ChatColor.GREEN + "Revenge's config was successfully reloaded!");
					} else if (args[0].equals("update")) {
						if (this.update) {
							sender.sendMessage(ChatColor.AQUA
									+ "Stay informed about what the update bring new at https://www.spigotmc.org/resources/revenge-1-7-1-8-1-9-1-10.18235/updates");
							sender.sendMessage(ChatColor.GOLD
									+ "The updating task will start in 10 seconds, then your server will shutdown to complete the updating process.");
							new BukkitRunnable() {
								public void run() {
									if (URLManager.update(instance, URLManager
											.getLatestVersion(), false, URLManager.Link.GITHUB_PATH))
										new BukkitRunnable() {
											public void run() {
												getFile().delete();
												getFile().deleteOnExit();
												Bukkit.getServer().shutdown();
											}
										}.runTaskLater(instance, 100);
								}
							}.runTaskLater(this, 20 * 10);
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

	public void setupConfig(Boolean onStart) {
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			this.getLogger()
					.info("Thank you for having downloaded Revenge ! If you find any bugs, feel free to contact our team of developers.");
			this.getLogger()
					.info("We would really appreciate if you could follow our twitter page where we post news about our plugins <3 https://twitter.com/AsyncDevTeam");
			this.saveDefaultConfig();
		}
		if (!onStart)
			this.reloadConfig();
		this.meleeModeRadius = getConfig().getDouble("melee-mode.radius");
		this.onlySameSpecies = getConfig().getBoolean("melee-mode.only-same-species");
		this.meleeModeEnabled = getConfig().getBoolean("melee-mode.enable");
		this.trackedInfoEnabled = getConfig().getBoolean("tracked-info.enable");
		this.trackedDescription = ChatColor
				.translateAlternateColorCodes('&', getConfig().getString("tracked-info.description"));
		this.randomBehavior = getConfig().getBoolean("random-behavior");
		this.animalsBlood = getConfig().getBoolean("animals-blood");
		this.angryMood = getConfig().getBoolean("angry-mood");
		this.globalRevenge = getConfig().getBoolean("global-revenge.enable");
		this.globalRevengeRadius = getConfig().getDouble("global-revenge.radius");

		setupParticle();
		setupDisableWorlds();

		Mob.map.clear();
		ConfigurationSection section = this.getConfig().getConfigurationSection("moblist");
		for (String s : section.getKeys(false))
			new Mob(s, this);
	}

	public void setupParticle() {
		try {
			String particle = "";
			particle = getConfig().getString("hit-particles");
			if (particle.equals("null") || particle.equals("")) {
				return;
			}
			this.revengeParticle = new Particles.RevengeParticle(this, particle);
		} catch (NullPointerException | IllegalArgumentException ex) {
			getLogger()
					.warning("An error occured when reading the 'hit-particles' field in config.yml. Please review your syntax.");
			return;
		}
	}

	public void setupDisableWorlds() {
		ArrayList<String> worlds = new ArrayList<>();
		for (String world : getConfig().getStringList("disable-worlds")) {
			if (Bukkit.getWorld(world) != null) {
				this.disableWorlds.add(Bukkit.getWorld(world));
				worlds.add(world);
			} else {
				getLogger().info("Disable Worlds: It appears that you have no world named \"" + world
						+ "\" on your server.");
			}
		}
		if (!worlds.isEmpty())
			getLogger().info("Disable Worlds: " + worlds.toString().replace("[", "").replace("]", ""));
	}

	public Boolean setupNMS() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException unsuportedVersion) {
			return false;
		}

		try {
			IParticleSpawner = (IParticleSpawner) ReflectionUtils
					.instantiateObject(Class.forName("fr.roytreo.revenge." + version + ".ParticleSpawner"));
			INMSUtils = (INMSUtils) ReflectionUtils
					.instantiateObject(Class.forName("fr.roytreo.revenge." + version + ".NMSUtils"));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | ClassNotFoundException e) {
			getLogger().warning(e.getMessage());
			return false;
		}

		if (Integer.parseInt(version.split("_")[1]) >= 13) {
			I13Helper = new Post13Helper();
		} else {
			I13Helper = new Pre13Helper();
		}

		return (IParticleSpawner != null && INMSUtils != null);
	}

	public Boolean initHook(Hooks hook, String comments) {
		try {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(hook.getPluginName());
			if (plugin == null)
				return false;
			hooks.put(hook, (HookManager) ReflectionUtils
					.instantiateObject(Class.forName("fr.roytreo.revenge.core.hook." + hook.toString())));
			getLogger().info(hook.getPluginName() + " hooked!");
			if (!comments.trim().equals(""))
				getLogger().info(comments);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void removeHook(Hooks hook) {
		if (hooks.containsKey(hook)) {
			hooks.remove(hook);
			getLogger().info(hook.getPluginName() + " unhooked!");
		}
	}

	public boolean isHooked(Hooks softDepend) {
		return this.hooks.containsKey(softDepend);
	}

	public HookManager getHook(Hooks softDepend) {
		return this.hooks.get(softDepend);
	}
}
