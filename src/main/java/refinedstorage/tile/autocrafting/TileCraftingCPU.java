package refinedstorage.tile.autocrafting;

import net.minecraft.inventory.Container;
import refinedstorage.tile.TileMachine;

public class TileCraftingCPU extends TileMachine {
    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }
}
