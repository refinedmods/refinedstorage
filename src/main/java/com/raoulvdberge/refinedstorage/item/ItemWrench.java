package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;

public class ItemWrench extends Item {
    public ItemWrench() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));

        this.setRegistryName(RS.ID, "wrench");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if (!ctx.getPlayer().isSneaking()) {
            return ActionResultType.FAIL;
        }

        if (ctx.getWorld().isRemote) {
            return ActionResultType.SUCCESS;
        }

        TileEntity tile = ctx.getWorld().getTileEntity(ctx.getPos());

        // TODO - Better INetworkNode check
        if (tile instanceof TileNode &&
            ((TileNode) tile).getNode().getNetwork() != null &&
            !((TileNode) tile).getNode().getNetwork().getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())) {
            WorldUtils.sendNoPermissionMessage(ctx.getPlayer());

            return ActionResultType.FAIL;
        }

        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());

        /* TODO - Covers
        if (block instanceof BlockCable && tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager manager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            @SuppressWarnings("deprecation")
            AdvancedRayTraceResult result = AdvancedRayTracer.rayTrace(
                pos,
                AdvancedRayTracer.getStart(player),
                AdvancedRayTracer.getEnd(player),
                ((BlockCable) block).getCollisions(tile, block.getActualState(state, world, pos))
            );

            if (result != null && result.getGroup().getDirection() != null) {
                EnumFacing facingSelected = result.getGroup().getDirection();

                if (manager.hasCover(facingSelected)) {
                    ItemStack cover = manager.getCover(facingSelected).getType().createStack();

                    ItemCover.setItem(cover, manager.getCover(facingSelected).getStack());

                    manager.setCover(facingSelected, null);

                    WorldUtils.updateBlock(world, pos);

                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), cover);

                    return ActionResultType.SUCCESS;
                }
            }
        }*/

        ctx.getWorld().setBlockState(ctx.getPos(), state.rotate(Rotation.CLOCKWISE_90));

        return ActionResultType.SUCCESS;
    }
}