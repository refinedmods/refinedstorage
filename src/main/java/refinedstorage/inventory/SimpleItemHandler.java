package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class SimpleItemHandler extends ItemStackHandler {
    private TileEntity tile;
    private IItemValidator[] validators;

    public SimpleItemHandler(int size, TileEntity tile, IItemValidator... validators) {
        super(size);

        this.tile = tile;
        this.validators = validators;
    }

    public SimpleItemHandler(int size, IItemValidator... validators) {
        this(size, null, validators);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        boolean mayInsert = validators.length > 0 ? false : true;

        for (IItemValidator validator : validators) {
            if (validator.valid(stack)) {
                mayInsert = true;
                break;
            }
        }

        if (mayInsert) {
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (tile != null) {
            tile.markDirty();
        }
    }
}
