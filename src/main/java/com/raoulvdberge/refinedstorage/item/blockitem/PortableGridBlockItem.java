package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.raoulvdberge.refinedstorage.item.WirelessGridItem;
import com.raoulvdberge.refinedstorage.render.Styles;
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
            type == Type.CREATIVE ? RSBlocks.CREATIVE_PORTABLE_GRID : RSBlocks.PORTABLE_GRID,
            new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1),
            type == Type.CREATIVE,
            () -> RS.SERVER_CONFIG.getPortableGrid().getCapacity()
        );

        this.type = type;
        this.setRegistryName(RS.ID, (type == Type.CREATIVE ? "creative_" : "") + "portable_grid");
    }

    public Type getType() {
        return type;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, (ServerPlayerEntity) player, stack, player.inventory.currentItem);
        }

        return ActionResult.newResult(ActionResultType.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("block.refinedstorage.portable_grid.tooltip").setStyle(Styles.GRAY));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getPlayer().isCrouching()) {
            return ActionResultType.FAIL;
        }

        return super.onItemUse(context);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem()) {
            if (WirelessGridItem.getSortingDirection(oldStack) == WirelessGridItem.getSortingDirection(newStack) &&
                WirelessGridItem.getSortingType(oldStack) == WirelessGridItem.getSortingType(newStack) &&
                WirelessGridItem.getSearchBoxMode(oldStack) == WirelessGridItem.getSearchBoxMode(newStack) &&
                WirelessGridItem.getTabSelected(oldStack) == WirelessGridItem.getTabSelected(newStack) &&
                WirelessGridItem.getTabPage(oldStack) == WirelessGridItem.getTabPage(newStack) &&
                WirelessGridItem.getSize(oldStack) == WirelessGridItem.getSize(newStack)) {
                return false;
            }
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}
