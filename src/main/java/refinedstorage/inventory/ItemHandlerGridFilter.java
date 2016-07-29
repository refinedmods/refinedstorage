package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import refinedstorage.tile.TileBase;

public class ItemHandlerGridFilter extends ItemStackHandler {
    private ItemStack stack;

    public ItemHandlerGridFilter(ItemStack stack) {
        super(9 * 3);

        this.stack = stack;

        if (stack.hasTagCompound()) {
            TileBase.readItems(this, 0, stack.getTagCompound());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        TileBase.writeItems(this, 0, stack.getTagCompound());
    }
}
