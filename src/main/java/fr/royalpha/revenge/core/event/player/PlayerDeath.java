package fr.royalpha.revenge.core.event.player;

import fr.royalpha.revenge.core.RevengePlugin;
import fr.royalpha.revenge.core.hook.base.Hooks;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import fr.royalpha.revenge.core.event.EventListener;
import fr.royalpha.revenge.core.handler.Mob;

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
