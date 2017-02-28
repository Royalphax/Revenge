package fr.roytreo.revenge.v1_8_R3;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.version.IParticleSpawner;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class ParticleSpawner implements IParticleSpawner {

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.a(particle.getId()), true, (float) location.getX(),
				(float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount, list);
		for (Player p : location.getWorld().getPlayers()) {
			if ((p.isOnline()) && (p instanceof org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)) {
				((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
}
