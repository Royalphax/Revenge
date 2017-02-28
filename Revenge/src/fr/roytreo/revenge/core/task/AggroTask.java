package fr.roytreo.revenge.core.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.handler.Mob;
import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.softdepend.PvPManager;
import fr.roytreo.revenge.core.util.MathUtils;

public class AggroTask extends BukkitRunnable {

	private Entity killer;
	private Mob mob;
	private Entity victim;
	private Integer stopTicks, intervalTicks, walkUpdateLimitTicks;
	private Double bloodTicks;
	private Boolean countStopTicks, countIntervalTicks, enableParticles;
	private Location lastAskedLocation;
	private RevengePlugin instance;
	private ArmorStand trackedInfo;

	public AggroTask(Entity ent, Mob m, Entity victim, RevengePlugin instance) {
		if (m == null)
			return;
		if (ent == victim)
			return;
		this.killer = ent;
		this.mob = m;
		this.victim = victim;
		this.stopTicks = 0;
		this.intervalTicks = 0;
		this.walkUpdateLimitTicks = 0;
		this.bloodTicks = 0.0D;
		this.countStopTicks = this.mob.getStopTimeTicks() != 0;
		this.countIntervalTicks = this.mob.getDamageIntervalTicks() != 0.0D;
		this.enableParticles = instance.revengeParticle != null;
		this.instance = instance;
		if ((instance.softDepends.containsKey("PvPManager")) && (victim instanceof Player))
			if (!((PvPManager) instance.softDepends.get("PvPManager")).hasPvPEnabled((Player) victim))
				return;
		this.killer.setMetadata(this.instance.revengeMobMetadata, new FixedMetadataValue(this.instance, true));
		if (instance.trackedInfoEnabled) {
			this.trackedInfo = this.killer.getWorld().spawn(MathUtils.getLeftBackSide(this.killer.getLocation(), 1.5D), ArmorStand.class);
			this.trackedInfo.setVisible(false);
			this.instance.IPathEntity.setGravity(this.trackedInfo, false);
			this.trackedInfo.setCustomNameVisible(false);
			this.trackedInfo.setMetadata(instance.revengeTrackedInfoMetadata, new FixedMetadataValue(instance, true));
			String trackedInfoDescription = instance.trackedDescription;
			if (this.victim instanceof Player) {
				trackedInfoDescription = trackedInfoDescription.replaceAll("%PLAYER%",
						((Player) this.victim).getName());
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				skullMeta.setOwner(((Player) this.victim).getName());
				skull.setItemMeta(skullMeta);
				this.trackedInfo.setHelmet(skull);
			}
			this.trackedInfo.setCustomName(trackedInfoDescription);
			this.trackedInfo.setCustomNameVisible(true);
		}
		m.getList().add(this);
		runTaskTimer(instance, 0L, 0L);
	}

	public void run() {
		if (this.countIntervalTicks)
			this.intervalTicks += 1;
		if (this.countStopTicks)
			this.stopTicks += 1;
		if (this.instance.randomBehavior)
			this.walkUpdateLimitTicks += 1;
		if (this.instance.animalsBlood && this.bloodTicks > 0) {
			this.bloodTicks -= 1;
			this.instance.revengeParticle.playBloodParticles(((LivingEntity) this.killer).getEyeLocation());
		}
		if (!this.killer.isDead() && !this.victim.isDead() && this.stopTicks <= this.mob.getStopTimeTicks()
				&& this.victim.getWorld() == this.killer.getWorld()
				&& this.victim.getLocation().distance(this.killer.getLocation()) < this.mob.getStopBlocks()
				&& this.victim.getWorld() == this.killer.getWorld()) {
			if (this.victim.getLocation().distance(this.killer.getLocation()) < this.mob.getHitRadius()) {
				if (this.countIntervalTicks && this.intervalTicks < this.mob.getDamageIntervalTicks())
					return;
				damage();
			}
			if (this.instance.trackedInfoEnabled)
				this.trackedInfo.teleport(MathUtils.getLeftBackSide(this.killer.getLocation(), 1.5D));
			Vector v = this.victim.getLocation().subtract(this.killer.getLocation()).toVector().normalize();
			this.killer.getLocation().setDirection(v);
			if (this.killer.getType() == EntityType.SQUID) {
				Material mat = this.killer.getLocation().getBlock().getType();
				if ((mat == Material.WATER || mat == Material.STATIONARY_WATER)
						&& this.victim.getLocation().distance(this.killer.getLocation()) > this.mob.getHitRadius()) {
					this.instance.IParticleSpawner.playParticles(Particles.WATER_BUBBLE, this.killer.getLocation(),
							0.3F, 0.3F, 0.3F, 3, 0.1F);
					this.killer.setVelocity(v.multiply(this.mob.getSpeed()));
				}
			} else if (this.killer.getType() == EntityType.BAT) {
				Material mat = this.killer.getLocation().getBlock().getType();
				if ((mat == Material.AIR)
						&& this.victim.getLocation().distance(this.killer.getLocation()) > this.mob.getHitRadius())
					this.killer.setVelocity(v.multiply(this.mob.getSpeed()));
			} else {
				this.walkToLocation(this.victim.getLocation(), this.mob.getSpeed());
			}
			return;
		}
		down();
	}

	private void damage() {
		this.victim.setMetadata(this.instance.lastDamagerMetadata,
				new FixedMetadataValue(this.instance, this.mob.getEntity().name()));
		this.instance.IPathEntity.damage(this.victim, this.killer, this.mob.getDamage());
		if (enableParticles)
			this.instance.revengeParticle.playParticles(this.victim.getLocation());
		if (this.countIntervalTicks)
			this.intervalTicks = 0;
	}

	public void down() {
		cancel();
		if (this.instance.trackedInfoEnabled)
			this.trackedInfo.remove();
		this.killer.removeMetadata(this.instance.revengeMobMetadata, this.instance);
		this.instance.IPathEntity.walkTo(this.killer, this.killer.getLocation(), this.mob.getSpeed());
		if (this.mob.getList().contains(this)) {
			this.mob.getList().remove(this);
		}
	}
	
	public void walkToLocation(Location location, Double speed) {
		if (this.instance.randomBehavior) {
			if (this.lastAskedLocation != null) {
				Location pos1 = this.killer.getLocation();
				pos1.setY(0.0D);
				Location pos2 = this.lastAskedLocation;
				pos2.setY(0.0D);
				Location pos3 = this.victim.getLocation();
				pos3.setY(0.0D);
				if (pos1.distance(pos2) > 1 && this.walkUpdateLimitTicks < 160 && pos1.distance(pos2) < pos1.distance(pos3))
					return;
			}
			this.walkUpdateLimitTicks = 0;
			Double distance = location.distance(this.killer.getLocation());
			if (distance <= 50.0D) {
				// Speed Modifier
				speed += MathUtils.random(1.5f);

				// Accuracy Modifier
				Float multiplier = (float) (distance / 50);
				Float range = 10 * multiplier;
				Float randX = MathUtils.random(range * 2) - range;
				Float randZ = MathUtils.random(range * 2) - range;
				location = location.add(randX, 0, randZ);
			}
		}
		this.lastAskedLocation = location;
		this.instance.IPathEntity.walkTo(this.killer, location, speed);
	}

	public Entity getKiller() {
		return this.killer;
	}

	public Entity getVictim() {
		return this.victim;
	}
	
	public void resetBloodAnimation(Double damage)
	{
		this.bloodTicks = damage*20;
	}
}