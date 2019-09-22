package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CoverManager {
    private static final String NBT_DIRECTION = "Direction";
    private static final String NBT_ITEM = "Item";
    private static final String NBT_TYPE = "Type";

    private Map<Direction, Cover> covers = new HashMap<>();
    private NetworkNode node;

    public CoverManager(NetworkNode node) {
        this.node = node;
    }

    public boolean canConduct(Direction direction) {
        Cover cover = getCover(direction);
        if (cover != null && cover.getType() != CoverType.HOLLOW) {
            return false;
        }

        INetworkNode neighbor = API.instance().getNetworkNodeManager((ServerWorld) node.getWorld()).getNode(node.getPos().offset(direction));
        if (neighbor instanceof ICoverable) {
            cover = ((ICoverable) neighbor).getCoverManager().getCover(direction.getOpposite());

            if (cover != null && cover.getType() != CoverType.HOLLOW) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public Cover getCover(Direction facing) {
        return covers.get(facing);
    }

    public boolean hasCover(Direction facing) {
        return covers.containsKey(facing);
    }

    public boolean setCover(Direction facing, @Nullable Cover cover) {
        if (cover == null || (isValidCover(cover.getStack()) && !hasCover(facing))) {
            if (cover != null) {
                if (facing == node.getDirection() && !(node instanceof NetworkNodeCable) && cover.getType() != CoverType.HOLLOW) {
                    return false;
                }
            }

            if (cover == null) {
                covers.remove(facing);
            } else {
                covers.put(facing, cover);
            }

            node.markDirty();

            if (node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().world(), node.getNetwork().getPosition());
            }

            return true;
        }

        return false;
    }

    public void readFromNbt(ListNBT list) {
        covers.clear();

        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);

            if (tag.contains(NBT_DIRECTION) && tag.contains(NBT_ITEM)) {
                Direction direction = Direction.byIndex(tag.getInt(NBT_DIRECTION));
                ItemStack item = ItemStack.read(tag.getCompound(NBT_ITEM));
                int type = tag.contains(NBT_TYPE) ? tag.getInt(NBT_TYPE) : 0;

                if (type >= CoverType.values().length) {
                    type = 0;
                }

                if (isValidCover(item)) {
                    covers.put(direction, new Cover(item, CoverType.values()[type]));
                }
            }
        }
    }

    public ListNBT writeToNbt() {
        ListNBT list = new ListNBT();

        for (Map.Entry<Direction, Cover> entry : covers.entrySet()) {
            CompoundNBT tag = new CompoundNBT();

            tag.putInt(NBT_DIRECTION, entry.getKey().ordinal());
            tag.put(NBT_ITEM, entry.getValue().getStack().serializeNBT());
            tag.putInt(NBT_TYPE, entry.getValue().getType().ordinal());

            list.add(tag);
        }

        return list;
    }

    public IItemHandlerModifiable getAsInventory() {
        ItemStackHandler handler = new ItemStackHandler(covers.size());

        int i = 0;

        for (Map.Entry<Direction, Cover> entry : covers.entrySet()) {
            ItemStack cover = entry.getValue().getType().createStack();

            // TODO ItemCover.setItem(cover, entry.getValue().getStack());

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

        BlockState state = getBlockState(item);

        // TODO: block.isSolid was isTopSolid! correct?
        return block != null && state != null && ((isModelSupported(state) && block.isSolid(state) && !block.ticksRandomly(state) && !block.hasTileEntity(state)) || block instanceof GlassBlock || block instanceof StainedGlassBlock);
    }

    private static boolean isModelSupported(BlockState state) {
        if (state.getRenderType() != BlockRenderType.MODEL) {
            return false;
        }

        return state.isSolid(); // TODO: correct? was isFullCube
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
    public static BlockState getBlockState(@Nullable ItemStack item) {
        Block block = getBlock(item);

        if (block == null) {
            return null;
        }

        try {
            return block.getDefaultState(); // TODO: is still correct?
        } catch (Exception e) {
            return null;
        }
    }
}
