package refinedstorage.apiimpl.network.grid;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.network.grid.IFluidGridHandler;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;

import javax.annotation.Nullable;

public class FluidGridHandler implements IFluidGridHandler {
    private INetworkMaster network;

    public FluidGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onExtract(int hash, boolean shift, EntityPlayerMP player) {
        FluidStack stack = network.getFluidStorage().get(hash);

        if (stack != null && FluidUtils.hasFluidBucket(stack)) {
            ItemStack bucket = FluidUtils.extractBucket(network);

            if (bucket == null) {
                for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                    ItemStack slot = player.inventory.getStackInSlot(i);

                    if (CompareUtils.compareStackNoQuantity(FluidUtils.EMPTY_BUCKET, slot)) {
                        bucket = FluidUtils.EMPTY_BUCKET.copy();

                        player.inventory.decrStackSize(i, 1);

                        break;
                    }
                }
            }

            if (bucket != null) {
                bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(NetworkUtils.extractFluid(network, stack, Fluid.BUCKET_VOLUME), true);

                if (shift) {
                    if (!player.inventory.addItemStackToInventory(bucket.copy())) {
                        InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), bucket);
                    }
                } else {
                    player.inventory.setItemStack(bucket);
                    player.updateHeldItem();
                }
            }
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(ItemStack container) {
        FluidStack stack = FluidUtils.getFluidFromStack(container, true);

        if (stack != null && network.insertFluid(stack, stack.amount, true) == null) {
            FluidStack drained = FluidUtils.getFluidFromStack(container, false);

            network.insertFluid(drained, drained.amount, false);
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        onInsert(player.inventory.getItemStack());

        player.updateHeldItem();
    }
}
