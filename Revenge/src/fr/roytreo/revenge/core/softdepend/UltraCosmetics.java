package fr.roytreo.revenge.core.softdepend;

import fr.roytreo.revenge.core.softdepend.base.SoftDepend;

public class UltraCosmetics implements SoftDepend {

	public UltraCosmetics() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
