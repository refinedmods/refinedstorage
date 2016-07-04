package refinedstorage.tile.controller;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.util.EnumFacing;

import static refinedstorage.RefinedStorageUtils.convertIC2ToRF;
import static refinedstorage.RefinedStorageUtils.convertRFToIC2;

public class IC2Energy {
    private BasicSink sink;

    public IC2Energy(final TileController controller) {
        this.sink = new BasicSink(controller, (int) convertRFToIC2(controller.getEnergy().getMaxEnergyStored()), Integer.MAX_VALUE) {
            @Override
            public double getDemandedEnergy() {
                return Math.max(0.0D, convertRFToIC2(controller.getEnergy().getMaxEnergyStored()) - convertRFToIC2(controller.getEnergy().getEnergyStored()));
            }

            @Override
            public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
                controller.getEnergy().setEnergyStored(controller.getEnergy().getEnergyStored() + convertIC2ToRF(amount));

                return 0.0D;
            }
        };
    }

    public void invalidate() {
        sink.invalidate();
    }

    public void update() {
        sink.update();
    }
}
