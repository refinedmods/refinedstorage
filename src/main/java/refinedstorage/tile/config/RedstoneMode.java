package refinedstorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public enum RedstoneMode {
    IGNORE, HIGH, LOW;

    public static final String NBT = "RedstoneMode";

    public boolean isEnabled(World world, BlockPos pos) {
        switch (this) {
            case IGNORE:
                return true;
            case HIGH:
                return world.isBlockPowered(pos);
            case LOW:
                return !world.isBlockPowered(pos);
        }

        return false;
    }

    public static RedstoneMode getById(int id) {
        return id < 0 || id >= values().length ? IGNORE : values()[id];
    }

    public static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, T>() {
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
