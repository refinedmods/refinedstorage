package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorage;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.data.TileDataParameter;

public class TileInterface extends TileNode implements IComparable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();

    private static final String NBT_COMPARE = "Compare";

    private ItemHandlerBasic importItems = new ItemHandlerBasic(9, this);
    private ItemHandlerBasic exportSpecimenItems = new ItemHandlerBasic(9, this);
    private ItemHandlerBasic exportItems = new ItemHandlerBasic(9, this);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private int compare = 0;

    private int currentSlot = 0;

    public TileInterface() {
        dataManager.addWatchedParameter(COMPARE);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.interfaceUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (currentSlot >= importItems.getSlots()) {
            currentSlot = 0;
        }

        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot == null) {
            currentSlot++;
        } else if (ticks % upgrades.getSpeed() == 0) {
            int size = Math.min(slot.stackSize, upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1);

            ItemStack remainder = network.insertItem(slot, size, false);

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
                    exportItems.setStackInSlot(i, network.insertItem(got, got.stackSize, false));
                }
            } else {
                int delta = got == null ? wanted.stackSize : (wanted.stackSize - got.stackSize);

                if (delta > 0) {
                    ItemStack result = network.extractItem(wanted, delta, compare);

                    if (result != null) {
                        if (got == null) {
                            exportItems.setStackInSlot(i, result);
                        } else {
                            exportItems.getStackInSlot(i).stackSize += result.stackSize;
                        }
                    }
                } else if (delta < 0) {
                    ItemStack remainder = network.insertItem(got, Math.abs(delta), false);

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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(importItems, 0, tag);
        readItems(exportSpecimenItems, 1, tag);
        readItems(exportItems, 2, tag);
        readItems(upgrades, 3, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(importItems, 0, tag);
        writeItems(exportSpecimenItems, 1, tag);
        writeItems(exportItems, 2, tag);
        writeItems(upgrades, 3, tag);

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
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
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(importItems, exportItems, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
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
