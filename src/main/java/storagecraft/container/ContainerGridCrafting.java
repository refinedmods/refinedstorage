package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import storagecraft.tile.TileGrid;

public class ContainerGridCrafting extends Container
{
	private TileGrid grid;

	public ContainerGridCrafting(TileGrid grid)
	{
		this.grid = grid;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return false;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory)
	{
		grid.onCraftingMatrixChanged();
	}
}
