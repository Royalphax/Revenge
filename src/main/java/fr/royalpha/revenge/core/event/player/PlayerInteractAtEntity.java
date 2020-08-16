package fr.royalpha.revenge.core.event.player;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import fr.royalpha.revenge.core.event.EventListener;

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
