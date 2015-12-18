package storagecraft.tile;

public enum RedstoneMode {
	IGNORE(0),
	HIGH(1),
	LOW(2);

	public final int id;

	RedstoneMode(int id) {
		this.id = id;
	}

	public static RedstoneMode getById(int id) {
		for (RedstoneMode control : values()) {
			if (control.id == id) {
				return control;
			}
		}

		return null;
	}
}
