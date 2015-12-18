package storagecraft.tile;

public enum ImporterMode {
	WHITELIST(0), BLACKLIST(1);

	public final int id;

	ImporterMode(int id) {
		this.id = id;
	}

	public ImporterMode next() {
		ImporterMode next = getById(id + 1);

		if (next == null) {
			return getById(0);
		}

		return next;
	}

	public static ImporterMode getById(int id) {
		for (ImporterMode mode : values()) {
			if (mode.id == id) {
				return mode;
			}
		}

		return null;
	}
}
