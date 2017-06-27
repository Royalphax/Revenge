package fr.roytreo.revenge.core.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.task.AggroTask;

public class PlayerMove extends EventListener {
	public PlayerMove(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev) {
		if (!this.plugin.globalRevenge)
			return;
		Player player = ev.getPlayer();
		if (!ev.getFrom().getBlock().equals(ev.getTo().getBlock())) {
			for (Entity nearbyEntity : player.getNearbyEntities(this.plugin.meleeModeRadius, this.plugin.meleeModeRadius, this.plugin.meleeModeRadius)) {
				if (Mob.isRegistred(nearbyEntity.getType()) && nearbyEntity.getType() != EntityType.PLAYER)
				{
					if (nearbyEntity instanceof Tameable) {
						if (((Tameable) nearbyEntity).isTamed())
							return;
					}
					Mob nearbyMob = Mob.getMob(nearbyEntity.getType());
					if (nearbyMob.isEnable())
					{
						if (Mob.isAngry(nearbyEntity))
						{
							if (!Mob.getAggroTask(nearbyEntity).getVictim().getName().equals(player.getName())) {
								Mob.getAggroTask(nearbyEntity).down();
								new AggroTask(nearbyEntity, nearbyMob, player, this.plugin);
							}
						} else {
							new AggroTask(nearbyEntity, nearbyMob, player, this.plugin);
						}
					}
				}
			}
		}
	}
}
