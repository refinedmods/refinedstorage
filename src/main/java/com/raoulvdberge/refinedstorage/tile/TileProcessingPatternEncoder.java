package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileProcessingPatternEncoder extends TileBase {
    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RSItems.PATTERN));
    private ItemHandlerBasic configuration = new ItemHandlerBasic(9 * 2, this);

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(patterns, 0, tag);
        RSUtils.writeItems(configuration, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(patterns, 0, tag);
        RSUtils.readItems(configuration, 1, tag);
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            ItemStack pattern = new ItemStack(RSItems.PATTERN);

            for (int i = 0; i < 18; ++i) {
                if (configuration.getStackInSlot(i) != null) {
                    if (i >= 9) {
                        for (int j = 0; j < configuration.getStackInSlot(i).stackSize; ++j) {
                            ItemPattern.addOutput(pattern, ItemHandlerHelper.copyStackWithSize(configuration.getStackInSlot(i), 1));
                        }
                    } else {
                        ItemPattern.setSlot(pattern, i, configuration.getStackInSlot(i));
                    }
                }
            }

            patterns.extractItem(0, 1, false);
            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean canCreatePattern() {
        int inputsFilled = 0, outputsFilled = 0;

        for (int i = 0; i < 9; ++i) {
            if (configuration.getStackInSlot(i) != null) {
                inputsFilled++;
            }
        }

        for (int i = 9; i < 18; ++i) {
            if (configuration.getStackInSlot(i) != null) {
                outputsFilled++;
            }
        }

        return inputsFilled > 0 && outputsFilled > 0 && patterns.getStackInSlot(0) != null && patterns.getStackInSlot(1) == null;
    }

    public ItemHandlerBasic getPatterns() {
        return patterns;
    }

    public ItemHandlerBasic getConfiguration() {
        return configuration;
    }

    @Override
    public IItemHandler getDrops() {
        return patterns;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) patterns;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
