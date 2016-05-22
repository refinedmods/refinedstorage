package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerDetector;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.RedstoneMode;

public class TileDetector extends TileMachine implements ICompareConfig {
    public static final int MODE_UNDER = 0;
    public static final int MODE_EQUAL = 1;
    public static final int MODE_ABOVE = 2;

    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";
    public static final String NBT_AMOUNT = "Amount";
    public static final String NBT_DESC_POWERED = "Powered";

    private BasicItemHandler filter = new BasicItemHandler(1, this);

    private int compare = 0;
    private int mode = MODE_EQUAL;
    private int amount = 0;

    private boolean powered = false;

    @Override
    public void onDisconnected(World world) {
        super.onDisconnected(world);

        powered = false;
    }

    @Override
    public int getEnergyUsage() {
        return 3;
    }

    @Override
    public void updateMachine() {
        if (ticks % 5 == 0) {
            ItemStack slot = filter.getStackInSlot(0);

            boolean wasPowered = powered;

            if (slot != null) {
                boolean foundAny = false;

                for (ItemGroup group : controller.getItemGroups()) {
                    if (group.compare(slot, compare)) {
                        foundAny = true;

                        switch (mode) {
                            case MODE_UNDER:
                                powered = group.getQuantity() < amount;
                                break;
                            case MODE_EQUAL:
                                powered = group.getQuantity() == amount;
                                break;
                            case MODE_ABOVE:
                                powered = group.getQuantity() > amount;
                                break;
                        }

                        break;
                    }
                }

                if (!foundAny) {
                    if (mode == MODE_UNDER && amount != 0) {
                        powered = true;
                    } else if (mode == MODE_EQUAL && amount == 0) {
                        powered = true;
                    } else {
                        powered = false;
                    }
                }
            } else {
                powered = false;
            }

            if (powered != wasPowered) {
                worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.DETECTOR);

                RefinedStorageUtils.updateBlock(worldObj, pos);
            }
        }
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;

        markDirty();
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

        if (nbt.hasKey(NBT_AMOUNT)) {
            amount = nbt.getInteger(NBT_AMOUNT);
        }

        RefinedStorageUtils.restoreItems(filter, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);
        nbt.setInteger(NBT_AMOUNT, amount);

        RefinedStorageUtils.saveItems(filter, 0, nbt);
    }

    @Override
    public void readFromDescriptionPacketNBT(NBTTagCompound tag) {
        super.readFromDescriptionPacketNBT(tag);

        powered = tag.getBoolean(NBT_DESC_POWERED);
    }

    @Override
    public void writeToDescriptionPacketNBT(NBTTagCompound tag) {
        super.writeToDescriptionPacketNBT(tag);

        tag.setBoolean(NBT_DESC_POWERED, powered);
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
        buf.writeInt(amount);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
        amount = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerDetector.class;
    }

    public IItemHandler getInventory() {
        return filter;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
