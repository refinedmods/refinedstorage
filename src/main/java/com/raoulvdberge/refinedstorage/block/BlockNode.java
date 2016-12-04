package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockNode extends BlockBase {
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
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite())) {
                    INetworkNode node = tile.getCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite());

                    if (node.getNetwork() != null) {
                        node.getNetwork().getNodeGraph().rebuild();

                        break;
                    }
                }
            }
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
        if (hasConnectivityState()) {
            return super.getActualState(state, world, pos).withProperty(CONNECTED, ((TileNode) world.getTileEntity(pos)).isActive());
        }

        return super.getActualState(state, world, pos);
    }

    public boolean hasConnectivityState() {
        return false;
    }
}
