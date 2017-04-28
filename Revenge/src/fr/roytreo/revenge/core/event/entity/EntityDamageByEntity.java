package fr.roytreo.revenge.core.event.entity;

import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.task.AggroTask;

public class EntityDamageByEntity extends EventListener {
	public EntityDamageByEntity(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
		Entity def = ev.getEntity();
		Entity att = ev.getDamager();
		if (this.plugin.disableWorlds.contains(att.getLocation().getWorld()))
			return;
		if (def.hasMetadata("NPC") || def.hasMetadata("shopkeeper")) return;
		if ((att instanceof Player || att instanceof Arrow) && ev.getDamage() > 0) {
			Player p = null;
			if (att instanceof Player)
				p = (Player) att;
			if (att instanceof Arrow) {
				LivingEntity livingEnt = ((Arrow) att).getShooter();
				if (livingEnt instanceof Player) {
					p = (Player) livingEnt;
				} else {
					return;
				}
			}
			if (!(def instanceof Player) && p != null) {
				if (Mob.isRegistred(def.getType()))
				{
					Mob mob = Mob.getMob(def.getType());
					if (mob.isEnable()) {
						Integer r = new Random().nextInt(101);
						if (r < mob.getPercent()) {
							this.plugin.IParticleSpawner.playParticles(Particles.VILLAGER_ANGRY, def.getLocation().add(0, 1, 0), 0.0F, 0.0F,
									0.0F, 1, 0.1F);
							if (!mob.isPlayerAttacked(p) || !mob.getAttackingScheduler(p).getKiller().equals(def))
							{
								if (Mob.isAngry(def))
									Mob.getAggroTask(def).down();
								new AggroTask(def, mob, p, this.plugin);
							}
							if (this.plugin.meleeModeEnabled) {
								for (Entity entities : def.getNearbyEntities(this.plugin.radius, this.plugin.radius, this.plugin.radius)) {
									if (Mob.isRegistred(entities.getType()) && entities.getType() != EntityType.PLAYER)
									{
										Mob nearbyMob = Mob.getMob(entities.getType());
										if (nearbyMob.isEnable())
										{
											this.plugin.IParticleSpawner.playParticles(Particles.VILLAGER_ANGRY, entities.getLocation().add(0, 1, 0), 0.0F, 0.0F,
													0.0F, 1, 0.1F);
											if (nearbyMob.isPlayerAttacked(p) && nearbyMob.getAttackingScheduler(p).getKiller() == entities) {
												continue;
											}
											new AggroTask(entities, nearbyMob, p, this.plugin);
										}
									} else if (entities instanceof Creature) {
										((Creature) entities).setTarget(p);
									}
								}
							}
						}
						if (mob.isPlayerAttacked(p))
							mob.getAttackingScheduler(p).resetBloodAnimation(ev.getDamage());
					}
				}
			}
		}
	}
}