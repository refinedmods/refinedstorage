package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface IModeSetting
{
	public boolean isWhitelist();

	public boolean isBlacklist();

	public void setToWhitelist();

	public void setToBlacklist();

	public BlockPos getMachinePos();
}
