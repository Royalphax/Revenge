package fr.roytreo.revenge.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;

public class PlayerJoin extends EventListener {
	public PlayerJoin(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		if (ev.getPlayer().hasMetadata(this.plugin.lastDamagerMetadata))
			ev.getPlayer().removeMetadata(this.plugin.lastDamagerMetadata, this.plugin);
	}
}
