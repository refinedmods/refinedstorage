package refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;

public class TileFluidInterface extends TileNode {
    private static final String NBT_TANK_IN = "TankIn";
    private static final String NBT_TANK_OUT = "TankOut";

    private FluidTank tankIn = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            markDirty();
        }
    };

    private FluidTank tankOut = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            markDirty();
        }
    };

    private ItemHandlerBasic buckets = new ItemHandlerBasic(2, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    public TileFluidInterface() {
        tankIn.setCanDrain(false);
        tankIn.setCanFill(true);

        tankOut.setCanDrain(true);
        tankOut.setCanFill(false);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(upgrades, 0, tag);
        writeItems(buckets, 1, tag);

        tag.setTag(NBT_TANK_IN, tankIn.writeToNBT(new NBTTagCompound()));
        tag.setTag(NBT_TANK_OUT, tankOut.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(upgrades, 0, tag);
        readItems(buckets, 1, tag);

        if (tag.hasKey(NBT_TANK_IN)) {
            tankIn.readFromNBT(tag.getCompoundTag(NBT_TANK_IN));
        }

        if (tag.hasKey(NBT_TANK_OUT)) {
            tankOut.readFromNBT(tag.getCompoundTag(NBT_TANK_OUT));
        }
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    public ItemHandlerBasic getBuckets() {
        return buckets;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return facing == EnumFacing.DOWN ? (T) tankOut : (T) tankIn;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void updateNode() {

    }

    @Override
    public int getEnergyUsage() {
        return 0; // @TODO: x
    }
}
