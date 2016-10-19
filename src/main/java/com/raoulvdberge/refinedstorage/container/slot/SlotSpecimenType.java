package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

public class SlotSpecimenType extends SlotSpecimen {
    private IType type;

    public SlotSpecimenType(IType type, int id, int x, int y, int flags) {
        super(null, id, x, y, flags);

        this.type = type;
    }

    public SlotSpecimenType(IType type, int id, int x, int y) {
        this(type, id, x, y, 0);
    }

    @Override
    public IItemHandler getItemHandler() {
        return type.getFilterInventory();
    }

    @Override
    public boolean isWithSize() {
        return super.isWithSize() && type.getType() != IType.FLUIDS;
    }

    @Override
    public boolean isBlockOnly() {
        return super.isBlockOnly() && type.getType() == IType.ITEMS;
    }

    @Override
    public ItemStack getStack() {
        return (type.getType() == IType.ITEMS || !((TileEntity) type).getWorld().isRemote) ? super.getStack() : null;
    }

    public ItemStack getRealStack() {
        return super.getStack();
    }
}
