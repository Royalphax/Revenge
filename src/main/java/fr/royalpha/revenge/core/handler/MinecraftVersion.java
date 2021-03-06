package fr.royalpha.revenge.core.handler;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public enum MinecraftVersion {
    UNKNOWN(-1),
    v1_7_R1(10701),
    v1_7_R2(10702),
    v1_7_R3(10703),
    v1_7_R4(10704),
    v1_8_R1(10801),
    v1_8_R2(10802),
    v1_8_R3(10803),
    v1_9_R1(10901),
    v1_9_R2(10902),
    v1_10_R1(11001),
    v1_11_R1(11101),
    v1_12_R1(11201),
    v1_13_R1(11301),
    v1_13_R2(11302),
    v1_14_R1(11401),
    v1_15_R1(11501),
    v1_16_R1(11601),
    v1_16_R2(11602),
    v1_16_R3(11603);

    private int version;

    private MinecraftVersion(int version) {
        this.version = version;
    }

    public int version() {
        return this.version;
    }

    public boolean olderThan(MinecraftVersion version) {
        return version() < version.version();
    }

    public boolean olderOrEqualTo(MinecraftVersion version) {
        return version() <= version.version();
    }

    public boolean newerOrEqualTo(MinecraftVersion version) {
        return version() >= version.version();
    }

    public boolean newerThan(MinecraftVersion version) {
        return version() >= version.version();
    }

    public boolean inRange(MinecraftVersion oldVersion, MinecraftVersion newVersion) {
        return (newerOrEqualTo(oldVersion)) && (olderThan(newVersion));
    }

    public boolean inRange(MinecraftVersion... versions)
    {
        return inRange(Arrays.asList(versions));
    }

    public boolean inRange(List<MinecraftVersion> versions)
    {
        for (MinecraftVersion ver : versions)
            if (ver.version() == this.version())
                return true;
        return false;
    }

    public boolean matchesPackageName(String packageName) {
        return packageName.toLowerCase().contains(name().toLowerCase());
    }

    public static MinecraftVersion getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String versionPackage = name.substring(name.lastIndexOf('.') + 1) + ".";
        for (MinecraftVersion version : values()) {
            if (version.matchesPackageName(versionPackage)) {
                return version;
            }
        }
        Bukkit.getLogger().log(Level.WARNING, "Failed to find version enum for '" + name + "'/'" + versionPackage + "'");
        return UNKNOWN;
    }
}