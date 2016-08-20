package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemPattern;

public class TileProcessingPatternEncoder extends TileBase {
    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RefinedStorageItems.PATTERN));
    private ItemHandlerBasic configuration = new ItemHandlerBasic(9 * 2, this);

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(patterns, 0, tag);
        writeItems(configuration, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(patterns, 0, tag);
        readItems(configuration, 1, tag);
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            ItemStack pattern = new ItemStack(RefinedStorageItems.PATTERN);

            ItemPattern.setProcessing(pattern, true);

            for (int i = 0; i < 18; ++i) {
                if (configuration.getStackInSlot(i) != null) {
                    for (int j = 0; j < configuration.getStackInSlot(i).stackSize; ++j) {
                        if (i >= 9) {
                            ItemPattern.addOutput(pattern, ItemHandlerHelper.copyStackWithSize(configuration.getStackInSlot(i), 1));
                        } else {
                            ItemPattern.addInput(pattern, ItemHandlerHelper.copyStackWithSize(configuration.getStackInSlot(i), 1));
                        }
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
