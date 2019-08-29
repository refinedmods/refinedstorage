package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemSecurityCard extends ItemBase {
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_OWNER_NAME = "OwnerName";
    private static final String NBT_PERMISSION = "Permission_%d";

    public ItemSecurityCard() {
        super(new ItemInfo(RS.ID, "security_card"));

        setMaxStackSize(1);

        addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            stack.setTagCompound(new CompoundNBT());

            stack.getTagCompound().setString(NBT_OWNER, player.getGameProfile().getId().toString());
            stack.getTagCompound().setString(NBT_OWNER_NAME, player.getGameProfile().getName());
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    public static UUID getOwner(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER)) {
            return UUID.fromString(stack.getTagCompound().getString(NBT_OWNER));
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
            stack.setTagCompound(new CompoundNBT());
        }

        stack.getTagCompound().putBoolean(String.format(NBT_PERMISSION, permission.getId()), state);
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER_NAME)) {
            tooltip.add(I18n.format("item.refinedstorage:security_card.owner", stack.getTagCompound().getString(NBT_OWNER_NAME)));
        }

        for (Permission permission : Permission.values()) {
            if (hasPermission(stack, permission)) {
                tooltip.add("- " + I18n.format("gui.refinedstorage:security_manager.permission." + permission.getId()));
            }
        }
    }
}
