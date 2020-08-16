package fr.royalpha.revenge.core.legacy;

import fr.royalpha.revenge.core.RevengePlugin;
import fr.royalpha.revenge.core.handler.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class LegacyUtils {

    public static boolean isMarineAnimal(EntityType ent) {
        if (RevengePlugin.getVersionManager().getVersion().newerOrEqualTo(MinecraftVersion.v1_13_R1)) {
            return (ent == EntityType.valueOf("COD") || ent == EntityType.valueOf("SALMON") || ent == EntityType.valueOf("DOLPHIN"));
        } else {
            return (ent == EntityType.valueOf("SQUID"));
        }
    }

    public static boolean isWater(Material material) {
        if (RevengePlugin.getVersionManager().getVersion().newerOrEqualTo(MinecraftVersion.v1_13_R1)) {
            return (material == Material.valueOf("WATER"));
        } else {
            return (material == Material.valueOf("WATER") || material == Material.valueOf("STATIONARY_WATER"));
        }
    }
}
