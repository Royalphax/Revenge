package fr.royalpha.revenge.core.event;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.event.Listener;

public class EventListener implements Listener {
	protected RevengePlugin plugin;

	protected EventListener(RevengePlugin plugin) {
		this.plugin = plugin;
	}
}
