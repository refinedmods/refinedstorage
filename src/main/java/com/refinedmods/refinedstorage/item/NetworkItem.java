package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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

    @Nullable
    public static ResourceKey<Level> getDimension(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryParse(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }
            return ResourceKey.create(Registries.DIMENSION, name);
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

    public static boolean isValid(ItemStack stack) {
        return stack.hasTag()
            && stack.getTag().contains(NBT_NODE_X)
            && stack.getTag().contains(NBT_NODE_Y)
            && stack.getTag().contains(NBT_NODE_Z)
            && stack.getTag().contains(NBT_DIMENSION);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            applyNetwork(level.getServer(), stack, n -> n.getNetworkItemManager().open(player, player.getItemInHand(hand), PlayerSlot.getSlotForHand(player, hand)), player::sendSystemMessage);
        }

        return InteractionResultHolder.success(stack);
    }

    public void applyNetwork(MinecraftServer server, ItemStack stack, Consumer<INetwork> onNetwork, Consumer<Component> onError) {
        MutableComponent notFound = Component.translatable("misc.refinedstorage.network_item.not_found");

        if (!isValid(stack)) {
            onError.accept(notFound);
            return;
        }

        ResourceKey<Level> dimension = getDimension(stack);
        if (dimension == null) {
            onError.accept(notFound);
            return;
        }

        Level nodeLevel = server.getLevel(dimension);
        if (nodeLevel == null) {
            onError.accept(notFound);
            return;
        }

        BlockPos pos = new BlockPos(getX(stack), getY(stack), getZ(stack));
        if (!nodeLevel.isLoaded(pos)) {
            onError.accept(notFound);
            return;
        }

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(nodeLevel.getBlockEntity(pos)));
        if (network == null) {
            onError.accept(notFound);
            return;
        }

        onNetwork.accept(network);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (isValid(stack)) {
            tooltip.add(Component.translatable("misc.refinedstorage.network_item.tooltip", getX(stack), getY(stack), getZ(stack)).setStyle(Styles.GRAY));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getPlayer().getItemInHand(ctx.getHand());

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(ctx.getLevel().getBlockEntity(ctx.getClickedPos())));
        if (network != null) {
            CompoundTag tag = stack.getTag();

            if (tag == null) {
                tag = new CompoundTag();
            }

            tag.putInt(NBT_NODE_X, network.getPosition().getX());
            tag.putInt(NBT_NODE_Y, network.getPosition().getY());
            tag.putInt(NBT_NODE_Z, network.getPosition().getZ());
            tag.putString(NBT_DIMENSION, ctx.getLevel().dimension().location().toString());

            stack.setTag(tag);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
