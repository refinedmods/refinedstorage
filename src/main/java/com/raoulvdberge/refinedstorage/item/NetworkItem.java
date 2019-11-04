package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.render.Styles;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NetworkItem extends EnergyItem implements INetworkItemProvider {
    private static final String NBT_NODE_X = "NodeX";
    private static final String NBT_NODE_Y = "NodeY";
    private static final String NBT_NODE_Z = "NodeZ";
    private static final String NBT_DIMENSION = "Dimension";

    public NetworkItem(Item.Properties item, boolean creative, Supplier<Integer> energyCapacity) {
        super(item, creative, energyCapacity);

        addPropertyOverride(new ResourceLocation("connected"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            applyNetwork(world.getServer(), stack, n -> n.getNetworkItemManager().open(player, player.getHeldItem(hand)), player::sendMessage);
        }

        return ActionResult.newResult(ActionResultType.SUCCESS, stack);
    }

    public void applyNetwork(MinecraftServer server, ItemStack stack, Consumer<INetwork> onNetwork, Consumer<ITextComponent> onError) {
        TranslationTextComponent notFound = new TranslationTextComponent("misc.refinedstorage.network_item.not_found");

        if (!isValid(stack)) {
            onError.accept(notFound);
            return;
        }

        DimensionType dimension = getDimension(stack);
        if (dimension == null) {
            onError.accept(notFound);
            return;
        }

        World nodeWorld = DimensionManager.getWorld(server, dimension, true, true);
        if (nodeWorld == null) {
            onError.accept(notFound);
            return;
        }

        TileEntity node = nodeWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)));
        if (node == null) {
            onError.accept(notFound);
            return;
        }

        INetworkNodeProxy proxy = node.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);
        if (proxy == null) {
            onError.accept(notFound);
            return;
        }

        INetwork network = proxy.getNode().getNetwork();
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

        TileEntity tile = ctx.getWorld().getTileEntity(ctx.getPos());
        if (tile != null) {
            INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);

            if (proxy != null) {
                INetwork network = proxy.getNode().getNetwork();

                if (network != null) {
                    CompoundNBT tag = stack.getTag();

                    if (tag == null) {
                        tag = new CompoundNBT();
                    }

                    tag.putInt(NBT_NODE_X, network.getPosition().getX());
                    tag.putInt(NBT_NODE_Y, network.getPosition().getY());
                    tag.putInt(NBT_NODE_Z, network.getPosition().getZ());
                    tag.putString(NBT_DIMENSION, DimensionType.getKey(ctx.getWorld().getDimension().getType()).toString());

                    stack.setTag(tag);

                    return ActionResultType.SUCCESS;
                }
            }
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

    public boolean isValid(ItemStack stack) {
        return stack.hasTag()
            && stack.getTag().contains(NBT_NODE_X)
            && stack.getTag().contains(NBT_NODE_Y)
            && stack.getTag().contains(NBT_NODE_Z)
            && stack.getTag().contains(NBT_DIMENSION);
    }
}
