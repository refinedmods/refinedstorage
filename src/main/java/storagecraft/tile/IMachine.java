package storagecraft.tile;

public interface IMachine {
	public int getEnergyUsage();

	public void onConnected(TileController controller);

	public void onDisconnected();
}
