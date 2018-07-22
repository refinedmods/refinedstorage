package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class CraftingRequestInfo implements ICraftingRequestInfo {
    private static final String NBT_FLUID = "Fluid";
    private static final String NBT_STACK = "Stack";

    private ItemStack item;
    private FluidStack fluid;

    public CraftingRequestInfo(NBTTagCompound tag) throws CraftingTaskReadException {
        if (!tag.getBoolean(NBT_FLUID)) {
            item = StackUtils.deserializeStackFromNbt(tag.getCompoundTag(NBT_STACK));

            if (item.isEmpty()) {
                throw new CraftingTaskReadException("Extractor stack is empty");
            }
        } else {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_STACK));

            if (fluid == null) {
                throw new CraftingTaskReadException("Extractor fluid stack is emty");
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
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean(NBT_FLUID, fluid != null);

        if (fluid != null) {
            tag.setTag(NBT_STACK, fluid.writeToNBT(new NBTTagCompound()));
        } else {
            tag.setTag(NBT_STACK, StackUtils.serializeStackToNbt(item));
        }

        return tag;
    }
}
