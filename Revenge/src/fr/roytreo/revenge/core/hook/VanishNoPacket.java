package fr.roytreo.revenge.core.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

import fr.roytreo.revenge.core.hook.base.HookManager;

public class VanishNoPacket implements HookManager {

	public VanishPlugin vanishPlugin;
	public VanishManager vanishManager;

	public VanishNoPacket() {
		this.vanishPlugin = (VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket");
		this.vanishManager = this.vanishPlugin.getManager();
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		if (getter == Getter.BOOLEAN_PLAYER_IS_VANISHED && args[0] instanceof Player) {
			Player player = (Player) args[0];
			if (this.vanishManager.isVanished(player)) {
				return true;
			}
		}
		return false;
	}
}
