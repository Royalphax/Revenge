package fr.roytreo.revenge.core.event.player;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;

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
		} else if (ev.getEntity() instanceof ArmorStand)
		{
			if (ev.getEntity().hasMetadata(this.plugin.revengeTrackedInfoMetadata))
			{
				ev.setCancelled(true);
			}
		}
	}
}
