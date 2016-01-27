package storagecraft.tile;

import net.minecraft.util.BlockPos;

public interface IRedstoneModeSetting
{
	public RedstoneMode getRedstoneMode();

	public void setRedstoneMode(RedstoneMode mode);

	public BlockPos getPos();
}
