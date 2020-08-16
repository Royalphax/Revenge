package fr.royalpha.revenge.core.hook;

import fr.royalpha.revenge.core.hook.base.HookManager;

public class Citizens implements HookManager {

	public Citizens() {
	}

	@Override
	public boolean get(Getter getter, Object... args) {
		return false;
	}
}
