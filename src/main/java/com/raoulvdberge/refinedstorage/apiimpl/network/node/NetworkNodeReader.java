package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.NodeBlock;
import com.raoulvdberge.refinedstorage.tile.TileReader;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class NetworkNodeReader extends NetworkNode implements IReader, IGuiReaderWriter, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "reader");

    private static final String NBT_CHANNEL = "Channel";
    private static final String NBT_COVERS = "Covers";

    private String channel = "";

    private CoverManager coverManager = new CoverManager(this);

    public NetworkNodeReader(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.readerUsage;
    }

    @Override
    public int getRedstoneStrength() {
        return world.getRedstonePower(pos.offset(getDirection()), getDirection());
    }

    @Override
    public String getTitle() {
        return "gui.refinedstorage:reader";
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public TileDataParameter<String, ?> getChannelParameter() {
        return TileReader.CHANNEL;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileReader.REDSTONE_MODE;
    }

    @Override
    public boolean isActive() {
        return world.getBlockState(pos).get(NodeBlock.CONNECTED);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.contains(NBT_CHANNEL)) {
            channel = tag.getString(NBT_CHANNEL);
        }

        if (tag.contains(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putString(NBT_CHANNEL, channel);

        tag.put(NBT_COVERS, coverManager.writeToNbt());

        return tag;
    }

    @Override
    public boolean canConduct(@Nullable Direction direction) {
        return coverManager.canConduct(direction);
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}
