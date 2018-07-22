package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class CraftingExtractorStack {
    private static final String NBT_FLUID = "Fluid";
    private static final String NBT_STACK = "Stack";
    private static final String NBT_STATUS = "Status";

    private ItemStack item;
    private FluidStack fluid;
    private CraftingExtractorStatus status = CraftingExtractorStatus.MISSING;

    public CraftingExtractorStack(ItemStack item) {
        this.item = item;
    }

    public CraftingExtractorStack(FluidStack fluid) {
        this.fluid = fluid;
    }

    public CraftingExtractorStack(NBTTagCompound tag) throws CraftingTaskReadException {
        if (!tag.getBoolean(NBT_FLUID)) {
            item = StackUtils.deserializeStackFromNbt(tag.getCompoundTag(NBT_STACK));

            if (item.isEmpty()) {
                throw new CraftingTaskReadException("Extractor stack is empty");
            }
        } else {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_STACK));

            if (fluid == null) {
                throw new CraftingTaskReadException("Extractor fluid stack is empty");
            }
        }

        status = CraftingExtractorStatus.values()[tag.getInteger(NBT_STATUS)];
    }

    @Nullable
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public FluidStack getFluid() {
        return fluid;
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean(NBT_FLUID, fluid != null);

        if (fluid != null) {
            tag.setTag(NBT_STACK, fluid.writeToNBT(new NBTTagCompound()));
        } else {
            tag.setTag(NBT_STACK, StackUtils.serializeStackToNbt(item));
        }

        tag.setInteger(NBT_STATUS, status.ordinal());

        return tag;
    }

    public CraftingExtractorStatus getStatus() {
        return status;
    }

    public void setStatus(CraftingExtractorStatus status) {
        this.status = status;
    }
}
