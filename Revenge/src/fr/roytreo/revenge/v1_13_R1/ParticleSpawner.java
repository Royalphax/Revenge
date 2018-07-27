package fr.roytreo.revenge.v1_13_R1;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.version.IParticleSpawner;

public class ParticleSpawner implements IParticleSpawner {
	
	private List<Particles> triedParticles = new ArrayList<>();

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list) {
		/*if (particle.getOptionalParametersSize() == 3) {
			Color color = Color.fromBGR(list[0], list[1], list[2]);
			location.getWorld().spawnParticle(Particle.valueOf(particle.toString()), location, amount, fx, fy, fz, particleData, new DustOptions(color, 1f));
		}*/
		if (particle.getOptionalParametersSize() > 0) {
			if (!triedParticles.contains(particle)) {
				triedParticles.add(particle);
				Bukkit.getLogger().info("<!> Something tried to show " + particle.toString() + " particle to a player. Sorry but this particle can't be used yet ! As you may know, 1.13 is really recent and our developers are always looking for how to handle this type of particle. Will be fixed in another update."); 
			}
		} else {
			location.getWorld().spawnParticle(Particle.valueOf(particle.toString()), location, amount, fx, fy, fz, particleData);
		}
	}
}
