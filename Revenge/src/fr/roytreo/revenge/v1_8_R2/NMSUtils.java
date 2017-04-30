package fr.roytreo.revenge.v1_8_R2;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.version.INMSUtils;
import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.PacketPlayOutAnimation;

public class NMSUtils implements INMSUtils {

	@Override
	public void walkTo(Entity entity, Location location, Double aggroSpeed) {
		Object pObject = ((CraftEntity) entity).getHandle();

		net.minecraft.server.v1_8_R2.PathEntity path = ((EntityInsentient) pObject).getNavigation().a(location.getX(),
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
		net.minecraft.server.v1_8_R2.Entity nmsEntity = ((CraftEntity) ent).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("NoGravity", !bool);
        nmsEntity.c(tag);
        EntityLiving el = (EntityLiving) nmsEntity;
        el.f(tag);
	}
	
	@Override
	public void playAnimation(Entity ent, int animation) {
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftEntity) ent).getHandle(), animation);
		for (Player p : ent.getWorld().getPlayers()) {
			if ((p.isOnline()) && (p instanceof CraftPlayer)) {
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
}
