package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;

public interface ICompareSetting
{
	public int getCompare();

	public void setCompare(int compare);

	public BlockPos getMachinePos();
}
