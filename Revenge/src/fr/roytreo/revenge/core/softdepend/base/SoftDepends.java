package fr.roytreo.revenge.core.softdepend.base;

public enum SoftDepends {

	Citizens("Citizens"),
	UltraCosmetics("UltraCosmetics"),
	ShopKeepers("Shopkeepers"),
	PVPManager("PvPManager"),
	DeathMessagesPrime("DeathMessagesPrime");

	private String pluginName;
	
	private SoftDepends(String pluginName) {
		this.pluginName = pluginName;
	}
	
	public String getPluginName() {
		return this.pluginName;
	}
}
