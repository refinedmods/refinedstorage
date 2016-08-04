package refinedstorage.integration.ic2;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.util.EnumFacing;
import refinedstorage.tile.TileController;

public class IC2EnergyController implements IIC2EnergyController {
    private BasicSink sink;

    public IC2EnergyController(final TileController controller) {
        this.sink = new BasicSink(controller, (int) IC2Integration.toEU(controller.getEnergy().getMaxEnergyStored()), Integer.MAX_VALUE) {
            @Override
            public double getDemandedEnergy() {
                return Math.max(0.0D, IC2Integration.toEU(controller.getEnergy().getMaxEnergyStored()) - IC2Integration.toEU(controller.getEnergy().getEnergyStored()));
            }

            @Override
            public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
                controller.getEnergy().setEnergyStored(controller.getEnergy().getEnergyStored() + IC2Integration.toRS(amount));

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
