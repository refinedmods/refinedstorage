package storagecraft.tile.settings;

import net.minecraft.util.BlockPos;

public interface ICompareSetting
{
	public int getCompare();

	public void setCompare(int compare);

	public BlockPos getMachinePos();
}
