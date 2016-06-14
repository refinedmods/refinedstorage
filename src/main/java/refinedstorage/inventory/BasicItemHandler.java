package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class BasicItemHandler extends ItemStackHandler {
    private TileEntity tile;
    private IItemValidator[] validators;

    public BasicItemHandler(int size, TileEntity tile, IItemValidator... validators) {
        super(size);

        this.tile = tile;
        this.validators = validators;
    }

    public BasicItemHandler(int size, IItemValidator... validators) {
        this(size, null, validators);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (validators.length > 0) {
            for (IItemValidator validator : validators) {
                if (validator.valid(stack)) {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (tile != null) {
            tile.markDirty();
        }
    }
}
