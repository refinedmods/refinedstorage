package storagecraft.tile;

import net.minecraft.util.BlockPos;

public interface IWhitelistBlacklistSetting
{
	public boolean isWhitelist();

	public boolean isBlacklist();

	public void setToWhitelist();

	public void setToBlacklist();

	public BlockPos getMachinePos();
}
