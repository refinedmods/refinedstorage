package storagecraft.tile.settings;

import net.minecraft.util.BlockPos;

public interface IModeSetting
{
	public boolean isWhitelist();

	public boolean isBlacklist();

	public void setToWhitelist();

	public void setToBlacklist();

	public BlockPos getMachinePos();
}
