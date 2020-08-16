package fr.royalpha.revenge.core.hook.base;

public enum Hooks {

	Citizens("Citizens"),
	UltraCosmetics("UltraCosmetics"),
	ShopKeepers("Shopkeepers"),
	PVPManager("PvPManager"),
	VanishNoPacket("VanishNoPacket"),
	DeathMessagesPrime("DeathMessagesPrime", "NOTE: Management of death messages was disabled to let DeathMessagesPrime handle them.");

	private String pluginName;
	private String info;
	
	private Hooks(String pluginName) {
		this(pluginName, "");
	}
	
	private Hooks(String pluginName, String info) {
		this.pluginName = pluginName;
		this.info = info;
	}
	
	public String getPluginName() {
		return this.pluginName;
	}
	
	public String getInfo() {
		return info;
	}
}
