package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import refinedstorage.tile.TileBase;

public class GridFilterItemHandler extends ItemStackHandler {
    private ItemStack filter;

    public GridFilterItemHandler(ItemStack filter) {
        super(9 * 3);

        this.filter = filter;

        if (filter.hasTagCompound()) {
            TileBase.readItems(this, 0, filter.getTagCompound());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (!filter.hasTagCompound()) {
            filter.setTagCompound(new NBTTagCompound());
        }

        TileBase.writeItems(this, 0, filter.getTagCompound());
    }
}
