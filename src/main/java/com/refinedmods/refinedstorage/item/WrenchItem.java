package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;
import net.minecraftforge.items.ItemHandlerHelper;

public class WrenchItem extends Item {
    public WrenchItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
        if (ctx.getWorld().isRemote) {
            return ActionResultType.CONSUME;
        }

        INetworkNode node = NetworkUtils.getNodeFromTile(ctx.getWorld().getTileEntity(ctx.getPos()));
        INetwork network = NetworkUtils.getNetworkFromNode(node);
        if (network != null && !network.getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())) {
            WorldUtils.sendNoPermissionMessage(ctx.getPlayer());

            return ActionResultType.FAIL;
        }
        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());

        if (node instanceof ICoverable && ((ICoverable) node).getCoverManager().hasCover(ctx.getFace())){
            Cover cover = ((ICoverable) node).getCoverManager().removeCover(ctx.getFace());
            if (cover != null){
                ItemStack stack1 = cover.getType().createStack();
                CoverItem.setItem(stack1, cover.getStack());
                ItemHandlerHelper.giveItemToPlayer(ctx.getPlayer(), stack1);
                ctx.getWorld().notifyBlockUpdate(ctx.getPos(), state, state, 3);
                ctx.getWorld().notifyNeighborsOfStateChange(ctx.getPos(), ctx.getWorld().getBlockState(ctx.getPos()).getBlock());
                return ActionResultType.SUCCESS;
            }
        }

        ctx.getWorld().setBlockState(ctx.getPos(), state.rotate(ctx.getWorld(), ctx.getPos(), Rotation.CLOCKWISE_90));

        return ActionResultType.CONSUME;
    }
}
