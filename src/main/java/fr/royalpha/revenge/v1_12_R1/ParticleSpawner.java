package fr.royalpha.revenge.v1_12_R1;

import fr.royalpha.revenge.core.handler.Particles;
import fr.royalpha.revenge.core.version.IParticleSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;

public class ParticleSpawner implements IParticleSpawner {

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
                              Float particleData, int... list) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.a(particle.getId()), true,
				(float) location.getX(), (float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData,
				amount, list);
		for (Player p : location.getWorld().getPlayers()) {
			if ((p.isOnline()) && (p instanceof org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)) {
				((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) p).getHandle().playerConnection
						.sendPacket(packet);
			}
		}
	}
}
