package fr.roytreo.revenge.v1_13_R1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.roytreo.revenge.core.util.ReflectionUtils;
import fr.roytreo.revenge.core.version.I13Helper;

public class Post13Helper implements I13Helper {

	@Override
	public boolean isWater(Material material) {
		return (material == Material.WATER);
	}

	@Override
	public ItemStack getSkull(OfflinePlayer player) {
		ItemStack skull = new ItemStack(Material.valueOf("PLAYER_HEAD"), 1);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		Method met;
		try {
			met = ReflectionUtils.getMethod(skullMeta.getClass(), "setOwningPlayer", OfflinePlayer.class);
			met.setAccessible(true);
			met.invoke(skullMeta, player);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			Bukkit.getLogger().info("Don't worry about this error. Just ask the developer !");
		}
		skull.setItemMeta(skullMeta);
		return skull;
	}

	@Override
	public boolean isMarineAnimal(EntityType ent) {
		return (ent == EntityType.SQUID || ent == EntityType.valueOf("COD") || ent == EntityType.valueOf("SALMON") || ent == EntityType.valueOf("DOLPHIN"));
	}

}
