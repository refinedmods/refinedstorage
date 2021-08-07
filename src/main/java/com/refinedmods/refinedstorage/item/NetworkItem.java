package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NetworkItem extends EnergyItem implements INetworkItemProvider {
    private static final String NBT_NODE_X = "NodeX";
    private static final String NBT_NODE_Y = "NodeY";
    private static final String NBT_NODE_Z = "NodeZ";
    private static final String NBT_DIMENSION = "Dimension";

    protected NetworkItem(Item.Properties item, boolean creative, Supplier<Integer> energyCapacity) {
        super(item, creative, energyCapacity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            applyNetwork(world.getServer(), stack, n -> n.getNetworkItemManager().open(player, stack, hand == Hand.OFF_HAND ? 40 : player.inventory.currentItem), err -> player.sendMessage(err, player.getUniqueID()));
        }

        return ActionResult.resultSuccess(stack);
    }

    public void applyNetwork(MinecraftServer server, ItemStack stack, Consumer<INetwork> onNetwork, Consumer<ITextComponent> onError) {
        TranslationTextComponent notFound = new TranslationTextComponent("misc.refinedstorage.network_item.not_found");

        if (!isValid(stack)) {
            onError.accept(notFound);
            return;
        }

        RegistryKey<World> dimension = getDimension(stack);
        if (dimension == null) {
            onError.accept(notFound);
            return;
        }

        World nodeWorld = server.getWorld(dimension);
        if (nodeWorld == null) {
            onError.accept(notFound);
            return;
        }

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(nodeWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)))));
        if (network == null) {
            onError.accept(notFound);
            return;
        }

        onNetwork.accept(network);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack)) {
            tooltip.add(new TranslationTextComponent("misc.refinedstorage.network_item.tooltip", getX(stack), getY(stack), getZ(stack)).setStyle(Styles.GRAY));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getPlayer().getHeldItem(ctx.getHand());

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(ctx.getWorld().getTileEntity(ctx.getPos())));
        if (network != null) {
            CompoundNBT tag = stack.getTag();

            if (tag == null) {
                tag = new CompoundNBT();
            }

            tag.putInt(NBT_NODE_X, network.getPosition().getX());
            tag.putInt(NBT_NODE_Y, network.getPosition().getY());
            tag.putInt(NBT_NODE_Z, network.getPosition().getZ());
            tag.putString(NBT_DIMENSION, ctx.getWorld().getDimensionKey().getLocation().toString());

            stack.setTag(tag);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Nullable
    public static RegistryKey<World> getDimension(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryCreate(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }

            return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, name);
        }

        return null;
    }

    public static int getX(ItemStack stack) {
        return stack.getTag().getInt(NBT_NODE_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTag().getInt(NBT_NODE_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTag().getInt(NBT_NODE_Z);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTag()
            && stack.getTag().contains(NBT_NODE_X)
            && stack.getTag().contains(NBT_NODE_Y)
            && stack.getTag().contains(NBT_NODE_Z)
            && stack.getTag().contains(NBT_DIMENSION);
    }
}
