package refinedstorage.integration.ic2;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.util.EnumFacing;
import refinedstorage.tile.TileController;

public class ControllerEnergyIC2 implements IControllerEnergyIC2 {
    private BasicSink sink;

    public ControllerEnergyIC2(final TileController controller) {
        this.sink = new BasicSink(controller, (int) IntegrationIC2.toEU(controller.getEnergy().getMaxEnergyStored()), Integer.MAX_VALUE) {
            @Override
            public double getDemandedEnergy() {
                return Math.max(0.0D, IntegrationIC2.toEU(controller.getEnergy().getMaxEnergyStored()) - IntegrationIC2.toEU(controller.getEnergy().getEnergyStored()));
            }

            @Override
            public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
                controller.getEnergy().setEnergyStored(controller.getEnergy().getEnergyStored() + IntegrationIC2.toRS(amount));

                return 0.0D;
            }
        };
    }

    @Override
    public void invalidate() {
        sink.invalidate();
    }

    @Override
    public void update() {
        sink.update();
    }

    @Override
    public void onChunkUnload() {
        sink.onChunkUnload();
    }
}
