package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftItems;
import storagecraft.container.slot.IItemValidator;
import storagecraft.container.slot.SlotFiltered;
import storagecraft.item.ItemPattern;
import storagecraft.tile.crafting.TileCrafter;

public class ContainerCrafter extends ContainerBase
{
	public ContainerCrafter(final EntityPlayer player, TileCrafter crafter)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotFiltered(crafter, i, 8 + (18 * i), 20, new IItemValidator()
			{
				@Override
				public boolean isValid(ItemStack stack)
				{
					return stack.getItem() == StorageCraftItems.PATTERN && ItemPattern.isValid(player.worldObj, stack);
				}
			}));
		}

		addPlayerInventory(8, 55);
	}
}
