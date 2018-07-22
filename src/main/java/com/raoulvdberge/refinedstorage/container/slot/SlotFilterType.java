package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class SlotFilterType extends SlotFilter {
    private IType type;

    public SlotFilterType(IType type, int id, int x, int y, int flags) {
        super(null, id, x, y, flags);

        this.type = type;
    }

    public SlotFilterType(IType type, int id, int x, int y) {
        this(type, id, x, y, 0);
    }

    @Override
    public IItemHandler getItemHandler() {
        return type.getFilterInventory();
    }

    @Override
    public boolean isBlockAllowed() {
        return super.isBlockAllowed() && type.getType() == IType.ITEMS;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return (type.getType() == IType.ITEMS || !((NetworkNode) type).getWorld().isRemote) ? super.getStack() : ItemStack.EMPTY;
    }

    public ItemStack getActualStack() {
        return super.getStack();
    }

    public IType getType() {
        return type;
    }

    @Override
    public int getInitialAmount(ItemStack stack) {
        if (type.getType() == IType.FLUIDS && isSizeAllowed()) {
            return Fluid.BUCKET_VOLUME;
        }

        return super.getInitialAmount(stack);
    }
}
