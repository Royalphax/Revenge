package fr.royalpha.revenge.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.royalpha.revenge.core.handler.MinecraftVersion;
import fr.royalpha.revenge.core.version.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.revenge.core.event.EventListener;
import fr.royalpha.revenge.core.event.entity.EntityDamageByEntity;
import fr.royalpha.revenge.core.event.player.PlayerDamage;
import fr.royalpha.revenge.core.event.player.PlayerDeath;
import fr.royalpha.revenge.core.event.player.PlayerInteractAtEntity;
import fr.royalpha.revenge.core.event.player.PlayerJoin;
import fr.royalpha.revenge.core.event.player.PlayerMove;
import fr.royalpha.revenge.core.event.server.PluginDisable;
import fr.royalpha.revenge.core.event.server.PluginEnable;
import fr.royalpha.revenge.core.handler.Mob;
import fr.royalpha.revenge.core.handler.Particles;
import fr.royalpha.revenge.core.handler.URLManager;
import fr.royalpha.revenge.core.hook.base.HookManager;
import fr.royalpha.revenge.core.hook.base.Hooks;
import fr.royalpha.revenge.core.util.ReflectionUtils;

public class RevengePlugin extends JavaPlugin {

    public static final String PACKAGE = "fr.royalpha.revenge";
    public static VersionManager versionManager;

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

        try {
            versionManager = new VersionManager(MinecraftVersion.getVersion());
        } catch (ReflectiveOperationException e) {
            this.getLogger().warning("Revenge doesn't support this version of Spigot. (If you think that's a mistake => contact the developer)");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        registerListeners(EntityDamageByEntity.class, PlayerDeath.class, PlayerJoin.class, PlayerDamage.class, PlayerMove.class, PluginEnable.class, PluginDisable.class);
        if (versionManager.getVersion().newerOrEqualTo(MinecraftVersion.v1_8_R1)) {
            registerListeners(PlayerInteractAtEntity.class);
        }

        setupConfig(true);

        new BukkitRunnable() {
            public void run() {
                if (!URLManager.checkVersion(getDescription().getVersion(), false, URLManager.Link.GITHUB_PATH)) {
                    getLogger()
                            .warning("A new version more efficient of the plugin is available. Do '/rev update' to automatically update the plugin.");
                    update = true;
                }
            }
        }.runTaskAsynchronously(this);

        try {
            Class.forName("com.google.gson.JsonElement");
            Metrics metrics = new Metrics(this, 8204);
        } catch( ClassNotFoundException e ) {
            // Do nothing
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (World world : Bukkit.getWorlds())
            for (Entity ent : world.getEntities())
                if (ent.hasMetadata(revengeTrackedInfoMetadata)) {
                    ent.remove();
                }
    }

    private void registerListeners(@SuppressWarnings("unchecked") Class<? extends EventListener>... classes) {
        try {
            for (Class<? extends EventListener> clazz : classes) {
                Constructor<? extends EventListener> constructor = clazz
                        .getConstructor(new Class[]{RevengePlugin.class});
                Bukkit.getPluginManager()
                        .registerEvents((Listener) constructor.newInstance(new Object[]{this}), this);
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
            this.getLogger().info("Thank you for having downloaded Revenge ! If you find any bugs, feel free to contact our team of developers.");
            this.getLogger().info("We would really appreciate if you could follow our twitter page where we post news about our plugins <3 https://twitter.com/AsyncDevTeam");
            this.saveDefaultConfig();
        }
        if (!onStart)
            this.reloadConfig();
        this.meleeModeRadius = getConfig().getDouble("melee-mode.radius");
        this.onlySameSpecies = getConfig().getBoolean("melee-mode.only-same-species");
        this.meleeModeEnabled = getConfig().getBoolean("melee-mode.enable");
        this.trackedInfoEnabled = getConfig().getBoolean("tracked-info.enable");
        if (trackedInfoEnabled && versionManager.getVersion().olderThan(MinecraftVersion.v1_8_R1)) {
            trackedInfoEnabled = false;
            this.getLogger().info("ArmorStands were invented in 1.8 version of Minecraft. So 'tracked info' can't be enabled.");
        }
        this.trackedDescription = ChatColor.translateAlternateColorCodes('&', getConfig().getString("tracked-info.description"));
        this.randomBehavior = getConfig().getBoolean("random-behavior");
        this.animalsBlood = getConfig().getBoolean("animals-blood");
        this.angryMood = getConfig().getBoolean("angry-mood");
        this.globalRevenge = getConfig().getBoolean("global-revenge.enable");
        this.globalRevengeRadius = getConfig().getDouble("global-revenge.radius");

        setupParticle();
        setupDisableWorlds();

        Mob.map.clear();
        ConfigurationSection section = this.getConfig().getConfigurationSection("moblist");

        List<String> unregisteredMobs = new ArrayList<>();
        for (String s : section.getKeys(false))
            try {
                if (EntityType.valueOf(s) != null) {
                    new Mob(s, this);
                } else {
                    unregisteredMobs.add(s);
                }
            } catch (Exception ex) {
                unregisteredMobs.add(s);
            }

        if (!unregisteredMobs.isEmpty()) {
            this.getLogger().info("Some entities couldn't been registered: " + unregisteredMobs.toString().replace("[", "").replace("]", ""));
        }
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
                    .warning("An error occurred when reading the 'hit-particles' field in config.yml. Please review your syntax.");
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
                getLogger().info("Disabled worlds: It appears that you have no world named \"" + world + "\" on your server.");
            }
        }
        if (!worlds.isEmpty())
            getLogger().info("Disabled worlds: " + worlds.toString().replace("[", "").replace("]", ""));
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

    public static VersionManager getVersionManager() {
        return versionManager;
    }
}
