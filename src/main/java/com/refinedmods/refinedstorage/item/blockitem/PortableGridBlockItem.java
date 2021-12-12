package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PortableGridBlockItem extends EnergyBlockItem {
    public enum Type {
        NORMAL,
        CREATIVE
    }

    private final Type type;

    public PortableGridBlockItem(Type type) {
        super(
            type == Type.CREATIVE ? RSBlocks.CREATIVE_PORTABLE_GRID.get() : RSBlocks.PORTABLE_GRID.get(),
            new Item.Properties().tab(RS.MAIN_GROUP).stacksTo(1),
            type == Type.CREATIVE,
            () -> RS.SERVER_CONFIG.getPortableGrid().getCapacity()
        );

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, (ServerPlayerEntity) player, stack, PlayerSlot.getSlotForHand(player, hand));
        }

        return ActionResult.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("block.refinedstorage.portable_grid.tooltip").setStyle(Styles.GRAY));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getPlayer() == null) {
            return ActionResultType.FAIL;
        }

        //Place
        if (context.getPlayer().isCrouching()) {
            return super.useOn(context);
        }

        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        if (!context.getLevel().isClientSide) {
            API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, (ServerPlayerEntity) context.getPlayer(), stack, PlayerSlot.getSlotForHand(context.getPlayer(), context.getHand()));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
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
}
