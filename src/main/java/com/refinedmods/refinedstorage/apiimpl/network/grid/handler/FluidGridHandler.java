package com.refinedmods.refinedstorage.apiimpl.network.grid.handler;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartResponseMessage;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.UUID;

public class FluidGridHandler implements IFluidGridHandler {
    private final INetwork network;

    public FluidGridHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void onExtract(ServerPlayerEntity player, UUID id, boolean shift) {
        FluidStack stack = network.getFluidStorageCache().getList().get(id);

        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player) || !network.canRun()) {
            return;
        }

        NetworkUtils.extractBucketFromPlayerInventoryOrNetwork(player, network, bucket -> bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fluidHandler -> {
            network.getFluidStorageTracker().changed(player, stack.copy());

            FluidStack extracted = network.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, Action.PERFORM);

            fluidHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

            if (shift) {
                if (!player.inventory.add(fluidHandler.getContainer().copy())) {
                    InventoryHelper.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), fluidHandler.getContainer());
                }
            } else {
                player.inventory.setCarried(fluidHandler.getContainer());
                player.broadcastCarriedItem();
            }

            network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessFluidGrid().getExtractUsage());
        }));
    }

    @Override
    @Nonnull
    public ItemStack onInsert(ServerPlayerEntity player, ItemStack container) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player) || !network.canRun()) {
            return container;
        }

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (!result.getValue().isEmpty() && network.insertFluid(result.getValue(), result.getValue().getAmount(), Action.SIMULATE).isEmpty()) {
            network.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            network.insertFluid(result.getValue(), result.getValue().getAmount(), Action.PERFORM);

            network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessFluidGrid().getInsertUsage());

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(ServerPlayerEntity player) {
        player.inventory.setCarried(onInsert(player, player.inventory.getCarried()));
        player.broadcastCarriedItem();
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            ICalculationResult result = network.getCraftingManager().create(stack, quantity);
            if (result == null) {
                return;
            }

            if (!result.isOk() && result.getType() != CalculationResultType.MISSING) {
                RS.NETWORK_HANDLER.sendTo(
                    player,
                    new GridCraftingPreviewResponseMessage(
                        Collections.singletonList(new ErrorCraftingPreviewElement(result.getType(), result.getRecursedPattern() == null ? ItemStack.EMPTY : result.getRecursedPattern().getStack())),
                        id,
                        quantity,
                        true
                    )
                );
            } else if (result.isOk() && noPreview) {
                network.getCraftingManager().start(result.getTask());

                RS.NETWORK_HANDLER.sendTo(player, new GridCraftingStartResponseMessage());
            } else {
                RS.NETWORK_HANDLER.sendTo(
                    player,
                    new GridCraftingPreviewResponseMessage(
                        result.getPreviewElements(),
                        id,
                        quantity,
                        true
                    )
                );
            }
        }
    }

    @Override
    public void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            ICalculationResult result = network.getCraftingManager().create(stack, quantity);
            if (result.isOk()) {
                network.getCraftingManager().start(result.getTask());
            }
        }
    }
}
