package fr.roytreo.revenge.core.softdepend;

import fr.roytreo.revenge.core.softdepend.base.SoftDepend;

public class Citizens implements SoftDepend {

	public Citizens() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
