package fr.roytreo.revenge.core.event.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.hook.base.Hooks;

public class PluginDisable extends EventListener {
	public PluginDisable(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent ev) {
		for (Hooks hook : Hooks.values()) {
			if (ev.getPlugin().getName().equalsIgnoreCase(hook.getPluginName())) {
				this.plugin.removeHook(hook);
			}
		}
	}
}
