package fr.roytreo.revenge.core.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.hook.base.HookManager;
import me.NoChance.PvPManager.PvPlayer;
import me.NoChance.PvPManager.Managers.PlayerHandler;
import net.md_5.bungee.api.ChatColor;

public class PVPManager implements HookManager {

	public me.NoChance.PvPManager.PvPManager pvpManager;
	public PlayerHandler playerHandler;

	public PVPManager() {
		this.pvpManager = (me.NoChance.PvPManager.PvPManager) Bukkit.getPluginManager().getPlugin("PvPManager");
		this.playerHandler = this.pvpManager.getPlayerHandler();
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		if (getter == Getter.BOOLEAN_PLAYER_HAS_PVP_DISABLED && args[0] instanceof Player) {
			PvPlayer pvplayer = this.playerHandler.get((Player) args[0]);
			if (!pvplayer.hasPvPEnabled()) {
				pvplayer.message(ChatColor.RED + "Your PvP is disabled. This entity can't attack you.");
				return true;
			}
		}
		return false;
	}
}
