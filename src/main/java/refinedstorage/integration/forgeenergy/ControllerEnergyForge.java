package refinedstorage.integration.forgeenergy;

import net.minecraftforge.energy.IEnergyStorage;
import refinedstorage.tile.TileController;

public class ControllerEnergyForge implements IEnergyStorage {
    private TileController controller;

    public ControllerEnergyForge(TileController controller) {
        this.controller = controller;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return controller.getEnergy().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return controller.getEnergy().extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return controller.getEnergy().getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return controller.getEnergy().getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
