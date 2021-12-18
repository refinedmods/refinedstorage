package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SecurityCardItem extends Item {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_OWNER_NAME = "OwnerName";
    private static final String NBT_PERMISSION = "Permission_%d";

    public SecurityCardItem() {
        super(new Item.Properties().tab(RS.CREATIVE_TAB).stacksTo(1));
    }

    @Nullable
    public static UUID getOwner(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_OWNER)) {
            return UUID.fromString(stack.getTag().getString(NBT_OWNER));
        }

        return null;
    }

    public static boolean hasPermission(ItemStack stack, Permission permission) {
        String id = String.format(NBT_PERMISSION, permission.getId());

        if (stack.hasTag() && stack.getTag().contains(id)) {
            return stack.getTag().getBoolean(id);
        }

        return false;
    }

    public static void setPermission(ItemStack stack, Permission permission, boolean state) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putBoolean(String.format(NBT_PERMISSION, permission.getId()), state);
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_OWNER);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            stack.setTag(new CompoundTag());

            stack.getTag().putString(NBT_OWNER, player.getGameProfile().getId().toString());
            stack.getTag().putString(NBT_OWNER_NAME, player.getGameProfile().getName());
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (stack.hasTag() && stack.getTag().contains(NBT_OWNER_NAME)) {
            tooltip.add(new TranslatableComponent("item.refinedstorage.security_card.owner", stack.getTag().getString(NBT_OWNER_NAME)).setStyle(Styles.GRAY));
        }

        for (Permission permission : Permission.values()) {
            if (hasPermission(stack, permission)) {
                tooltip.add(new TextComponent("- ").append(new TranslatableComponent("gui.refinedstorage.security_manager.permission." + permission.getId())).setStyle(Styles.GRAY));
            }
        }
    }
}
