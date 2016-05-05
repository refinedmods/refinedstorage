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
import refinedstorage.container.ContainerSolderer;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.solderer.ISoldererRecipe;
import refinedstorage.tile.solderer.SoldererRegistry;
import refinedstorage.util.InventoryUtils;
import refinedstorage.util.UpgradeUtils;

public class TileSolderer extends TileMachine implements IInventory, ISidedInventory {
    public static final String NBT_WORKING = "Working";
    public static final String NBT_PROGRESS = "Progress";

    public static final int[] FACES = new int[]{
        0, 1, 2
    };
    public static final int[] FACES_DOWN = new int[]{
        3
    };

    private InventorySimple inventory = new InventorySimple("solderer", 4, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;
    private int duration;

    @Override
    public int getEnergyUsage() {
        return 3;
    }

    @Override
    public void updateMachine() {
        ISoldererRecipe newRecipe = SoldererRegistry.getRecipe(inventory);

        if (newRecipe == null) {
            reset();
        } else if (newRecipe != recipe) {
            boolean isSameItem = inventory.getStackInSlot(3) != null ? InventoryUtils.compareStackNoQuantity(inventory.getStackInSlot(3), newRecipe.getResult()) : false;

            if (inventory.getStackInSlot(3) == null || (isSameItem && ((inventory.getStackInSlot(3).stackSize + newRecipe.getResult().stackSize) <= inventory.getStackInSlot(3).getMaxStackSize()))) {
                recipe = newRecipe;
                progress = 0;
                working = true;

                markDirty();
            }
        } else if (working) {
            progress += 1 + UpgradeUtils.getUpgradeCount(upgradesInventory, ItemUpgrade.TYPE_SPEED);

            if (progress >= recipe.getDuration()) {
                if (inventory.getStackInSlot(3) != null) {
                    inventory.getStackInSlot(3).stackSize += recipe.getResult().stackSize;
                } else {
                    inventory.setInventorySlotContents(3, recipe.getResult());
                }

                for (int i = 0; i < 3; ++i) {
                    if (recipe.getRow(i) != null) {
                        inventory.decrStackSize(i, recipe.getRow(i).stackSize);
                    }
                }

                reset();
            }
        }
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();

        reset();
    }

    public void reset() {
        progress = 0;
        working = false;
        recipe = null;

        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        InventoryUtils.restoreInventory(this, 0, nbt);
        InventoryUtils.restoreInventory(upgradesInventory, 1, nbt);

        recipe = SoldererRegistry.getRecipe(inventory);

        if (nbt.hasKey(NBT_WORKING)) {
            working = nbt.getBoolean(NBT_WORKING);
        }

        if (nbt.hasKey(NBT_PROGRESS)) {
            progress = nbt.getInteger(NBT_PROGRESS);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        InventoryUtils.saveInventory(this, 0, nbt);
        InventoryUtils.saveInventory(upgradesInventory, 1, nbt);

        nbt.setBoolean(NBT_WORKING, working);
        nbt.setInteger(NBT_PROGRESS, progress);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        progress = buf.readInt();
        duration = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(progress);
        buf.writeInt(recipe != null ? recipe.getDuration() : 0);
    }

    @Override
    public void receiveData(ByteBuf buf) {
        super.receiveData(buf);

        boolean lastWorking = working;

        working = buf.readBoolean();

        if (working != lastWorking) {
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2 | 4);
        }
    }

    @Override
    public void sendData(ByteBuf buf) {
        super.sendData(buf);

        buf.writeBoolean(working);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerSolderer.class;
    }

    public boolean isWorking() {
        return working;
    }

    public int getProgressScaled(int i) {
        if (progress > duration) {
            return i;
        }
        return (int) ((float) progress / (float) duration * (float) i);
    }

    @Override
    public IInventory getDroppedInventory() {
        return inventory;
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
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {
        return slot != 3;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        return slot == 3;
    }

    public InventorySimple getUpgradesInventory() {
        return upgradesInventory;
    }
}
