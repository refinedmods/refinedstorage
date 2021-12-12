package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.block.CableBlock;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CoverItem extends Item {

    private static final String NBT_ITEM = "Item";

    public static final ItemStack HIDDEN_COVER_ALTERNATIVE = new ItemStack(Blocks.STONE_BRICKS);


    public CoverItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP));
    }

    public static void setItem(ItemStack cover, ItemStack item) {
        if (!cover.hasTag()) {
            cover.setTag(new CompoundNBT());
        }
        ItemStack result = item.copy();
        result.setCount(1);
        cover.getTag().put(NBT_ITEM, result.serializeNBT());
    }

    @Nonnull
    public static ItemStack getItem(ItemStack cover) {
        if (!cover.hasTag() || !cover.getTag().contains(NBT_ITEM)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(cover.getTag().getCompound(NBT_ITEM));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ItemStack item = getItem(stack);

        if (!item.isEmpty()) {
            tooltip.add(((TextComponent) item.getItem().getName(item)).withStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) { //Changed from 1.12: to use 1.16 configs
            if (!RS.CLIENT_CONFIG.getCover().showAllRecipesInJEI()) {
                ItemStack stack = new ItemStack(this);

                setItem(stack, HIDDEN_COVER_ALTERNATIVE);

                items.add(stack);

                return;
            }
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                Item item = Item.byBlock(block);

                if (item == Items.AIR) {
                    continue;
                }

                NonNullList<ItemStack> subBlocks = NonNullList.create();

                block.fillItemCategory(ItemGroup.TAB_SEARCH, subBlocks);

                for (ItemStack subBlock : subBlocks) {
                    if (CoverManager.isValidCover(subBlock)) {
                        ItemStack stack = new ItemStack(this);

                        setItem(stack, subBlock);

                        items.add(stack);
                    }
                }
            }
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        World world = context.getLevel();

        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        TileEntity tile = world.getBlockEntity(pos);

        // Support placing on the bottom side without too much hassle.
        if (!canPlaceOn(world, pos, facing)) {
            pos = pos.above();

            facing = Direction.DOWN;

            tile = world.getBlockEntity(pos);
        }

        if (canPlaceOn(world, pos, facing)) {
            if (world.isClientSide) {
                ModelDataManager.requestModelDataRefresh(tile);
                return ActionResultType.SUCCESS;
            }

            INetworkNode node = ((NetworkNodeTile<?>) tile).getNode();

            if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, context.getPlayer())) {
                WorldUtils.sendNoPermissionMessage(context.getPlayer());

                return ActionResultType.FAIL;
            }

            if (((ICoverable) node).getCoverManager().setCover(facing, createCover(getItem(stack)))) {
                context.getPlayer().getItemInHand(context.getHand()).shrink(1);

                WorldUtils.updateBlock(world, pos);
                API.instance().getNetworkNodeManager((ServerWorld) world).markForSaving();
                return ActionResultType.SUCCESS;
            }

            return ActionResultType.FAIL;
        }

        return ActionResultType.PASS;
    }


    private boolean canPlaceOn(World world, BlockPos pos, Direction facing) {
        return world.getBlockEntity(pos) instanceof NetworkNodeTile && ((NetworkNodeTile<?>) world.getBlockEntity(pos)).getNode() instanceof ICoverable && !CableBlock.hasVisualConnectionOnSide(world.getBlockState(pos), facing);
    }

    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.NORMAL);
    }

}
