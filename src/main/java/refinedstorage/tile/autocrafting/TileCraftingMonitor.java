package refinedstorage.tile.autocrafting;

import net.minecraft.inventory.Container;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.tile.TileMachine;

public class TileCraftingMonitor extends TileMachine {
    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCraftingMonitor.class;
    }
}
