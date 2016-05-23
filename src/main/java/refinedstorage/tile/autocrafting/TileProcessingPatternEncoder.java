package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileBase;

public class TileProcessingPatternEncoder extends TileBase {
    private BasicItemHandler patterns = new BasicItemHandler(2, this, new BasicItemValidator(RefinedStorageItems.PATTERN));
    private BasicItemHandler configuration = new BasicItemHandler(9 * 2, this);

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(patterns, 0, tag);
        RefinedStorageUtils.writeItems(configuration, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(patterns, 0, nbt);
        RefinedStorageUtils.readItems(configuration, 1, nbt);
    }

    public void onCreatePattern() {
        if (mayCreatePattern()) {
            ItemStack pattern = new ItemStack(RefinedStorageItems.PATTERN);

            ItemPattern.setProcessing(pattern, true);

            for (int i = 0; i < 18; ++i) {
                if (configuration.getStackInSlot(i) != null) {
                    if (i >= 9) {
                        ItemPattern.addOutput(pattern, configuration.getStackInSlot(i));
                    } else {
                        ItemPattern.addInput(pattern, configuration.getStackInSlot(i));
                    }
                }
            }

            patterns.extractItem(0, 1, false);
            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean mayCreatePattern() {
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

    public BasicItemHandler getPatterns() {
        return patterns;
    }

    public BasicItemHandler getConfiguration() {
        return configuration;
    }

    @Override
    public IItemHandler getDroppedItems() {
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
