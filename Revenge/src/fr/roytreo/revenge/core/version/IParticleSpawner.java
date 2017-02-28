package fr.roytreo.revenge.core.version;

import org.bukkit.Location;

import fr.roytreo.revenge.core.handler.Particles;

public interface IParticleSpawner {
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount,
			Float particleData, int... list);
}
