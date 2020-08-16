package fr.royalpha.revenge.core.event.server;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;

import fr.royalpha.revenge.core.event.EventListener;
import fr.royalpha.revenge.core.hook.base.Hooks;

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
