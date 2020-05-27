package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.apiimpl.network.item.WirelessCraftingMonitorNetworkItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class WirelessCraftingMonitorItem extends NetworkItem {
    public static final String NBT_TAB_SELECTED = "TabSelected";
    public static final String NBT_TAB_PAGE = "TabPage";

    public enum Type {
        NORMAL,
        CREATIVE
    }

    private final Type type;

    public WirelessCraftingMonitorItem(Type type) {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), type == Type.CREATIVE, () -> RS.SERVER_CONFIG.getWirelessCraftingMonitor().getCapacity());

        this.setRegistryName(RS.ID, (type == Type.CREATIVE ? "creative_" : "") + "wireless_crafting_monitor");

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Nonnull
    @Override
    public INetworkItem provide(INetworkItemManager handler, PlayerEntity player, ItemStack stack, int slotId) {
        return new WirelessCraftingMonitorNetworkItem(handler, player, stack, slotId);
    }

    public static Optional<UUID> getTabSelected(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().hasUniqueId(NBT_TAB_SELECTED)) {
            return Optional.of(stack.getTag().getUniqueId(NBT_TAB_SELECTED));
        }

        return Optional.empty();
    }

    public static void setTabSelected(ItemStack stack, Optional<UUID> tabSelected) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        if (tabSelected.isPresent()) {
            stack.getTag().putUniqueId(NBT_TAB_SELECTED, tabSelected.get());
        } else {
            stack.getTag().remove(NBT_TAB_SELECTED + "Least");
            stack.getTag().remove(NBT_TAB_SELECTED + "Most");
        }
    }

    public static int getTabPage(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_TAB_PAGE)) {
            return stack.getTag().getInt(NBT_TAB_PAGE);
        }

        return 0;
    }

    public static void setTabPage(ItemStack stack, int tabPage) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putInt(NBT_TAB_PAGE, tabPage);
    }
}
