package fr.roytreo.revenge.core.softdepend;

import fr.roytreo.revenge.core.softdepend.base.SoftDepend;

public class DeathMessagesPrime implements SoftDepend {

	public DeathMessagesPrime() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
