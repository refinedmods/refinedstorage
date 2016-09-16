package refinedstorage.integration.forgeenergy;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import refinedstorage.RefinedStorageItems;

public class WirelessGridEnergyForge implements IEnergyStorage {
    private ItemStack stack;

    public WirelessGridEnergyForge(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return RefinedStorageItems.WIRELESS_GRID.receiveEnergy(stack, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return RefinedStorageItems.WIRELESS_GRID.extractEnergy(stack, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return RefinedStorageItems.WIRELESS_GRID.getEnergyStored(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return RefinedStorageItems.WIRELESS_GRID.getMaxEnergyStored(stack);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
