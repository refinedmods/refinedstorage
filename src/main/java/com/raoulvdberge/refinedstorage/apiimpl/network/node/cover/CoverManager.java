package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CoverManager {
    private static final String NBT_DIRECTION = "Direction";
    private static final String NBT_ITEM = "Item";

    private Map<EnumFacing, ItemStack> covers = new HashMap<>();
    private NetworkNode node;
    private boolean canPlaceCoversOnFace = true;

    public CoverManager(NetworkNode node) {
        this.node = node;
    }

    public boolean canConduct(EnumFacing direction) {
        if (hasCover(direction)) {
            return false;
        }

        INetworkNode neighbor = API.instance().getNetworkNodeManager(node.getWorld()).getNode(node.getPos().offset(direction));
        if (neighbor instanceof ICoverable && ((ICoverable) neighbor).getCoverManager().hasCover(direction.getOpposite())) {
            return false;
        }

        return true;
    }

    @Nullable
    public ItemStack getCover(EnumFacing facing) {
        return covers.get(facing);
    }

    public boolean hasCover(EnumFacing facing) {
        return covers.containsKey(facing);
    }

    public boolean setCover(EnumFacing facing, ItemStack stack) {
        if (isValidCover(stack) && !hasCover(facing)) {
            if (facing == node.getDirection() && !canPlaceCoversOnFace) {
                return false;
            }

            covers.put(facing, stack);

            node.markDirty();

            if (node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().rebuild();
            }

            return true;
        }

        return false;
    }

    public CoverManager setCanPlaceCoversOnFace(boolean canPlaceCoversOnFace) {
        this.canPlaceCoversOnFace = canPlaceCoversOnFace;

        return this;
    }

    public void readFromNbt(NBTTagList list) {
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            if (tag.hasKey(NBT_DIRECTION) && tag.hasKey(NBT_ITEM)) {
                EnumFacing direction = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));
                ItemStack item = new ItemStack(tag.getCompoundTag(NBT_ITEM));

                if (isValidCover(item)) {
                    covers.put(direction, item);
                }
            }
        }
    }

    public NBTTagList writeToNbt() {
        NBTTagList list = new NBTTagList();

        for (Map.Entry<EnumFacing, ItemStack> entry : covers.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setInteger(NBT_DIRECTION, entry.getKey().ordinal());
            tag.setTag(NBT_ITEM, entry.getValue().serializeNBT());

            list.appendTag(tag);
        }

        return list;
    }

    public IItemHandlerModifiable getAsInventory() {
        ItemStackHandler handler = new ItemStackHandler(covers.size());

        int i = 0;

        for (Map.Entry<EnumFacing, ItemStack> entry : covers.entrySet()) {
            ItemStack cover = new ItemStack(RSItems.COVER);

            ItemCover.setItem(cover, entry.getValue());

            handler.setStackInSlot(i++, cover);
        }

        return handler;
    }

    @SuppressWarnings("deprecation")
    public static boolean isValidCover(ItemStack item) {
        if (item.isEmpty()) {
            return false;
        }

        Block block = getBlock(item);

        IBlockState state = getBlockState(item);

        return block != null && state != null && ((isModelSupported(state) && block.isTopSolid(state) && !block.getTickRandomly() && !block.hasTileEntity(state)) || block instanceof BlockGlass || block instanceof BlockStainedGlass);
    }

    private static boolean isModelSupported(IBlockState state) {
        if (state.getRenderType() != EnumBlockRenderType.MODEL || state instanceof IExtendedBlockState) {
            return false;
        }

        return state.isFullCube();
    }

    @Nullable
    public static Block getBlock(@Nullable ItemStack item) {
        if (item == null) {
            return null;
        }

        Block block = Block.getBlockFromItem(item.getItem());

        if (block == Blocks.AIR) {
            return null;
        }

        return block;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static IBlockState getBlockState(@Nullable ItemStack item) {
        Block block = getBlock(item);

        if (block == null) {
            return null;
        }

        return block.getStateFromMeta(item.getItem().getMetadata(item));
    }
}
