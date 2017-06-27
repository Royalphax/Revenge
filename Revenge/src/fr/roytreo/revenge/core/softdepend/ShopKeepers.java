package fr.roytreo.revenge.core.softdepend;

import fr.roytreo.revenge.core.softdepend.base.SoftDepend;

public class ShopKeepers implements SoftDepend {

	public ShopKeepers() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
