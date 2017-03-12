package fr.roytreo.revenge.core.handler;

import java.util.Random;

import org.bukkit.Location;

import fr.roytreo.revenge.core.RevengePlugin;

public enum Particles {
	EXPLOSION_NORMAL("explode", 0), 
	EXPLOSION_LARGE("largeexplode", 1), 
	EXPLOSION_HUGE("hugeexplosion",2), 
	FIREWORKS_SPARK("fireworksSpark", 3), 
	WATER_BUBBLE("bubble", 4), 
	WATER_SPLASH("splash", 5), 
	WATER_WAKE("wake", 6), 
	SUSPENDED("suspended", 7), 
	SUSPENDED_DEPTH("depthsuspend", 8), 
	CRIT("crit", 9), 
	CRIT_MAGIC("magicCrit", 10), 
	SMOKE_NORMAL("smoke", 11), 
	SMOKE_LARGE("largesmoke", 12), 
	SPELL("spell", 13), 
	SPELL_INSTANT("instantSpell", 14), 
	SPELL_MOB("mobSpell", 15), 
	SPELL_MOB_AMBIENT("mobSpellAmbient", 16), 
	SPELL_WITCH("witchMagic", 17), 
	DRIP_WATER("dripWater", 18), 
	DRIP_LAVA("dripLava", 19), 
	VILLAGER_ANGRY("angryVillager", 20), 
	VILLAGER_HAPPY("happyVillager", 21), 
	TOWN_AURA("townaura", 22), 
	NOTE("note", 23), 
	PORTAL("portal", 24), 
	ENCHANTMENT_TABLE("enchantmenttable", 25), 
	FLAME("flame", 26), 
	LAVA("lava", 27), 
	FOOTSTEP("footstep", 28), 
	CLOUD("cloud", 29), 
	REDSTONE("reddust",	30), 
	SNOWBALL("snowballpoof", 31), 
	SNOW_SHOVEL("snowshovel", 32), 
	SLIME("slime", 33), 
	HEART("heart", 34), 
	BARRIER("barrier", 35), 
	ITEM_CRACK("iconcrack_", 36, 2), 
	BLOCK_CRACK("blockcrack_", 37, 1), 
	BLOCK_DUST("blockdust_", 38, 1), 
	WATER_DROP("droplet", 39), 
	ITEM_TAKE("take", 40), 
	MOB_APPEARANCE("mobappearance",	41),
	DRAGON_BREATH("dragonbreath", 42),
	END_ROD("endrod", 43),
	DAMAGE_INDICATOR("damageindicator",	44),
	SWEEP_ATTACK("sweepattack",	45),
	FALLING_DUST("fallingdust",	46);

	private final String particleName;
	private final int particleId;
	private final int optionalParamSize;

	private Particles(String particleName, int particleId, int optionalParamSize) {
		this.particleName = particleName;
		this.particleId = particleId;
		this.optionalParamSize = optionalParamSize;
	}

	private Particles(String paramString, int paramInt) {
		this(paramString, paramInt, 0);
	}

	public String getName() {
		return this.particleName;
	}

	public int getId() {
		return this.particleId;
	}

	public int getOptionalParametersSize() {
		return this.optionalParamSize;
	}
	
