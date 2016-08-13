package refinedstorage.apiimpl.network.grid;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.grid.IFluidGridHandler;

import javax.annotation.Nullable;

public class FluidGridHandler implements IFluidGridHandler {
    private INetworkMaster network;

    public FluidGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onExtract(int hash, boolean shift, EntityPlayerMP player) {
        System.out.println("Extract " + hash + " (shift = " + shift + ")");
    }

    @Nullable
    @Override
    public ItemStack onInsert(ItemStack container) {
        if (container.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

            FluidStack drainPre = handler.drain(Fluid.BUCKET_VOLUME, false);

            if (drainPre != null && network.insertFluid(drainPre, drainPre.amount, true) == null) {
                FluidStack drain = handler.drain(Fluid.BUCKET_VOLUME, true);

                network.insertFluid(drain, drain.amount, false);
            }
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        onInsert(player.inventory.getItemStack());

        player.updateHeldItem();
    }
}
