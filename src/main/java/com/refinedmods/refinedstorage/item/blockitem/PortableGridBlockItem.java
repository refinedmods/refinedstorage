package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PortableGridBlockItem extends EnergyBlockItem {
    private final Type type;

    public PortableGridBlockItem(Type type) {
        super(
            type == Type.CREATIVE ? RSBlocks.CREATIVE_PORTABLE_GRID.get() : RSBlocks.PORTABLE_GRID.get(),
            new Item.Properties().stacksTo(1),
            type == Type.CREATIVE,
            () -> RS.SERVER_CONFIG.getPortableGrid().getCapacity()
        );

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, (ServerPlayer) player, stack, PlayerSlot.getSlotForHand(player, hand));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.translatable("block.refinedstorage.portable_grid.tooltip").setStyle(Styles.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) {
            return InteractionResult.FAIL;
        }

        //Place
        if (context.getPlayer().isCrouching()) {
            return super.useOn(context);
        }

        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        if (!context.getLevel().isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, (ServerPlayer) context.getPlayer(), stack, PlayerSlot.getSlotForHand(context.getPlayer(), context.getHand()));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public int getEntityLifespan(ItemStack stack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem() &&
            WirelessGridItem.getSortingDirection(oldStack) == WirelessGridItem.getSortingDirection(newStack) &&
            WirelessGridItem.getSortingType(oldStack) == WirelessGridItem.getSortingType(newStack) &&
            WirelessGridItem.getSearchBoxMode(oldStack) == WirelessGridItem.getSearchBoxMode(newStack) &&
            WirelessGridItem.getTabSelected(oldStack) == WirelessGridItem.getTabSelected(newStack) &&
            WirelessGridItem.getTabPage(oldStack) == WirelessGridItem.getTabPage(newStack) &&
            WirelessGridItem.getSize(oldStack) == WirelessGridItem.getSize(newStack)) {
            return false;
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    public enum Type {
        NORMAL,
        CREATIVE
    }
}
