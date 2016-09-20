package refinedstorage.tile.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public enum RedstoneMode {
    IGNORE, HIGH, LOW;

    private static final String NBT = "RedstoneMode";

    public boolean isEnabled(World world, BlockPos pos) {
        switch (this) {
            case IGNORE:
                return true;
            case HIGH:
                return world.isBlockPowered(pos);
            case LOW:
                return !world.isBlockPowered(pos);
            default:
                return false;
        }
    }

    public void write(NBTTagCompound tag) {
        tag.setInteger(NBT, ordinal());
    }

    public static RedstoneMode read(NBTTagCompound tag) {
        if (tag.hasKey(RedstoneMode.NBT)) {
            return getById(tag.getInteger(NBT));
        }

        return IGNORE;
    }

    public static RedstoneMode getById(int id) {
        return id < 0 || id >= values().length ? IGNORE : values()[id];
    }

    public static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IRedstoneConfigurable) tile).getRedstoneMode().ordinal();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IRedstoneConfigurable) tile).setRedstoneMode(RedstoneMode.getById(value));
            }
        });
    }
}
