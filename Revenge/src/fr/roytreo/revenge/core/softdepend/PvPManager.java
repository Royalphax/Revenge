package fr.roytreo.revenge.core.softdepend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.roytreo.revenge.core.RevengePlugin;
import me.NoChance.PvPManager.PvPlayer;
import me.NoChance.PvPManager.Config.Messages;
import me.NoChance.PvPManager.Managers.PlayerHandler;

public class PvPManager {

	public me.NoChance.PvPManager.PvPManager pvpManager;
	public PlayerHandler playerHandler;

	public PvPManager(RevengePlugin plugin) {
		this.pvpManager = (me.NoChance.PvPManager.PvPManager) Bukkit.getPluginManager().getPlugin("PvPManager");
		this.playerHandler = this.pvpManager.getPlayerHandler();
		plugin.getLogger().info("PvPManager hooked.");
	}

	public PlayerHandler getPlayerHandler() {
		return this.playerHandler;
	}
	
	public boolean hasPvPEnabled(Player player)
	{
		PvPlayer pvplayer = this.playerHandler.get(player);
		if (!pvplayer.hasPvPEnabled()) {
			pvplayer.message(Messages.pvpDisabled());
			return false;
		} else {
			return true;
		}
	}
}
