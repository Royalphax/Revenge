package fr.roytreo.revenge.core.hook.base;

public interface HookManager {

	public boolean get(Getter getter, Object... args);
	
	public static enum Getter {
		BOOLEAN_PLAYER_HAS_PVP_DISABLED(),
		BOOLEAN_PLAYER_IS_VANISHED();
	}
}
