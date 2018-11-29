package fr.roytreo.revenge.core.event.player;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.hook.base.Hooks;

public class PlayerDeath extends EventListener {
	public PlayerDeath(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent ev) {
		Player p = ev.getEntity();
		if (p.hasMetadata(plugin.lastDamagerMetadata) && !this.plugin.hooks.containsKey(Hooks.DeathMessagesPrime))
		{
			for (MetadataValue value : p.getMetadata(plugin.lastDamagerMetadata))
			{
				if (value.getOwningPlugin().getDescription().getName().equals(this.plugin.getDescription().getName()))
				{
					try {
						Mob mob = Mob.getMob(EntityType.valueOf((String) value.value()));
						ev.setDeathMessage(mob.getDeathMessage().replaceAll("%NAME%", mob.getName()).replaceAll("%PLAYER%", p.getName()));
						break;
					} catch (NullPointerException | ClassCastException e) {}
					p.removeMetadata(plugin.lastDamagerMetadata, this.plugin);
				}
			}
		}
	}
}