	public static Particles getParticleByName(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException ex) {
			for (Particles particle : values())
				if (particle.getName().equalsIgnoreCase(name))
					return particle;
		}
		return null;
	}

	public static class RevengeParticle {

		public Particles hitParticle;
		public Float particleFx, particleFy, particleFz, particleSpeed;
		public Integer particleCount, particleArg1, particleArg2, particleArg3;
		public Boolean asRGBParticle, asItemCrackParticle, asBlockCrackBlockDustParticle;
		public RevengePlugin instance;

		public RevengeParticle(RevengePlugin instance, String input) {
			this.instance = instance;
			hitParticle = Particles.CRIT;
			particleFx = 0.5f;
			particleFy = 0.3f;
			particleFz = 0.5f;
			particleSpeed = 0.1f;
			particleCount = 3;
			particleArg1 = 0;
			particleArg2 = 0;
			particleArg3 = 0;
			asRGBParticle = false;
			asItemCrackParticle = false;
			asBlockCrackBlockDustParticle = false;
			
			String type = "Basic particle";
			try {
				String[] particleSplitted = input.split(";");
				hitParticle = getParticleByName(particleSplitted[0]);
				if (particleSplitted.length >= 2) {
					particleCount = Integer.parseInt(particleSplitted[1]);
				}
				if (particleSplitted.length >= 3) {
					particleSpeed = Float.parseFloat(particleSplitted[2]);
				}
				if (particleSplitted.length >= 4) {
					particleFx = Float.parseFloat(particleSplitted[3]);
				}
				if (particleSplitted.length >= 5) {
					particleFy = Float.parseFloat(particleSplitted[4]);
				}
				if (particleSplitted.length >= 6) {
					particleFz = Float.parseFloat(particleSplitted[5]);
				}

				if (hitParticle == Particles.ITEM_CRACK && particleSplitted.length >= 8) {
					asItemCrackParticle = true;
					particleArg1 = Integer.parseInt(particleSplitted[6]);
					particleArg2 = Integer.parseInt(particleSplitted[7]);
					type = "ItemCrack particle";
				} else if ((hitParticle == Particles.BLOCK_CRACK || hitParticle == Particles.BLOCK_DUST)
						&& particleSplitted.length >= 8) {
					asBlockCrackBlockDustParticle = true;
					if (hitParticle == Particles.BLOCK_CRACK) {
						particleArg1 = (Integer.parseInt(particleSplitted[6])
								+ (Integer.parseInt(particleSplitted[7]) << 12));
					} else if (hitParticle == Particles.BLOCK_DUST) {
						particleArg1 = Integer.parseInt(particleSplitted[6]);
					}
					type = "BlockCrack/Dust particle";
				} else if ((hitParticle == Particles.REDSTONE || hitParticle == Particles.SPELL_MOB
						|| hitParticle == Particles.SPELL_MOB_AMBIENT || hitParticle == Particles.NOTE) && particleSplitted.length >= 9) {
					asRGBParticle = true;
					particleArg1 = Integer.parseInt(particleSplitted[6]);
					particleArg2 = Integer.parseInt(particleSplitted[7]);
					particleArg3 = Integer.parseInt(particleSplitted[8]);
					type = "Colored particle";
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				instance.getLogger().info("[DON'T WORRY] Particle error: " + ex.getMessage());
			}
			instance.getLogger()
					.info("Particle set ! / Name: " + hitParticle.toString() + " / Count: " + particleCount
							+ " / Speed: " + particleSpeed + " / dX: " + particleFx + " / dY: " + particleFy + " / dZ: "
							+ particleFz + " / Arg1: " + particleArg1 + " / Arg2: " + particleArg2 + " / Arg3: "
							+ particleArg3 + " / type: " + type);

		}

		public void playParticles(Location location) {
			location = location.add(0, 1, 0);
			Random rand = new Random();
			Float randX = rand.nextFloat() * particleFx;
			if (rand.nextBoolean()) 
				randX = -randX;
			Float randY = rand.nextFloat() * particleFy;
			if (rand.nextBoolean()) 
				randY = -randY;
			Float randZ = rand.nextFloat() * particleFz;
			if (rand.nextBoolean()) 
				randZ = -randZ;
			location.add(randX, randY, randZ);
			if (asRGBParticle) {
				for (int i = 0; i < particleCount; i++)
					this.instance.IParticleSpawner.playParticles(hitParticle, location, (float) color(particleArg1), (float) color(particleArg2), (float) color(particleArg3), 
							0, (float) 1.0, new int[0]);
			} else if (asItemCrackParticle) {
				this.instance.IParticleSpawner.playParticles(hitParticle, location, 0F, 0F, 0F,
						particleCount, particleSpeed, particleArg1, particleArg2);
			} else if (asBlockCrackBlockDustParticle) {
				this.instance.IParticleSpawner.playParticles(hitParticle, location, 0F, 0F, 0F,
						particleCount, particleSpeed, particleArg1);
			} else {
				this.instance.IParticleSpawner.playParticles(hitParticle, location, 0F, 0F, 0F,
						particleCount, particleSpeed);
			}
		}

		public void playBloodParticles(Location location) {
			this.instance.IParticleSpawner.playParticles(Particles.BLOCK_CRACK, location, 0.0f, 0.0f, 0.0f, 1, 0.1f, 152);
		}
		
		private double color(double color) {
			color = color <= 0.0D ? 1.0D : color;
			return color / 255.0D;
		}
	}
}
