package fr.roytreo.revenge.core.softdepend.base;

public interface SoftDepend {

	public boolean get(Getter getter, Object... args);
	
	public static enum Getter {
		BOOLEAN_PLAYER_HAS_PVP_ENABLED();
	}
}
