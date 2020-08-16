package fr.royalpha.revenge.core.version;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface INMSUtils {
	public void walkTo(Entity entity, Location location, Double aggro_speed);

	public void damage(Entity ent, Entity damager, float damage);
	
	public void setGravity(Entity ent, boolean bool);
	
	public void playAnimation(Entity ent, int animation);
}