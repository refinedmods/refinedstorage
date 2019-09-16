package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class NetworkItem extends EnergyItem implements INetworkItemProvider {
    private static final String NBT_CONTROLLER_X = "ControllerX";
    private static final String NBT_CONTROLLER_Y = "ControllerY";
    private static final String NBT_CONTROLLER_Z = "ControllerZ";
    private static final String NBT_DIMENSION = "Dimension";

    public NetworkItem(Item.Properties item, boolean creative, int energyCapacity) {
        super(item, creative, energyCapacity);

        addPropertyOverride(new ResourceLocation("connected"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            applyNetwork(world.getServer(), stack, n -> n.getNetworkItemHandler().open(player, player.getHeldItem(hand)), player::sendMessage);
        }

        return ActionResult.newResult(ActionResultType.SUCCESS, stack);
    }

    private void applyNetwork(MinecraftServer server, ItemStack stack, Consumer<INetwork> onNetwork, Consumer<ITextComponent> onError) {
        if (!isValid(stack)) {
            onError.accept(new TranslationTextComponent("misc.refinedstorage:network_item.not_found"));
        } else {
            World networkWorld = DimensionManager.getWorld(server, getDimension(stack), true, true);

            TileEntity network;

            if (networkWorld != null && ((network = networkWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)))) instanceof INetwork)) {
                onNetwork.accept((INetwork) network);
            } else {
                onError.accept(new TranslationTextComponent("misc.refinedstorage:network_item.not_found"));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack)) {
            tooltip.add(new TranslationTextComponent("misc.refinedstorage:network_item.tooltip", getX(stack), getY(stack), getZ(stack)).setStyle(new Style().setColor(TextFormatting.GRAY)));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getPlayer().getHeldItem(ctx.getHand());

        Block block = ctx.getWorld().getBlockState(ctx.getPos()).getBlock();

        if (block == RSBlocks.CONTROLLER) {
            CompoundNBT tag = stack.getTag();

            if (tag == null) {
                tag = new CompoundNBT();
            }

            tag.putInt(NBT_CONTROLLER_X, ctx.getPos().getX());
            tag.putInt(NBT_CONTROLLER_Y, ctx.getPos().getY());
            tag.putInt(NBT_CONTROLLER_Z, ctx.getPos().getZ());
            tag.putString(NBT_DIMENSION, DimensionType.getKey(ctx.getWorld().getDimension().getType()).toString());

            stack.setTag(tag);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Nullable
    public static DimensionType getDimension(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryCreate(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }

            return DimensionType.byName(name);
        }

        return null;
    }

    public static int getX(ItemStack stack) {
        return stack.getTag().getInt(NBT_CONTROLLER_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTag().getInt(NBT_CONTROLLER_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTag().getInt(NBT_CONTROLLER_Z);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public boolean isValid(ItemStack stack) {
        return stack.hasTag()
            && stack.getTag().contains(NBT_CONTROLLER_X)
            && stack.getTag().contains(NBT_CONTROLLER_Y)
            && stack.getTag().contains(NBT_CONTROLLER_Z)
            && stack.getTag().contains(NBT_DIMENSION);
    }
}
