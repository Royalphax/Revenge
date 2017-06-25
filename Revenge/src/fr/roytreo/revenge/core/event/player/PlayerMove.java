package fr.roytreo.revenge.core.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
		Player p = ev.getPlayer();
		if (!ev.getFrom().getBlock().equals(ev.getTo().getBlock())) {
			for (Entity ent : p.getNearbyEntities(this.plugin.globalRevengeRadius, this.plugin.globalRevengeRadius, this.plugin.globalRevengeRadius))
				if (Mob.isRegistred(ent.getType()))
				{
					Mob mob = Mob.getMob(ent.getType());
					if (mob.isEnable()) {
						if (!mob.isPlayerAttacked(p) || !mob.getAttackingScheduler(p).getKiller().equals(ent))
						{
							if (!Mob.isAngry(ent))
								new AggroTask(ent, mob, p, this.plugin);
						}
					}
				}
		}
	}
}
