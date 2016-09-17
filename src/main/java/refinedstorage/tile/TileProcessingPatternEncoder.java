package refinedstorage.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.gui.GuiProcessingPatternEncoder;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public class TileProcessingPatternEncoder extends TileBase {
    public static final TileDataParameter<Boolean> PATTERN_OREDICTED = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileProcessingPatternEncoder>() {
        @Override
        public Boolean getValue(TileProcessingPatternEncoder tile) {
            return tile.patternOredicted;
        }
    }, new ITileDataConsumer<Boolean, TileProcessingPatternEncoder>() {
        @Override
        public void setValue(TileProcessingPatternEncoder tile, Boolean value) {
            tile.patternOredicted = value;
            tile.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiProcessingPatternEncoder) {
            ((GuiProcessingPatternEncoder) Minecraft.getMinecraft().currentScreen).updatePatternOredicted(parameter.getValue());
        }
    });

    private static final String NBT_PATTERN_OREDICTED = "PatternOredicted";

    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RefinedStorageItems.PATTERN));
    private ItemHandlerBasic configuration = new ItemHandlerBasic(9 * 2, this);

    private boolean patternOredicted = false;

    public TileProcessingPatternEncoder() {
        dataManager.addWatchedParameter(PATTERN_OREDICTED);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(patterns, 0, tag);
        writeItems(configuration, 1, tag);

        tag.setBoolean(NBT_PATTERN_OREDICTED, patternOredicted);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(patterns, 0, tag);
        readItems(configuration, 1, tag);

        if (tag.hasKey(NBT_PATTERN_OREDICTED)) {
            patternOredicted = tag.getBoolean(NBT_PATTERN_OREDICTED);
        }
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            ItemStack pattern = new ItemStack(RefinedStorageItems.PATTERN);

            ItemPattern.setOredicted(pattern, patternOredicted);

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
