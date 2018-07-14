package com.raoulvdberge.refinedstorage.integration.funkylocomotion;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.rwtema.funkylocomotion.api.IMoveFactory;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoveFactoryNetworkNode implements IMoveFactory {
    private static final String NBT_DIRECTION = "Direction";
    private static final String NBT_NODE = "Node";
    private static final String NBT_NODE_ID = "NodeID";
    private static final String NBT_BLOCK = "Block";
    private static final String NBT_META = "Meta";
    private static final String NBT_TILE = "Tile";

    @Override
    public NBTTagCompound destroyBlock(World world, BlockPos pos) {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(pos);

        TileNode tile = (TileNode) world.getTileEntity(pos);

        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger(NBT_DIRECTION, tile.getDirection().ordinal());
        tag.setTag(NBT_NODE, node.write(new NBTTagCompound()));
        tag.setString(NBT_NODE_ID, node.getId());

        // Funky Locomotion requires this
        IBlockState state = world.getBlockState(pos);
        tag.setString(NBT_BLOCK, Block.REGISTRY.getNameForObject(state.getBlock()).toString());
        tag.setInteger(NBT_META, state.getBlock().getMetaFromState(state));
        tag.setTag(NBT_TILE, tile.writeToNBT(new NBTTagCompound()));

        manager.removeNode(pos); // Avoid inventory dropping
        manager.markForSaving();

        return tag;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean recreateBlock(World world, BlockPos pos, NBTTagCompound tag) {
        NetworkNode node = (NetworkNode) API.instance().getNetworkNodeRegistry().get(tag.getString(NBT_NODE_ID)).create(tag.getCompoundTag(NBT_NODE), world, pos);
        node.setThrottlingDisabled();

        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        manager.setNode(pos, node);
        manager.markForSaving();

        Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString(NBT_BLOCK)));
        world.setBlockState(pos, block.getStateFromMeta(tag.getInteger(NBT_META)));

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileNode) {
            ((TileNode) tile).setDirection(EnumFacing.byIndex(tag.getInteger(NBT_DIRECTION)));

            tile.markDirty();
        }

        return true;
    }
}