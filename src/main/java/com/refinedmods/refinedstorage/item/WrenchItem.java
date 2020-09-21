package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;

public class WrenchItem extends Item {
    public WrenchItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
        if (ctx.getWorld().isRemote) {
            return ActionResultType.CONSUME;
        }

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(ctx.getWorld().getTileEntity(ctx.getPos())));
        if (network != null && !network.getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())) {
            WorldUtils.sendNoPermissionMessage(ctx.getPlayer());

            return ActionResultType.FAIL;
        }

        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());

        ctx.getWorld().setBlockState(ctx.getPos(), state.rotate(ctx.getWorld(), ctx.getPos(), Rotation.CLOCKWISE_90));

        return ActionResultType.CONSUME;
    }
}
