package fr.royalpha.revenge.v1_16_R1;

import fr.royalpha.revenge.core.version.INMSUtils;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NMSUtils implements INMSUtils {

    @Override
    public void walkTo(Entity entity, Location location, Double aggroSpeed) {
        Object pObject = ((CraftEntity) entity).getHandle();

        //net.minecraft.server.v1_16_R1.PathEntity path = ((EntityInsentient) pObject).getNavigation().a(location.getX(), location.getY(), location.getZ());
        net.minecraft.server.v1_16_R1.PathEntity path = ((EntityInsentient) pObject).getNavigation().a(location.getX(), location.getY(), location.getZ(), 1);
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
        net.minecraft.server.v1_16_R1.Entity nmsEntity = ((CraftEntity) ent).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("NoGravity", !bool);
        nmsEntity.a_(tag);
        EntityLiving el = (EntityLiving) nmsEntity;
        el.load(tag);
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