package fr.roytreo.revenge.v1_7_R1;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.version.IParticleSpawner;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;

public class ParticleSpawner implements IParticleSpawner {

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle.getName(), (float) location.getX(),
				(float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount);
		for (Player p : location.getWorld().getPlayers()) {
			if ((p.isOnline()) && (p instanceof org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer)) {
				((org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
}
