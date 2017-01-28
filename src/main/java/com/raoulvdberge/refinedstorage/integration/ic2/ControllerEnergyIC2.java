package com.raoulvdberge.refinedstorage.integration.ic2;

import com.raoulvdberge.refinedstorage.tile.TileController;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.info.Info;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;

public class ControllerEnergyIC2 implements IControllerEnergyIC2 {
    private BasicSink sink;

    public ControllerEnergyIC2(final TileController controller) {
        this.sink = new BasicSink(controller, (int) IntegrationIC2.toEU(controller.getEnergy().getMaxEnergyStored()), 3) {
            @Override
            public double getDemandedEnergy() {
                return Math.max(0.0D, IntegrationIC2.toEU(controller.getEnergy().getMaxEnergyStored()) - IntegrationIC2.toEU(controller.getEnergy().getEnergyStored()));
            }

            @Override
            public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
                controller.getEnergy().setEnergyStored(controller.getEnergy().getEnergyStored() + IntegrationIC2.toRS(amount));

                return 0.0D;
            }

            @Override
            public void onLoaded() {
                if (!this.addedToEnet && !this.parent.getWorld().isRemote && Info.isIc2Available()) {
                    this.world = this.parent.getWorld();
                    this.pos = this.parent.getPos();
                    MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
                    this.addedToEnet = true;
                }
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
