package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import refinedstorage.container.ContainerInterface;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.util.InventoryUtils;
import refinedstorage.util.UpgradeUtils;

public class TileInterface extends TileMachine implements ICompareConfig, ISidedInventory {
    public static final String NBT_COMPARE = "Compare";

    public static final int[] FACES = new int[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };
    public static final int[] FACES_DOWN = new int[]{
        18, 19, 20, 21, 22, 23, 24, 25, 26
    };

    private InventorySimple inventory = new InventorySimple("interface", 9 * 3, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private int compare = 0;

    private int currentSlot = 0;

    @Override
    public int getEnergyUsage() {
        return 4;
    }

    @Override
    public void updateMachine() {
        if (currentSlot > 8) {
            currentSlot = 0;
        }

        ItemStack slot = getStackInSlot(currentSlot);

        if (slot == null) {
            currentSlot++;
        } else {
            if (ticks % UpgradeUtils.getSpeed(upgradesInventory) == 0) {
                ItemStack toPush = slot.copy();
                toPush.stackSize = 1;

                if (controller.push(toPush)) {
                    decrStackSize(currentSlot, 1);
                }
            }
        }

        for (int i = 9; i < 18; ++i) {
            ItemStack wanted = inventory.getStackInSlot(i);
            ItemStack got = inventory.getStackInSlot(i + 9);

            if (wanted != null) {
                boolean mayTake = false;

                if (got != null) {
                    if (!InventoryUtils.compareStack(wanted, got, compare)) {
                        if (controller.push(got)) {
                            inventory.setInventorySlotContents(i + 9, null);
                        }
                    } else {
                        mayTake = true;
                    }
                } else {
                    mayTake = true;
                }

                if (mayTake) {
                    got = inventory.getStackInSlot(i + 9);

                    int needed = got == null ? wanted.stackSize : wanted.stackSize - got.stackSize;

                    if (needed > 0) {
                        ItemStack goingToTake = wanted.copy();
                        goingToTake.stackSize = needed;

                        ItemStack took = controller.take(goingToTake, compare);

                        if (took != null) {
                            if (got == null) {
                                inventory.setInventorySlotContents(i + 9, took);
                            } else {
                                got.stackSize += took.stackSize;
                            }
                        }

                        if (UpgradeUtils.hasUpgrade(upgradesInventory, ItemUpgrade.TYPE_CRAFTING)) {
                            CraftingPattern pattern = controller.getPattern(wanted, compare);

                            if (pattern != null && (took == null || took.stackSize != needed)) {
                                int tasksToCreate = needed - controller.getCraftingTaskCount(pattern, compare);

                                for (int j = 0; j < tasksToCreate; ++j) {
                                    controller.addCraftingTask(pattern);
                                }
                            }
                        }
                    }
                }
            } else if (got != null) {
                if (controller.push(got)) {
                    inventory.setInventorySlotContents(i + 9, null);
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

        InventoryUtils.restoreInventory(this, 0, nbt);
        InventoryUtils.restoreInventory(upgradesInventory, 1, nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        InventoryUtils.saveInventory(this, 0, nbt);
        InventoryUtils.saveInventory(upgradesInventory, 1, nbt);

        nbt.setInteger(NBT_COMPARE, compare);
    }

    public InventorySimple getUpgradesInventory() {
        return upgradesInventory;
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

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return inventory.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? FACES_DOWN : FACES;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return slot < 9;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot >= 18;
    }

    @Override
    public IInventory getDroppedInventory() {
        return upgradesInventory;
    }
}
