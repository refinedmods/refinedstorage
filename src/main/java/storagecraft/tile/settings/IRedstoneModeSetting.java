package storagecraft.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface IRedstoneModeSetting
{
	public RedstoneMode getRedstoneMode();

	public void setRedstoneMode(RedstoneMode mode);

	public BlockPos getMachinePos();
}
