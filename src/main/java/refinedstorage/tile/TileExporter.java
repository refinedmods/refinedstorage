package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerExporter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileExporter extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    private InventorySimple inventory = new InventorySimple("exporter", 9, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private int compare = 0;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler();

    @Override
    public int getEnergyUsage() {
        return 2 + RefinedStorageUtils.getUpgradeEnergyUsage(upgradesInventory);
    }

    @Override
    public void updateMachine() {
        TileEntity connectedTile = worldObj.getTileEntity(pos.offset(getDirection()));

        if (connectedTile instanceof IInventory) {
            IInventory connectedInventory = (IInventory) connectedTile;

            if (ticks % RefinedStorageUtils.getSpeed(upgradesInventory) == 0) {
                for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                    ItemStack slot = inventory.getStackInSlot(i);

                    if (slot != null) {
                        ItemStack toTake = slot.copy();
                        toTake.stackSize = 1;

                        ItemStack took = controller.take(toTake, compare);

                        if (took != null) {
                            scheduler.resetSchedule();

                            ItemStack remaining = TileEntityHopper.putStackInInventoryAllSlots(connectedInventory, took, getDirection().getOpposite());

                            if (remaining != null) {
                                controller.push(remaining);
                            }
                        } else if (RefinedStorageUtils.hasUpgrade(upgradesInventory, ItemUpgrade.TYPE_CRAFTING)) {
                            if (scheduler.canSchedule(compare, slot)) {
                                scheduler.schedule(controller, compare, slot);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        markDirty();

        this.compare = compare;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(upgradesInventory, 1, nbt);

        scheduler.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
        RefinedStorageUtils.saveInventory(upgradesInventory, 1, nbt);

        scheduler.writeToNBT(nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerExporter.class;
    }

    @Override
    public IInventory getDroppedInventory() {
        return upgradesInventory;
    }

    public InventorySimple getUpgradesInventory() {
        return upgradesInventory;
    }

    public IInventory getInventory() {
        return inventory;
    }
}
