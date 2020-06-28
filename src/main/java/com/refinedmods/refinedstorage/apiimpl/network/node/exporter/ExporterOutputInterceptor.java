package com.refinedmods.refinedstorage.apiimpl.network.node.exporter;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class ExporterOutputInterceptor implements IOutputInterceptor {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "exporter");

    private final ItemStack interestedItemStack;
    private final FluidStack interestedFluidStack;
    private final DimensionType exporterDimension;
    private final BlockPos exporterPos;

    public ExporterOutputInterceptor(ItemStack interestedItemStack, FluidStack interestedFluidStack, DimensionType exporterDimension, BlockPos exporterPos) {
        this.interestedItemStack = interestedItemStack;
        this.interestedFluidStack = interestedFluidStack;
        this.exporterDimension = exporterDimension;
        this.exporterPos = exporterPos;
    }

    public static ExporterOutputInterceptor forExporter(ItemStack interestedItemStack, FluidStack interestedFluidStack, ExporterNetworkNode exporter) {
        return new ExporterOutputInterceptor(interestedItemStack.copy(), interestedFluidStack.copy(), exporter.getWorld().getDimension().getType(), exporter.getPos());
    }

    @Nullable
    private ExporterNetworkNode getExporter(MinecraftServer server) {
        ServerWorld world = DimensionManager.getWorld(server, exporterDimension, true, true);
        if (world == null) {
            return null;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(exporterPos);
        if (!(node instanceof ExporterNetworkNode)) {
            return null;
        }

        return (ExporterNetworkNode) node;
    }

    @Override
    public ItemStack intercept(MinecraftServer server, ItemStack stack) {
        if (API.instance().getComparer().isEqualNoQuantity(stack, interestedItemStack)) {
            ExporterNetworkNode exporter = getExporter(server);

            IItemHandler handler = exporter.getFacingItemHandler();
            if (handler == null) {
                return stack;
            }

            int toInsertIntoNetwork = 0;
            int toInsertIntoInventory = stack.getCount();

            if (exporter.getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                int needed = exporter.getStackInteractCountForRegulatorUpgrade(handler, interestedItemStack, toInsertIntoInventory);

                if (toInsertIntoInventory > needed) {
                    toInsertIntoNetwork = toInsertIntoInventory - needed;
                    toInsertIntoInventory = needed;
                }
            }

            if (toInsertIntoInventory > 0) {
                ItemStack remainder = ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, toInsertIntoInventory), false);

                toInsertIntoNetwork += remainder.getCount();
            }

            stack = ItemHandlerHelper.copyStackWithSize(stack, toInsertIntoNetwork);
        }

        return stack;
    }

    @Override
    public FluidStack intercept(MinecraftServer server, FluidStack stack) {
        if (API.instance().getComparer().isEqual(stack, interestedFluidStack, IComparer.COMPARE_NBT)) {
            ExporterNetworkNode exporter = getExporter(server);

            IFluidHandler handler = exporter.getFacingFluidHandler();
            if (handler == null) {
                return stack;
            }

            int toInsertIntoNetwork = 0;
            int toInsertIntoInventory = stack.getAmount();

            if (exporter.getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                int needed = exporter.getStackInteractCountForRegulatorUpgrade(handler, interestedFluidStack, toInsertIntoInventory);

                if (toInsertIntoInventory > needed) {
                    toInsertIntoNetwork = toInsertIntoInventory - needed;
                    toInsertIntoInventory = needed;
                }
            }

            if (toInsertIntoInventory > 0) {
                int filled = handler.fill(StackUtils.copy(stack, toInsertIntoInventory), IFluidHandler.FluidAction.EXECUTE);
                int remainder = toInsertIntoInventory - filled;

                toInsertIntoNetwork += remainder;
            }

            stack = StackUtils.copy(stack, toInsertIntoNetwork);
        }

        return stack;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("Item", interestedItemStack.write(new CompoundNBT()));
        tag.put("Fluid", interestedFluidStack.writeToNBT(new CompoundNBT()));
        tag.putString("Dim", exporterDimension.getRegistryName().toString());
        tag.putLong("Pos", exporterPos.toLong());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
