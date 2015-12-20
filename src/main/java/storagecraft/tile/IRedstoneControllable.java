package storagecraft.tile;

public interface IRedstoneControllable {
	public RedstoneMode getRedstoneMode();

	public void setRedstoneMode(RedstoneMode mode);

	public int getX();

	public int getY();

	public int getZ();
}
