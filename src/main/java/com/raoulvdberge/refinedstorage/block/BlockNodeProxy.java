package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockNodeProxy extends BlockBase {
    public BlockNodeProxy(IBlockInfo info) {
        super(info);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null)) {
            INetworkNode node = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null).getNode();

            if (node.getNetwork() != null) {
                return entity instanceof EntityPlayer && node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, (EntityPlayer) entity);
            }
        }

        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!world.isRemote && getDirection() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            EnumFacing newDirection = getDirection().cycle(tile.getDirection());

            if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable && ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().hasCover(newDirection)) {
                return false;
            }
        }

        return super.rotateBlock(world, pos, axis);
    }

    protected boolean openNetworkGui(int guiId, EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
        return openNetworkGui(guiId, player, world, pos, facing, Permission.MODIFY);
    }

    protected boolean openNetworkGui(int guiId, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, Permission... permissions) {
        return openNetworkGui(player, world, pos, facing, () -> player.openGui(info.getModObject(), guiId, world, pos.getX(), pos.getY(), pos.getZ()), permissions);
    }

    protected boolean openNetworkGui(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, Runnable action) {
        return openNetworkGui(player, world, pos, facing, action, Permission.MODIFY);
    }

    protected boolean openNetworkGui(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, Runnable action, Permission... permissions) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing)) {
            INetworkNodeProxy nodeProxy = CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing));
            INetworkNode node = nodeProxy.getNode();

            if (node.getNetwork() != null) {
                for (Permission permission : permissions) {
                    if (!node.getNetwork().getSecurityManager().hasPermission(permission, player)) {
                        WorldUtils.sendNoPermissionMessage(player);

                        return true; // Avoid placing blocks
                    }
                }
            }
        }

        action.run();

        return true;
    }
}
