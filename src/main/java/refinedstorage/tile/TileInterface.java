package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerInterface;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileInterface extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    private BasicItemHandler items = new BasicItemHandler(9 * 3, this);
    private BasicItemHandler upgrades = new BasicItemHandler(4, this, new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

    private int compare = 0;

    private int currentSlot = 0;

    @Override
    public int getEnergyUsage() {
        return 4 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
        if (currentSlot > 8) {
            currentSlot = 0;
        }

        ItemStack slot = items.getStackInSlot(currentSlot);

        if (slot == null) {
            currentSlot++;
        } else {
            if (ticks % RefinedStorageUtils.getSpeed(upgrades) == 0) {
                if (controller.push(ItemHandlerHelper.copyStackWithSize(slot, 1))) {
                    items.extractItem(currentSlot, 1, false);
                }
            }
        }

        for (int i = 9; i < 18; ++i) {
            ItemStack wanted = items.getStackInSlot(i);
            ItemStack got = items.getStackInSlot(i + 9);

            if (wanted != null) {
                boolean mayTake = false;

                if (got != null) {
                    if (!RefinedStorageUtils.compareStack(wanted, got, compare)) {
                        if (controller.push(got)) {
                            items.setStackInSlot(i + 9, null);
                        }
                    } else {
                        mayTake = true;
                    }
                } else {
                    mayTake = true;
                }

                if (mayTake) {
                    got = items.getStackInSlot(i + 9);

                    int needed = got == null ? wanted.stackSize : wanted.stackSize - got.stackSize;

                    if (needed > 0) {
                        ItemStack took = controller.take(ItemHandlerHelper.copyStackWithSize(wanted, needed), compare);

                        if (took != null) {
                            if (got == null) {
                                items.setStackInSlot(i + 9, took);
                            } else {
                                got.stackSize += took.stackSize;
                            }
                        }
                    }
                }
            } else if (got != null) {
                if (controller.push(got)) {
                    items.setStackInSlot(i + 9, null);
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreItems(items, 0, nbt);
        RefinedStorageUtils.restoreItems(upgrades, 1, nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveItems(items, 0, nbt);
        RefinedStorageUtils.saveItems(upgrades, 1, nbt);

        nbt.setInteger(NBT_COMPARE, compare);
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
        return ContainerInterface.class;
    }

    public IItemHandler getItems() {
        return items;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        BasicItemHandler dummy = new BasicItemHandler(9 + 9 + 4);

        for (int i = 0; i < 9; ++i) {
            dummy.setStackInSlot(i, items.getStackInSlot(i));
        }

        for (int i = 0; i < 9; ++i) {
            dummy.setStackInSlot(9 + i, items.getStackInSlot(18 + i));
        }

        for (int i = 0; i < 4; ++i) {
            dummy.setStackInSlot(18 + i, upgrades.getStackInSlot(i));
        }

        return dummy;
    }
}
