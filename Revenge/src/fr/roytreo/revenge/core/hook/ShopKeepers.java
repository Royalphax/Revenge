package fr.roytreo.revenge.core.hook;

import fr.roytreo.revenge.core.hook.base.HookManager;

public class ShopKeepers implements HookManager {

	public ShopKeepers() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
