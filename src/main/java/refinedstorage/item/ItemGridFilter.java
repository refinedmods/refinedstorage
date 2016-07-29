package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageItems;

public class ItemGridFilter extends ItemBase {
    public ItemGridFilter() {
        super("grid_filter");

        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                return new ActionResult(EnumActionResult.SUCCESS, new ItemStack(RefinedStorageItems.GRID_FILTER));
            }

            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.GRID_FILTER, world, hand.ordinal(), 0, 0);

            return new ActionResult(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult(EnumActionResult.PASS, stack);
    }
}
