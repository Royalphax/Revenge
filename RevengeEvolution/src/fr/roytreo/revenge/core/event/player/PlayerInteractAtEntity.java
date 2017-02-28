package fr.roytreo.revenge.core.event.player;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;

public class PlayerInteractAtEntity extends EventListener {
	public PlayerInteractAtEntity(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onArmorStandInteract(PlayerInteractAtEntityEvent ev) {
		if (ev.getRightClicked() instanceof ArmorStand)
		{
			if (ev.getRightClicked().hasMetadata(this.plugin.revengeTrackedInfoMetadata))
			{
				ev.setCancelled(true);
			}
		}
	}
}
