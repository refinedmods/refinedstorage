package storagecraft.tile;

public interface IMachine {
	public void onConnected(TileController controller);

	public void onDisconnected();
}
