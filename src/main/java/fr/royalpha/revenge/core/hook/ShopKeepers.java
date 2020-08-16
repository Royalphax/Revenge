package fr.royalpha.revenge.core.hook;

import fr.royalpha.revenge.core.hook.base.HookManager;

public class ShopKeepers implements HookManager {

	public ShopKeepers() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
