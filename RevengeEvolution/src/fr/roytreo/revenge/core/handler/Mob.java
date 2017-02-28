package fr.roytreo.revenge.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.RevengePlugin;
import fr.roytreo.revenge.core.task.AggroTask;

public class Mob {
	public static HashMap<EntityType, Mob> map = new HashMap<>();
	private Boolean enable;
	private String name;
	private Double speed;
	private Float damage;
	private Double damage_interval;
	private Double hit_radius;
	private Integer percent;
	private Integer stop_time;
	private Integer stop_blocks;
	private String deathMsg;
	private EntityType entity;
	private ArrayList<AggroTask> list = new ArrayList<>();

	public Mob(String name, RevengePlugin instance) {
		try {
			this.entity = EntityType.valueOf(name);
			this.enable = instance.getConfig().getBoolean("moblist." + name + ".enable");
			this.name = instance.getConfig().getString("moblist." + name + ".name");
			this.speed = instance.getConfig().getDouble("moblist." + name + ".speed");
			this.damage = (float) instance.getConfig().getDouble("moblist." + name + ".damage");
			this.damage_interval = Math.floor(instance.getConfig().getDouble("moblist." + name + ".damage-interval") * 20.0D);
			this.hit_radius = instance.getConfig().getDouble("moblist." + name + ".hit-radius");
			this.percent = instance.getConfig().getInt("moblist." + name + ".percent");
			this.stop_time = instance.getConfig().getInt("moblist." + name + ".stop-time") * 20;
			this.stop_blocks = instance.getConfig().getInt("moblist." + name + ".stop-blocks");
			if (this.stop_blocks == 0) {
				this.stop_blocks = Integer.MAX_VALUE;
			}
			this.deathMsg = ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("moblist." + name + ".death-message"));
		} catch (Exception ex) {
			instance.getLogger().warning("Error when loading \"" + name + "\" in config.yml. If you don't know how to fix this issue, please contact the developer.");
			instance.getLogger().warning("|> Issue's description: " + ex.getMessage());
			return;
		}
		map.put(EntityType.valueOf(name), this);
		
	}

	//Getter
	public Boolean isEnable() {
		return this.enable;
	}

	public String getName() {
		return this.name;
	}

	public Double getSpeed() {
		return this.speed;
	}

	public Float getDamage() {
		return this.damage;
	}

	public Double getDamageIntervalTicks() {
		return this.damage_interval;
	}

	public double getHitRadius() {
		return this.hit_radius;
	}

	public Integer getPercent() {
		return this.percent;
	}

	public Integer getStopTimeTicks() {
		return this.stop_time;
	}

	public Integer getStopBlocks() {
		return this.stop_blocks;
	}

	public String getDeathMessage() {
		return this.deathMsg;
	}

	public EntityType getEntity() {
		return this.entity;
	}

	public ArrayList<AggroTask> getList() {
		return this.list;
	}
	
	//Setter
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public void setDamage(Float damage) {
		this.damage = damage;
	}

	public void setDamageIntervalTicks(Double damage_interval) {
		this.damage_interval = damage_interval;
	}

	public void setHitRadius(Double hit_radius) {
		this.hit_radius = hit_radius;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public void setStopTimeTicks(Integer stop_time) {
		this.stop_time = stop_time;
	}

	public void setStopBlocks(Integer stop_blocks) {
		this.stop_blocks = stop_blocks;
	}
	
	public void setDeathMessage(String deathMsg) {
		this.deathMsg = deathMsg;
	}

	//Other
	public Boolean isPlayerAttacked(Player player) {
		for (AggroTask task : this.list)
		{
			if (task.getVictim() == player)
				return true;
		}
		return false;
	}
	
	public AggroTask getAttackingScheduler(Player player) {
		for (AggroTask task : this.list)
		{
			if (task.getVictim() == player)
				return task;
		}
		return null;
	}

	//Static
	public static Mob getMob(EntityType type) {
		for (EntityType t : map.keySet()) {
			if (t == type) {
				return (Mob) map.get(t);
			}
		}
		return null;
	}
	
	public static boolean isAngry(Entity entity) {
		Mob mob = getMob(entity.getType());
		for (AggroTask task : mob.getList())
			if (task.getKiller().equals(entity))
				return true;
		return false;
	}
	
	public static AggroTask getAggroTask(Entity entity) {
		Mob mob = getMob(entity.getType());
		for (AggroTask task : mob.getList())
			if (task.getKiller().equals(entity))
				return task;
		return null;
	}

	public static Boolean isRegistred(EntityType type) {
		for (EntityType t : map.keySet()) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}

	public static HashMap<EntityType, Mob> getMap() {
		return map;
	}
	
	public static void updateMobs(RevengePlugin instance) {
		for (EntityType entity : map.keySet())
		{
			Mob mob = map.get(entity);
			String name = entity.name();
			try {
				mob.setEnable(instance.getConfig().getBoolean("moblist." + name + ".enable"));
				mob.setName(instance.getConfig().getString("moblist." + name + ".name"));
				mob.setSpeed(instance.getConfig().getDouble("moblist." + name + ".speed"));
				mob.setDamage((float) instance.getConfig().getDouble("moblist." + name + ".damage"));
				mob.setDamageIntervalTicks(Math.floor(instance.getConfig().getDouble("moblist." + name + ".damage-interval") * 20.0D));
				mob.setHitRadius(instance.getConfig().getDouble("moblist." + name + ".hit-radius"));
				mob.setPercent(instance.getConfig().getInt("moblist." + name + ".percent"));
				mob.setStopTimeTicks(instance.getConfig().getInt("moblist." + name + ".stop-time") * 20);
				mob.setStopBlocks(instance.getConfig().getInt("moblist." + name + ".stop-blocks"));
				if (mob.getStopBlocks() == 0) {
					mob.setStopBlocks(Integer.MAX_VALUE);
				}
				mob.setDeathMessage(ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("moblist." + name + ".death-message")));
			} catch (Exception ex) {
				instance.getLogger().warning("Error when loading \"" + name + "\" in config.yml. If you don't know how to fix this issue, please contact the developer.");
				instance.getLogger().warning("|> Issue's description: " + ex.getMessage());
				return;
			}
		}
	}
	
	public static Set<EntityType> getMapSet() {
		return map.keySet();
	}
}
