package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemSecurityCard extends ItemBase {
    private static final String NBT_BOUND = "Bound";
    private static final String NBT_BOUND_NAME = "BoundName";
    private static final String NBT_PERMISSION = "Permission_%d";

    public ItemSecurityCard() {
        super("security_card");

        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            stack.setTagCompound(new NBTTagCompound());

            stack.getTagCompound().setString(NBT_BOUND, player.getGameProfile().getId().toString());
            stack.getTagCompound().setString(NBT_BOUND_NAME, player.getGameProfile().getName());
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    public static UUID getBound(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_BOUND)) {
            return UUID.fromString(stack.getTagCompound().getString(NBT_BOUND));
        }

        return null;
    }

    public static boolean hasPermission(ItemStack stack, Permission permission) {
        String id = String.format(NBT_PERMISSION, permission.getId());

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(id)) {
            return stack.getTagCompound().getBoolean(id);
        }

        return true;
    }

    public static void setPermission(ItemStack stack, Permission permission, boolean state) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean(String.format(NBT_PERMISSION, permission.getId()), state);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_BOUND_NAME)) {
            tooltip.add(I18n.format("item.refinedstorage:security_card.bound", stack.getTagCompound().getString(NBT_BOUND_NAME)));
        }
    }
}
