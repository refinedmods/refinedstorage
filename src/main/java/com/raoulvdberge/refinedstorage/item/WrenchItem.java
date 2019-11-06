package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;

public class WrenchItem extends Item {
    public WrenchItem() {
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

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(ctx.getWorld().getTileEntity(ctx.getPos())));
        if (network != null && !network.getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())) {
            WorldUtils.sendNoPermissionMessage(ctx.getPlayer());

            return ActionResultType.FAIL;
        }

        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());

        ctx.getWorld().setBlockState(ctx.getPos(), state.rotate(Rotation.CLOCKWISE_90));

        return ActionResultType.SUCCESS;
    }
}