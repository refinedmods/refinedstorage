package com.refinedmods.refinedstorage.apiimpl.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public class CraftingRequestInfo implements ICraftingRequestInfo {
    private static final String NBT_FLUID = "Fluid";
    private static final String NBT_STACK = "Stack";

    private ItemStack item;
    private FluidStack fluid;

    public CraftingRequestInfo(CompoundTag tag) throws CraftingTaskReadException {
        if (!tag.getBoolean(NBT_FLUID)) {
            item = StackUtils.deserializeStackFromNbt(tag.getCompound(NBT_STACK));

            if (item.isEmpty()) {
                throw new CraftingTaskReadException("Extractor stack is empty");
            }
        } else {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(NBT_STACK));

            if (fluid.isEmpty()) {
                throw new CraftingTaskReadException("Extractor fluid stack is empty");
            }
        }
    }

    public CraftingRequestInfo(ItemStack item) {
        this.item = item;
    }

    public CraftingRequestInfo(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Nullable
    @Override
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(NBT_FLUID, fluid != null);

        if (fluid != null) {
            tag.put(NBT_STACK, fluid.writeToNBT(new CompoundTag()));
        } else {
            tag.put(NBT_STACK, StackUtils.serializeStackToNbt(item));
        }

        return tag;
    }
}
