package com.refinedmods.refinedstorage.apiimpl.network.grid.handler;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PortableFluidGridHandler implements IFluidGridHandler {
    private final IPortableGrid portableGrid;

    public PortableFluidGridHandler(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void onExtract(ServerPlayer player, UUID id, boolean shift) {
        if (!portableGrid.isGridActive()) {
            return;
        }

        FluidStack stack = portableGrid.getFluidCache().getList().get(id);

        if (stack == null || stack.getAmount() < FluidType.BUCKET_VOLUME) {
            return;
        }

        ItemStack bucket = ItemStack.EMPTY;

        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack slot = player.getInventory().getItem(i);

            if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                bucket = StackUtils.EMPTY_BUCKET.copy();

                player.getInventory().removeItem(i, 1);

                break;
            }
        }

        if (!bucket.isEmpty()) {
            bucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).ifPresent(fluidHandler -> {
                portableGrid.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(portableGrid.getFluidStorage().extract(stack, FluidType.BUCKET_VOLUME, IComparer.COMPARE_NBT, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE);

                if (shift) {
                    if (!player.getInventory().add(fluidHandler.getContainer().copy())) {
                        Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), fluidHandler.getContainer());
                    }
                } else {
                    player.containerMenu.setCarried(fluidHandler.getContainer());
                }

                portableGrid.drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getExtractUsage());
            });
        }
    }

    @Override
    @Nonnull
    public ItemStack onInsert(ServerPlayer player, ItemStack container) {
        if (!portableGrid.isGridActive()) {
            return container;
        }

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (!result.getValue().isEmpty() && portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.SIMULATE).isEmpty()) {
            portableGrid.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.PERFORM);

            portableGrid.drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getInsertUsage());

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(ServerPlayer player) {
        player.containerMenu.setCarried(onInsert(player, player.containerMenu.getCarried()));
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayer player, UUID id, int quantity, boolean noPreview) {
        // NO OP
    }

    @Override
    public void onCraftingRequested(ServerPlayer player, UUID id, int quantity) {
        // NO OP
    }
}
