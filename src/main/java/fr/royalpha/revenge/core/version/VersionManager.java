package fr.royalpha.revenge.core.version;

import fr.royalpha.revenge.core.RevengePlugin;
import fr.royalpha.revenge.core.handler.MinecraftVersion;
import fr.royalpha.revenge.core.util.ReflectionUtils;

public class VersionManager {

    private MinecraftVersion version;
    private INMSUtils INMSUtils;
    private IParticleSpawner IParticleSpawner;

    public VersionManager(MinecraftVersion version) throws ReflectiveOperationException {
        this.version = version;
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() throws ReflectiveOperationException {
        this.INMSUtils = loadModule("NMSUtils");
        this.IParticleSpawner = loadModule("ParticleSpawner");
    }

    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name) throws ReflectiveOperationException {
        return (T) loadModule(name, this.version);
    }

    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name, MinecraftVersion version) throws ReflectiveOperationException {
        return (T) ReflectionUtils.instantiateObject(Class.forName(RevengePlugin.PACKAGE + "." + version.toString() + "." + name));
    }

    public INMSUtils getNMSUtils() {
        return this.INMSUtils;
    }

    public IParticleSpawner getParticleFactory() {
        return this.IParticleSpawner;
    }

    public MinecraftVersion getVersion() {
        return this.version;
    }
}