package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemWrench extends ItemBase {
    private static final String NBT_WRENCHED_TILE = "WrenchedTile";

    public ItemWrench() {
        super("wrench");

        setMaxStackSize(1);
        setMaxDamage(64);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof IWrenchable) {
            IWrenchable wrenchable = (IWrenchable) tile;

            boolean canWrite = false;

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_WRENCHED_TILE)) {
                String wrenchedTile = stack.getTagCompound().getString(NBT_WRENCHED_TILE);

                if (wrenchable.getClass().getName().equals(wrenchedTile)) {
                    wrenchable.readConfiguration(stack.getTagCompound());
                    tile.markDirty();

                    player.addChatComponentMessage(new TextComponentTranslation("item.refinedstorage:wrench.read"));
                } else {
                    canWrite = true;
                }
            } else {
                canWrite = true;
            }

            if (canWrite) {
                NBTTagCompound tag = new NBTTagCompound();

                tag.setString(NBT_WRENCHED_TILE, wrenchable.getClass().getName());

                stack.setTagCompound(wrenchable.writeConfiguration(tag));

                player.addChatComponentMessage(new TextComponentTranslation("item.refinedstorage:wrench.saved"));
            }

            stack.damageItem(1, player);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && !player.isSneaking()) {
            stack.setTagCompound(new NBTTagCompound());

            player.addChatComponentMessage(new TextComponentTranslation("item.refinedstorage:wrench.cleared"));
        }

        return super.onItemRightClick(stack, world, player, hand);
    }
}
