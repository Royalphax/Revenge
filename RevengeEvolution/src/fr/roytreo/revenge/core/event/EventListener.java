package fr.roytreo.revenge.core.event;

import org.bukkit.event.Listener;

import fr.roytreo.revenge.core.RevengePlugin;

public class EventListener implements Listener {
	protected RevengePlugin plugin;

	protected EventListener(RevengePlugin plugin) {
		this.plugin = plugin;
	}
}
