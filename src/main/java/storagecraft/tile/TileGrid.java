package storagecraft.tile;

public class TileGrid extends TileSC implements IMachine {
	@Override
	public void onConnected(TileController controller) {
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public int getEnergyUsage() {
		return 10;
	}
}
