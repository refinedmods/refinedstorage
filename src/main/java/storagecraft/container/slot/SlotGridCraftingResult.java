package storagecraft.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;

public class SlotGridCraftingResult extends SlotCrafting
{
	public SlotGridCraftingResult(EntityPlayer player, IInventory craftingMatrix, IInventory craftingResult, int id, int x, int y)
	{
		super(player, craftingMatrix, craftingResult, id, x, y);
	}
}
