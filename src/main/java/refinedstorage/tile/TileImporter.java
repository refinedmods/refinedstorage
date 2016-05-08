package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerImporter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;

public class TileImporter extends TileMachine implements ICompareConfig, IModeConfig {
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("importer", 9, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private int compare = 0;
    private int mode = 0;

    private int currentSlot;

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
        TileEntity connectedTile = worldObj.getTileEntity(pos.offset(getDirection()));

        if (connectedTile instanceof ISidedInventory) {
            ISidedInventory sided = (ISidedInventory) connectedTile;

            int[] availableSlots = sided.getSlotsForFace(getDirection().getOpposite());

            if (currentSlot >= availableSlots.length) {
                currentSlot = 0;
            }

            if (availableSlots.length > 0) {
                int availableSlot = availableSlots[currentSlot];

                ItemStack stack = sided.getStackInSlot(availableSlot);

                if (stack == null) {
                    currentSlot++;
                } else {
                    if (ticks % RefinedStorageUtils.getSpeed(upgradesInventory) == 0) {
                        ItemStack toTake = stack.copy();
                        toTake.stackSize = 1;

                        if (canImport(toTake) && sided.canExtractItem(availableSlot, toTake, getDirection().getOpposite())) {
                            if (controller.push(toTake)) {
                                sided.decrStackSize(availableSlot, 1);
                                sided.markDirty();
                            }
                        } else {
                            // If we can't import and/or extract, move on (otherwise we stay on the same slot forever)
                            currentSlot++;
                        }
                    }
                }
            }
        } else if (connectedTile instanceof IInventory) {
            IInventory inventory = (IInventory) connectedTile;

            if (currentSlot >= inventory.getSizeInventory()) {
                currentSlot = 0;
            }

            ItemStack stack = inventory.getStackInSlot(currentSlot);

            if (stack != null) {
                if (ticks % RefinedStorageUtils.getSpeed(upgradesInventory) == 0) {
                    ItemStack toTake = stack.copy();
                    toTake.stackSize = 1;

                    // If we can't import and/ or push, move on (otherwise we stay on the same slot forever)
                    if (canImport(toTake)) {
                        if (controller.push(toTake)) {
                            inventory.decrStackSize(currentSlot, 1);
                            inventory.markDirty();
                        } else {
                            currentSlot++;
                        }
                    } else {
                        currentSlot++;
                    }
                }
            } else {
                currentSlot++;
            }
        } else {
            currentSlot = 0;
        }
    }

    public boolean canImport(ItemStack stack) {
        int slots = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack slot = inventory.getStackInSlot(i);

            if (slot != null) {
                slots++;

                if (RefinedStorageUtils.compareStack(stack, slot, compare)) {
                    if (isWhitelist()) {
                        return true;
                    } else if (isBlacklist()) {
                        return false;
                    }
                }
            }
        }

        if (isWhitelist()) {
            return slots == 0;
        }

        return true;
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
    public boolean isWhitelist() {
        return mode == 0;
    }

    @Override
    public boolean isBlacklist() {
        return mode == 1;
    }

    @Override
    public void setToWhitelist() {
        markDirty();

        this.mode = 0;
    }

    @Override
    public void setToBlacklist() {
        markDirty();

        this.mode = 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
        RefinedStorageUtils.saveInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerImporter.class;
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
