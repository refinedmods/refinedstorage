package com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.NetworkItemAction;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementError;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingStartResponse;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collections;

public class FluidGridHandler implements IFluidGridHandler {
    private INetwork network;

    public FluidGridHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void onExtract(EntityPlayerMP player, int hash, boolean shift) {
        FluidStack stack = network.getFluidStorageCache().getList().get(hash);

        if (stack == null || stack.amount < Fluid.BUCKET_VOLUME || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        if (StackUtils.hasFluidBucket(stack)) {
            ItemStack bucket = null;

            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack slot = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                    bucket = StackUtils.EMPTY_BUCKET.copy();

                    player.inventory.decrStackSize(i, 1);

                    break;
                }
            }

            if (bucket == null) {
                bucket = network.extractItem(StackUtils.EMPTY_BUCKET, 1, Action.PERFORM);
            }

            if (bucket != null) {
                IFluidHandlerItem fluidHandler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                network.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(network.extractFluid(stack, Fluid.BUCKET_VOLUME, Action.PERFORM), true);

                if (shift) {
                    if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), fluidHandler.getContainer());
                    }
                } else {
                    player.inventory.setItemStack(fluidHandler.getContainer());
                    player.updateHeldItem();
                }

                INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

                if (networkItem != null) {
                    networkItem.onAction(NetworkItemAction.FLUID_EXTRACTED);
                }
            }
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack container) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return container;
        }

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (result.getValue() != null && network.insertFluid(result.getValue(), result.getValue().amount, Action.SIMULATE) == null) {
            network.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            network.insertFluid(result.getValue(), result.getValue().amount, Action.PERFORM);

            INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

            if (networkItem != null) {
                networkItem.onAction(NetworkItemAction.FLUID_INSERTED);
            }

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        player.inventory.setItemStack(StackUtils.nullToEmpty(onInsert(player, player.inventory.getItemStack())));
        player.updateHeldItem();
    }

    @Override
    public ItemStack onShiftClick(EntityPlayerMP player, ItemStack container) {
        return StackUtils.nullToEmpty(onInsert(player, container));
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        IStackList<FluidStack> cache = API.instance().createFluidStackList();

        for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
            for (FluidStack output : pattern.getFluidOutputs()) {
                cache.add(output);
            }
        }

        FluidStack stack = cache.get(hash);

        if (stack != null) {
            Thread calculationThread = new Thread(() -> {
                ICraftingTask task = network.getCraftingManager().create(stack, quantity);
                if (task == null) {
                    return;
                }

                ICraftingTaskError error = task.calculate();

                if (error != null) {
                    RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(Collections.singletonList(new CraftingPreviewElementError(error.getType(), error.getRecursedPattern() == null ? ItemStack.EMPTY : error.getRecursedPattern().getStack())), hash, quantity, true), player);
                } else if (noPreview && !task.hasMissing()) {
                    network.getCraftingManager().add(task);

                    RS.INSTANCE.network.sendTo(new MessageGridCraftingStartResponse(), player);
                } else {
                    RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(task.getPreviewStacks(), hash, quantity, true), player);
                }
            }, "RS crafting preview calculation");

            calculationThread.start();
        }
    }

    @Override
    public void onCraftingRequested(EntityPlayerMP player, int hash, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        FluidStack stack = null;

        for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
            for (FluidStack output : pattern.getFluidOutputs()) {
                if (API.instance().getFluidStackHashCode(output) == hash) {
                    stack = output;

                    break;
                }
            }

            if (stack != null) {
                break;
            }
        }

        if (stack != null) {
            ICraftingTask task = network.getCraftingManager().create(stack, quantity);
            if (task == null) {
                return;
            }

            ICraftingTaskError error = task.calculate();
            if (error == null && !task.hasMissing()) {
                network.getCraftingManager().add(task);
            }
        }
    }
}
