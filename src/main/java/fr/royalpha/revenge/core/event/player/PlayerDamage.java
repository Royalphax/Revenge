package fr.royalpha.revenge.core.event.player;

import fr.royalpha.revenge.core.RevengePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.royalpha.revenge.core.event.EventListener;

public class PlayerDamage extends EventListener {
	public PlayerDamage(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDamaged(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player)
		{
			Player victim = (Player) ev.getEntity();
			DamageCause cause = ev.getCause();
			if (cause != DamageCause.ENTITY_ATTACK && victim.hasMetadata(plugin.lastDamagerMetadata))
			{
				victim.removeMetadata(plugin.lastDamagerMetadata, this.plugin);
			}
		} else if (ev.getEntity().getType().toString().equalsIgnoreCase("ARMOR_STAND"))
		{
			if (ev.getEntity().hasMetadata(this.plugin.revengeTrackedInfoMetadata))
			{
				ev.setCancelled(true);
			}
		}
	}
}
