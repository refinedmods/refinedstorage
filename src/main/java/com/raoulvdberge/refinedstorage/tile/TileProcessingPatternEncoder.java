package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.gui.GuiProcessingPatternEncoder;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileProcessingPatternEncoder extends TileBase {
    private static final String NBT_OREDICT_PATTERN = "OredictPattern";

    public static final TileDataParameter<Boolean> OREDICT_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileProcessingPatternEncoder>() {
        @Override
        public Boolean getValue(TileProcessingPatternEncoder tile) {
            return tile.oredictPattern;
        }
    }, new ITileDataConsumer<Boolean, TileProcessingPatternEncoder>() {
        @Override
        public void setValue(TileProcessingPatternEncoder tile, Boolean value) {
            tile.oredictPattern = value;

            tile.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiProcessingPatternEncoder) {
            ((GuiProcessingPatternEncoder) Minecraft.getMinecraft().currentScreen).updateOredictPattern(parameter.getValue());
        }
    });

    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RSItems.PATTERN));
    private ItemHandlerBasic configuration = new ItemHandlerBasic(9 * 2, this);

    private boolean oredictPattern;

    public TileProcessingPatternEncoder() {
        dataManager.addWatchedParameter(OREDICT_PATTERN);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(patterns, 0, tag);
        RSUtils.writeItems(configuration, 1, tag);

        tag.setBoolean(NBT_OREDICT_PATTERN, oredictPattern);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(patterns, 0, tag);
        RSUtils.readItems(configuration, 1, tag);

        if (tag.hasKey(NBT_OREDICT_PATTERN)) {
            oredictPattern = tag.getBoolean(NBT_OREDICT_PATTERN);
        }
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            ItemStack pattern = new ItemStack(RSItems.PATTERN);

            ItemPattern.setOredict(pattern, oredictPattern);

            for (int i = 0; i < 18; ++i) {
                if (!configuration.getStackInSlot(i).isEmpty()) {
                    if (i >= 9) {
                        ItemPattern.addOutput(pattern, configuration.getStackInSlot(i));
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
            if (!configuration.getStackInSlot(i).isEmpty()) {
                inputsFilled++;
            }
        }

        for (int i = 9; i < 18; ++i) {
            if (!configuration.getStackInSlot(i).isEmpty()) {
                outputsFilled++;
            }
        }

        return inputsFilled > 0 && outputsFilled > 0 && !patterns.getStackInSlot(0).isEmpty() && patterns.getStackInSlot(1).isEmpty();
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
