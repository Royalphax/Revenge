package fr.roytreo.revenge.v1_8_R3;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import fr.roytreo.revenge.core.version.IPathEntity;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class PathEntity implements IPathEntity {

	@Override
	public void walkTo(Entity entity, Location location, Double aggroSpeed) {
		Object pObject = ((CraftEntity) entity).getHandle();

		net.minecraft.server.v1_8_R3.PathEntity path = ((EntityInsentient) pObject).getNavigation().a(location.getX(),
				location.getY(), location.getZ());
		if (path != null) {
			((EntityInsentient) pObject).getNavigation().a(path, 2.0D);
			((EntityInsentient) pObject).getNavigation().a(aggroSpeed);
		}
	}
	
	@Override
	public void damage(Entity ent, Entity damager, float damage) {
		((CraftEntity) ent).getHandle().damageEntity(DamageSource.mobAttack((EntityLiving) ((CraftEntity) damager).getHandle()), damage);
	}
	
	@Override
	public void setGravity(Entity ent, boolean bool) {
		net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) ent).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("NoGravity", !bool);
        nmsEntity.c(tag);
        EntityLiving el = (EntityLiving) nmsEntity;
        el.f(tag);
	}
}
