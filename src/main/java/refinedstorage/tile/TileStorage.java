package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.block.BlockStorage;
import refinedstorage.block.EnumStorageType;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.storage.*;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConfigUtils;

import java.util.List;

public class TileStorage extends TileMachine implements IStorageProvider, IStorage, IStorageGui, ICompareConfig, IModeConfig {
    public static final String NBT_STORAGE = "Storage";
    public static final String NBT_PRIORITY = "Priority";
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("storage", 9, this);

    private NBTTagCompound tag = NBTStorage.createNBT();

    private int priority = 0;
    private int compare = 0;
    private int mode = 0;
    private int stored;

    @Override
    public int getEnergyUsage() {
        return 3;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void provide(List<IStorage> storages) {
        storages.add(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);

        if (nbt.hasKey(NBT_STORAGE)) {
            tag = nbt.getCompoundTag(NBT_STORAGE);
        }

        if (nbt.hasKey(NBT_PRIORITY)) {
            priority = nbt.getInteger(NBT_PRIORITY);
        }

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);

        nbt.setTag(NBT_STORAGE, tag);
        nbt.setInteger(NBT_PRIORITY, priority);
        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);
    }

    public EnumStorageType getType() {
        if (worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.STORAGE) {
            return ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
        }

        return EnumStorageType.TYPE_1K;
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(NBTStorage.getStored(tag));
        buf.writeInt(priority);
        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        stored = buf.readInt();
        priority = buf.readInt();
        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerStorage.class;
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        getStorage().addItems(items);

        markDirty();
    }

    @Override
    public void push(ItemStack stack) {
        getStorage().push(stack);

        markDirty();
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        ItemStack result = getStorage().take(stack, flags);

        markDirty();

        return result;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        return ModeConfigUtils.doesNotViolateMode(inventory, this, compare, stack) && getStorage().canPush(stack);
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
    public String getGuiTitle() {
        return "block.refinedstorage:storage." + getType().getId() + ".name";
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeConfig() {
        return this;
    }

    @Override
    public ICompareConfig getCompareConfig() {
        return this;
    }

    @Override
    public IModeConfig getModeConfig() {
        return this;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    public NBTStorage getStorage() {
        return new NBTStorage(tag, getCapacity(), priority);
    }

    public NBTTagCompound getStorageTag() {
        return tag;
    }

    public void setStorageTag(NBTTagCompound tag) {
        markDirty();

        this.tag = tag;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        markDirty();

        this.priority = priority;
    }

    @Override
    public int getStored() {
        return stored;
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity();
    }
}
