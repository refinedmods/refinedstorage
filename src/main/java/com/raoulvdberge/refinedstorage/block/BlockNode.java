package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockNode extends BlockBase {
    public static final String NBT_REFINED_STORAGE_DATA = "RefinedStorageData";

    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public BlockNode(String name) {
        super(name);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_REFINED_STORAGE_DATA)) {
                TileEntity tile = world.getTileEntity(pos);

                if (tile instanceof TileNode) {
                    ((TileNode) tile).getNode().readConfiguration(stack.getTagCompound().getCompoundTag(NBT_REFINED_STORAGE_DATA));
                    ((TileNode) tile).getNode().markDirty();
                }
            }

            API.instance().discoverNode(world, pos);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world.provider.getDimension());

        INetworkNode node = manager.getNode(pos);

        dropContents(world, pos);

        removeTile(world, pos, state);

        manager.removeNode(pos, true);

        API.instance().markNetworkNodesDirty(world);

        if (node.getNetwork() != null) {
            node.getNetwork().getNodeGraph().rebuild();
        }
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        BlockStateContainer.Builder builder = super.createBlockStateBuilder();

        if (hasConnectivityState()) {
            builder.add(CONNECTED);
        }

        return builder;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder().build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);

        if (hasConnectivityState()) {
            TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos);

            if (tile instanceof TileNode) {
                return state.withProperty(CONNECTED, ((TileNode) tile).getNode().isActive());
            }
        }

        return state;
    }

    public boolean hasConnectivityState() {
        return false;
    }
}
