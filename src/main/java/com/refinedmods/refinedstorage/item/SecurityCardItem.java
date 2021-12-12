package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SecurityCardItem extends Item {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_OWNER_NAME = "OwnerName";
    private static final String NBT_PERMISSION = "Permission_%d";

    public SecurityCardItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP).stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            stack.setTag(new CompoundNBT());

            stack.getTag().putString(NBT_OWNER, player.getGameProfile().getId().toString());
            stack.getTag().putString(NBT_OWNER_NAME, player.getGameProfile().getName());
        }

        return ActionResult.success(stack);
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
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putBoolean(String.format(NBT_PERMISSION, permission.getId()), state);
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_OWNER);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if (stack.hasTag() && stack.getTag().contains(NBT_OWNER_NAME)) {
            tooltip.add(new TranslationTextComponent("item.refinedstorage.security_card.owner", stack.getTag().getString(NBT_OWNER_NAME)).setStyle(Styles.GRAY));
        }

        for (Permission permission : Permission.values()) {
            if (hasPermission(stack, permission)) {
                tooltip.add(new StringTextComponent("- ").append(new TranslationTextComponent("gui.refinedstorage.security_manager.permission." + permission.getId())).setStyle(Styles.GRAY));
            }
        }
    }
}
