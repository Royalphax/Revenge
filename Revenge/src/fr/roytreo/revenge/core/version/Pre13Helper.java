package fr.roytreo.revenge.core.version;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Pre13Helper implements I13Helper {

	@Override
	public boolean isWater(Material material) {
		return (material == Material.WATER || material == Material.STATIONARY_WATER);
	}

	@Override
	public ItemStack getSkull(OfflinePlayer player) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(player.getName());
		skull.setItemMeta(skullMeta);
		return skull;
	}

	@Override
	public boolean isMarineAnimal(EntityType ent) {
		return (ent == EntityType.SQUID);
	}

}
