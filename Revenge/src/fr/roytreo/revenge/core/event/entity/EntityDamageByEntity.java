package fr.roytreo.revenge.core.event.entity;

import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.event.EventListener;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.softdepend.base.SoftDepends;
import fr.roytreo.revenge.core.task.AggroTask;

public class EntityDamageByEntity extends EventListener {
	public EntityDamageByEntity(RevengePlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
		Entity entity = ev.getEntity();
		Entity attacker = ev.getDamager();
		if (this.plugin.disableWorlds.contains(attacker.getLocation().getWorld()))
			return;
		if ((this.plugin.isSoftDepend(SoftDepends.Citizens) && entity.hasMetadata("NPC")) || (this.plugin.isSoftDepend(SoftDepends.ShopKeepers) && entity.hasMetadata("shopkeeper")) || (this.plugin.isSoftDepend(SoftDepends.UltraCosmetics) && entity.hasMetadata("Pet"))) 
			return;
		if ((attacker instanceof Player || attacker instanceof Arrow) && ev.getDamage() > 0) {
			Player player = null;
			if (attacker instanceof Player)
				player = (Player) attacker;
			if (attacker instanceof Arrow) {
				LivingEntity livingEnt = ((LivingEntity) ((Arrow) attacker).getShooter());
				if (livingEnt instanceof Player) {
					player = (Player) livingEnt;
				} else {
					return;
				}
			}
			if (!(entity instanceof Player) && player != null) {
				if (Mob.isRegistred(entity.getType()))
				{
					if (entity instanceof Tameable) {
						if (((Tameable) entity).isTamed())
							return;
					}
					Mob mob = Mob.getMob(entity.getType());
					if (mob.isEnable()) {
						Integer r = new Random().nextInt(101);
						if (r < mob.getPercent()) {
							if (Mob.isAngry(entity))
							{
								if (!Mob.getAggroTask(entity).getVictim().getName().equals(player.getName())) {
									Mob.getAggroTask(entity).down();
									new AggroTask(entity, mob, player, this.plugin);
								}
							} else {
								new AggroTask(entity, mob, player, this.plugin);
							}
							if (this.plugin.meleeModeEnabled) {
								for (Entity nearbyEntity : entity.getNearbyEntities(this.plugin.meleeModeRadius, this.plugin.meleeModeRadius, this.plugin.meleeModeRadius)) {
									if (Mob.isRegistred(nearbyEntity.getType()) && nearbyEntity.getType() != EntityType.PLAYER)
									{
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
									} else if (nearbyEntity instanceof Creature) {
										((Creature) nearbyEntity).setTarget(player);
									}
								}
							}
						}
						if (mob.isPlayerAttacked(player))
							mob.getAttackingScheduler(player).resetBloodAnimation(ev.getDamage());
					}
				}
			}
		}
	}
}