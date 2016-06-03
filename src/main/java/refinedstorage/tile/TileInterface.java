package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerInterface;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileInterface extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    private BasicItemHandler importItems = new BasicItemHandler(9, this);
    private BasicItemHandler exportSpecimenItems = new BasicItemHandler(9, this);
    private BasicItemHandler exportItems = new BasicItemHandler(9, this);
    private BasicItemHandler upgrades = new BasicItemHandler(4, this, new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

    private int compare = 0;

    private int currentSlot = 0;

    @Override
    public int getEnergyUsage() {
        return 4 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
        if (currentSlot >= importItems.getSlots()) {
            currentSlot = 0;
        }

        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot == null) {
            currentSlot++;
        } else {
            if (ticks % RefinedStorageUtils.getSpeed(upgrades) == 0) {
                if (controller.push(ItemHandlerHelper.copyStackWithSize(slot, 1))) {
                    importItems.extractItem(currentSlot, 1, false);
                }
            }
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack wanted = exportSpecimenItems.getStackInSlot(i);
            ItemStack got = exportItems.getStackInSlot(i);

            if (wanted != null) {
                boolean mayTake = false;

                if (got != null) {
                    if (!RefinedStorageUtils.compareStack(wanted, got, compare)) {
                        if (controller.push(got)) {
                            exportItems.setStackInSlot(i, null);
                        }
                    } else {
                        mayTake = true;
                    }
                } else {
                    mayTake = true;
                }

                if (mayTake) {
                    got = exportItems.getStackInSlot(i);

                    int needed = got == null ? wanted.stackSize : wanted.stackSize - got.stackSize;

                    if (needed > 0) {
                        ItemStack took = controller.take(wanted, needed, compare);

                        if (took != null) {
                            if (got == null) {
                                exportItems.setStackInSlot(i, took);
                            } else {
                                got.stackSize += took.stackSize;
                            }
                        }
                    }
                }
            } else if (got != null) {
                if (controller.push(got)) {
                    exportItems.setStackInSlot(i, null);
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
        return new CombinedInvWrapper(importItems, exportItems);
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
