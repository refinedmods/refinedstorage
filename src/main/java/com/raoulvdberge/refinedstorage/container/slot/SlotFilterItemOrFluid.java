package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SlotFilterItemOrFluid extends SlotFilter {
    public interface IFluidAmountChangeListener {
        void onChangeRequested(int slot, int amount);
    }

    private IType type;

    @Nullable
    private IFluidAmountChangeListener listener;
    private int maxFluidAmount;

    private Supplier<Boolean> enableHandler = () -> true;

    public SlotFilterItemOrFluid(IType type, int id, int x, int y, int flags, @Nullable IFluidAmountChangeListener listener, int maxFluidAmount) {
        super(null, id, x, y, flags);

        this.type = type;
        this.listener = listener;
        this.maxFluidAmount = maxFluidAmount;
    }

    public SlotFilterItemOrFluid(IType type, int id, int x, int y, int flags, @Nullable IFluidAmountChangeListener listener, int maxFluidAmount, Supplier<Boolean> enableHandler) {
        this(type, id, x, y, flags, listener, maxFluidAmount);

        this.enableHandler = enableHandler;
    }

    public SlotFilterItemOrFluid(IType type, int id, int x, int y) {
        this(type, id, x, y, 0, null, 0);
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
        return (type.getType() == IType.ITEMS || type.isServer()) ? super.getStack() : ItemStack.EMPTY;
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

    @Nullable
    public IFluidAmountChangeListener getFluidAmountChangeListener() {
        return listener;
    }

    public int getMaxFluidAmount() {
        return maxFluidAmount;
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.get();
    }
}
