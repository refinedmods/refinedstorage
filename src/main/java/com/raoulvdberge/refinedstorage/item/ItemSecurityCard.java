package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemSecurityCard extends Item {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_OWNER_NAME = "OwnerName";
    private static final String NBT_PERMISSION = "Permission_%d";

    public ItemSecurityCard() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));

        this.setRegistryName(RS.ID, "security_card");

        addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            stack.setTag(new CompoundNBT());

            stack.getTag().putString(NBT_OWNER, player.getGameProfile().getId().toString());
            stack.getTag().putString(NBT_OWNER_NAME, player.getGameProfile().getName());
        }

        return ActionResult.newResult(ActionResultType.SUCCESS, stack);
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (stack.hasTag() && stack.getTag().contains(NBT_OWNER_NAME)) {
            tooltip.add(new TranslationTextComponent("item.refinedstorage.security_card.owner", stack.getTag().getString(NBT_OWNER_NAME)).setStyle(new Style().setColor(TextFormatting.GRAY)));
        }

        for (Permission permission : Permission.values()) {
            if (hasPermission(stack, permission)) {
                tooltip.add(new StringTextComponent("- ").appendSibling(new TranslationTextComponent("gui.refinedstorage.security_manager.permission." + permission.getId())).setStyle(new Style().setColor(TextFormatting.GRAY)));
            }
        }
    }
}
