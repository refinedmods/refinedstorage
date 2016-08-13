package refinedstorage.apiimpl.network.grid;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
        System.out.println("Extract " + hash);
    }

    @Nullable
    @Override
    public ItemStack onInsert(ItemStack container) {
        System.out.println("Insert " + container);
        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        System.out.println("Insert held!");
    }
}
