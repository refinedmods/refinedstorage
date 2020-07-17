package com.refinedmods.refinedstorage.apiimpl.network.grid.handler;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskError;
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
import net.minecraft.util.ResourceLocation;
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

        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        NetworkUtils.extractBucketFromPlayerInventoryOrNetwork(player, network, bucket -> bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fluidHandler -> {
            network.getFluidStorageTracker().changed(player, stack.copy());

            FluidStack extracted = network.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, Action.PERFORM);

            fluidHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

            if (shift) {
                if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                    InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), fluidHandler.getContainer());
                }
            } else {
                player.inventory.setItemStack(fluidHandler.getContainer());
                player.updateHeldItem();
            }

            network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessFluidGrid().getExtractUsage());
        }));
    }

    @Override
    @Nonnull
    public ItemStack onInsert(ServerPlayerEntity player, ItemStack container) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
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
        player.inventory.setItemStack(onInsert(player, player.inventory.getItemStack()));
        player.updateHeldItem();
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            Thread calculationThread = new Thread(() -> {
                ICraftingTask task = network.getCraftingManager().create(stack, quantity);
                if (task == null) {
                    return;
                }

                ICraftingTaskError error = task.calculate();

                ResourceLocation factoryId = task.getPattern().getCraftingTaskFactoryId();

                if (error != null) {
                    RS.NETWORK_HANDLER.sendTo(
                        player,
                        new GridCraftingPreviewResponseMessage(
                            factoryId,
                            Collections.singletonList(new ErrorCraftingPreviewElement(error.getType(), error.getRecursedPattern() == null ? ItemStack.EMPTY : error.getRecursedPattern().getStack())),
                            id,
                            quantity,
                            true
                        )
                    );
                } else if (noPreview && !task.hasMissing()) {
                    network.getCraftingManager().start(task);

                    RS.NETWORK_HANDLER.sendTo(player, new GridCraftingStartResponseMessage());
                } else {
                    RS.NETWORK_HANDLER.sendTo(
                        player,
                        new GridCraftingPreviewResponseMessage(
                            factoryId,
                            task.getPreviewElements(),
                            id,
                            quantity,
                            true
                        )
                    );
                }
            }, "RS crafting preview calculation");

            calculationThread.start();
        }
    }

    @Override
    public void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        FluidStack stack = network.getFluidStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            ICraftingTask task = network.getCraftingManager().create(stack, quantity);
            if (task == null) {
                return;
            }

            ICraftingTaskError error = task.calculate();
            if (error == null && !task.hasMissing()) {
                network.getCraftingManager().start(task);
            }
        }
    }
}
