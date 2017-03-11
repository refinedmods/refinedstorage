package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDetector;
import com.raoulvdberge.refinedstorage.gui.GuiDetector;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class TileDetector extends TileNode<NetworkNodeDetector> {
    private static final String NBT_POWERED = "Powered";

    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public static final TileDataParameter<Integer> MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileDetector>() {
        @Override
        public Integer getValue(TileDetector tile) {
            return tile.getNode().getMode();
        }
    }, new ITileDataConsumer<Integer, TileDetector>() {
        @Override
        public void setValue(TileDetector tile, Integer value) {
            if (value == NetworkNodeDetector.MODE_UNDER || value == NetworkNodeDetector.MODE_EQUAL || value == NetworkNodeDetector.MODE_ABOVE || value == NetworkNodeDetector.MODE_AUTOCRAFTING) {
                tile.getNode().setMode(value);
                tile.getNode().markDirty();
            }
        }
    });

    public static final TileDataParameter<Integer> AMOUNT = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileDetector>() {
        @Override
        public Integer getValue(TileDetector tile) {
            return tile.getNode().getAmount();
        }
    }, new ITileDataConsumer<Integer, TileDetector>() {
        @Override
        public void setValue(TileDetector tile, Integer value) {
            tile.getNode().setAmount(value);
            tile.getNode().markDirty();
        }
    }, parameter -> {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;

            if (gui instanceof GuiDetector) {
                ((GuiDetector) gui).AMOUNT.setText(String.valueOf(parameter.getValue()));
            }
        }
    });

    public TileDetector() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        getNode().setPowered(tag.getBoolean(NBT_POWERED));

        super.readUpdate(tag);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_POWERED, getNode().isPowered());

        return tag;
    }

    @Override
    @Nonnull
    public NetworkNodeDetector createNode() {
        return new NetworkNodeDetector(this);
    }
}
