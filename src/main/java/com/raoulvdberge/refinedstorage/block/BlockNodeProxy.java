package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;

public abstract class BlockNodeProxy extends BlockBase {
    public BlockNodeProxy(IBlockInfo info) {
        super(info);
    }

    /* TODO
    @Override
    public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null)) {
            INetworkNode node = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null).getNode();

            if (node.getNetwork() != null) {
                return entity instanceof PlayerEntity && node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, (PlayerEntity) entity);
            }
        }

        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
        if (!world.isRemote && getDirection() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            Direction newDirection = getDirection().cycle(tile.getDirection());

            if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable && ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().hasCover(newDirection)) {
                return false;
            }
        }

        return super.rotateBlock(world, pos, axis);
    }

    protected boolean openNetworkGui(int guiId, PlayerEntity player, World world, BlockPos pos, Direction facing) {
        return openNetworkGui(guiId, player, world, pos, facing, Permission.MODIFY);
    }

    protected boolean openNetworkGui(int guiId, PlayerEntity player, World world, BlockPos pos, Direction facing, Permission... permissions) {
        return openNetworkGui(player, world, pos, facing, () -> player.openGui(info.getModObject(), guiId, world, pos.getX(), pos.getY(), pos.getZ()), permissions);
    }

    protected boolean openNetworkGui(PlayerEntity player, World world, BlockPos pos, Direction facing, Runnable action) {
        return openNetworkGui(player, world, pos, facing, action, Permission.MODIFY);
    }

    protected boolean openNetworkGui(PlayerEntity player, World world, BlockPos pos, Direction facing, Runnable action, Permission... permissions) {
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
    }*/
}
