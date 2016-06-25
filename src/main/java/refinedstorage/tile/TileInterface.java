package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerInterface;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileInterface extends TileSlave implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    private BasicItemHandler importItems = new BasicItemHandler(9, this);
    private BasicItemHandler exportSpecimenItems = new BasicItemHandler(9, this);
    private BasicItemHandler exportItems = new BasicItemHandler(9, this);
    private BasicItemHandler upgrades = new BasicItemHandler(
        4,
        this,
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED),
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_STACK)
    );

    private int compare = 0;

    private int currentSlot = 0;

    @Override
    public int getEnergyUsage() {
        return 4 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateSlave() {
        if (currentSlot >= importItems.getSlots()) {
            currentSlot = 0;
        }

        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot == null) {
            currentSlot++;
        } else if (ticks % RefinedStorageUtils.getSpeed(upgrades) == 0) {
            int size = Math.min(slot.stackSize, RefinedStorageUtils.hasUpgrade(upgrades, ItemUpgrade.TYPE_STACK) ? 64 : 1);

            ItemStack remainder = network.push(slot, size, false);

            if (remainder == null) {
                importItems.extractItem(currentSlot, size, false);
            } else {
                importItems.extractItem(currentSlot, size - remainder.stackSize, false);
            }
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack wanted = exportSpecimenItems.getStackInSlot(i);
            ItemStack got = exportItems.getStackInSlot(i);

            if (wanted == null) {
                if (got != null) {
                    exportItems.setStackInSlot(i, network.push(got, got.stackSize, false));
                }
            } else {
                int delta = got == null ? wanted.stackSize : (wanted.stackSize - got.stackSize);

                if (delta > 0) {
                    ItemStack result = network.take(wanted, delta, compare);

                    if (result != null) {
                        if (got == null) {
                            exportItems.setStackInSlot(i, result);
                        } else {
                            exportItems.getStackInSlot(i).stackSize += result.stackSize;
                        }
                    }
                } else if (delta < 0) {
                    ItemStack remainder = network.push(got, Math.abs(delta), false);

                    if (remainder == null) {
                        exportItems.extractItem(i, Math.abs(delta), false);
                    } else {
                        exportItems.extractItem(i, Math.abs(delta) - remainder.stackSize, false);
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
        this.compare = compare;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(importItems, 0, nbt);
        RefinedStorageUtils.readItems(exportSpecimenItems, 1, nbt);
        RefinedStorageUtils.readItems(exportItems, 2, nbt);
        RefinedStorageUtils.readItems(upgrades, 3, nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(importItems, 0, tag);
        RefinedStorageUtils.writeItems(exportSpecimenItems, 1, tag);
        RefinedStorageUtils.writeItems(exportItems, 2, tag);
        RefinedStorageUtils.writeItems(upgrades, 3, tag);

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
    }


    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerInterface.class;
    }

    public IItemHandler getImportItems() {
        return importItems;
    }

    public IItemHandler getExportSpecimenItems() {
        return exportSpecimenItems;
    }

    public IItemHandler getExportItems() {
        return exportItems;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return new CombinedInvWrapper(importItems, exportItems, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN) {
                return (T) exportItems;
            } else {
                return (T) importItems;
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
