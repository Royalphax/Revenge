package fr.roytreo.revenge.core.version;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface I13Helper {

	boolean isWater(Material material);
	
	ItemStack getSkull(OfflinePlayer player);
	
	boolean isMarineAnimal(EntityType ent);
}
