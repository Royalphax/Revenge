package fr.royalpha.revenge.core.event.server;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;

import fr.royalpha.revenge.core.event.EventListener;
import fr.royalpha.revenge.core.hook.base.Hooks;

public class PluginEnable extends EventListener {
	public PluginEnable(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent ev) {
		for (Hooks hook : Hooks.values()) {
			if (ev.getPlugin().getName().equalsIgnoreCase(hook.getPluginName())) {
				this.plugin.initHook(hook, hook.getInfo());
			}
		}
		if (ev.getPlugin().getName().equals("MultiWorld") || ev.getPlugin().getName().equals("Multiverse-Core")) {
			this.plugin.getLogger().info("World managment plugin detected! Reloading disable-worlds feature ...");
			this.plugin.setupDisableWorlds();
		}
	}
}
