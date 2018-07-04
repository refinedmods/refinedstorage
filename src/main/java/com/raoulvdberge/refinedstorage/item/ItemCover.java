package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemCover extends ItemBase {
    private static final String NBT_ITEM = "Item";

    public ItemCover() {
        super("cover");

        setCreativeTab(RS.INSTANCE.coversTab);
    }

    public static void setItem(ItemStack cover, ItemStack item) {
        if (!cover.hasTagCompound()) {
            cover.setTagCompound(new NBTTagCompound());
        }

        cover.getTagCompound().setTag(NBT_ITEM, item.serializeNBT());
    }

    @Nonnull
    public static ItemStack getItem(ItemStack cover) {
        if (!cover.hasTagCompound() || !cover.getTagCompound().hasKey(NBT_ITEM)) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(cover.getTagCompound().getCompoundTag(NBT_ITEM));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        ItemStack item = getItem(stack);

        if (!item.isEmpty()) {
            tooltip.add(item.getItem().getItemStackDisplayName(item));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (Block block : Block.REGISTRY) {
            Item item = Item.getItemFromBlock(block);

            if (item == Items.AIR) {
                continue;
            }

            NonNullList<ItemStack> subBlocks = NonNullList.create();

            block.getSubBlocks(CreativeTabs.SEARCH, subBlocks);

            for (ItemStack subBlock : subBlocks) {
                if (CoverManager.isValidCover(subBlock)) {
                    ItemStack stack = new ItemStack(RSItems.COVER);

                    setItem(stack, subBlock);

                    items.add(stack);
                }
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        TileEntity tile = world.getTileEntity(pos);

        // Support placing on the bottom side without too much hassle.
        if (!canPlaceOn(tile)) {
            pos = pos.up();

            facing = EnumFacing.DOWN;

            tile = world.getTileEntity(pos);
        }

        if (canPlaceOn(tile)) {
            if (world.isRemote) {
                return EnumActionResult.SUCCESS;
            }

            INetworkNode node = ((TileNode) tile).getNode();

            if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                return EnumActionResult.FAIL;
            }

            if (((ICoverable) node).getCoverManager().setCover(facing, getItem(stack))) {
                player.getHeldItem(hand).shrink(1);

                WorldUtils.updateBlock(world, pos);

                return EnumActionResult.SUCCESS;
            }

            return EnumActionResult.FAIL;
        }

        return EnumActionResult.PASS;
    }

    private boolean canPlaceOn(TileEntity tile) {
        return tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable;
    }
}
