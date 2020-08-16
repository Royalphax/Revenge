package fr.royalpha.revenge.core.event.player;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.royalpha.revenge.core.event.EventListener;

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
