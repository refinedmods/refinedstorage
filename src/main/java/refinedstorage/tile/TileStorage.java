package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.apiimpl.storage.NBTStorage;
import refinedstorage.block.BlockStorage;
import refinedstorage.block.EnumStorageType;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.tile.config.*;

import java.util.List;

public class TileStorage extends TileNode implements IStorageProvider, IStorageGui, ICompareConfig, IModeConfig {
    class Storage extends NBTStorage {
        public Storage() {
            super(TileStorage.this.getStorageTag(), TileStorage.this.getCapacity(), TileStorage.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!ModeFilter.respectsMode(filters, TileStorage.this, compare, stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insertItem(stack, size, simulate);
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private BasicItemHandler filters = new BasicItemHandler(9, this);

    private NBTTagCompound storageTag = NBTStorage.createNBT();

    private Storage storage;

    private EnumStorageType type;

    private int priority = 0;
    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;
    private int stored;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.storageUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new Storage();

            if (network != null) {
                network.getStorage().rebuild();
            }
        }
    }

    public void onBreak() {
        if (storage != null) {
            storage.writeToNBT();
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getStorage().rebuild();
    }

    @Override
    public void addStorages(List<IStorage> storages) {
        if (storage != null) {
            storages.add(storage);
        }
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        TileBase.readItems(filters, 0, nbt);

        if (nbt.hasKey(NBT_PRIORITY)) {
            priority = nbt.getInteger(NBT_PRIORITY);
        }

        if (nbt.hasKey(NBT_STORAGE)) {
            storageTag = nbt.getCompoundTag(NBT_STORAGE);
        }

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        TileBase.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);

        if (storage != null) {
            storage.writeToNBT();
        }

        tag.setTag(NBT_STORAGE, storageTag);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
    }

    public EnumStorageType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.STORAGE) {
            this.type = ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
        }

        return type == null ? EnumStorageType.TYPE_1K : type;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(NBTStorage.getStoredFromNBT(storageTag));
        buf.writeInt(priority);
        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

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
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:storage." + getType().getId() + ".name";
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
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
        RefinedStorage.INSTANCE.network.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public NBTStorage getStorage() {
        return storage;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
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
