package fr.roytreo.revenge.v1_13_R1;

import org.bukkit.Location;
import org.bukkit.Particle;

import fr.roytreo.revenge.core.handler.Particles;
import fr.roytreo.revenge.core.version.IParticleSpawner;

public class ParticleSpawner implements IParticleSpawner {

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list) {
		location.getWorld().spawnParticle(Particle.valueOf(particle.toString()), location, amount, fx, fy, fz, particleData);
	}
}
