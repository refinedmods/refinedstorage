package refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.tile.IStorageGui;
import refinedstorage.tile.TileSlave;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConstants;

import java.util.List;

public class TileExternalStorage extends TileSlave implements IStorageProvider, IStorageGui, ICompareConfig, IModeConfig {
    public static final String NBT_PRIORITY = "Priority";
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private BasicItemHandler filters = new BasicItemHandler(9, this);

    private int priority = 0;
    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    private int stored;
    private int capacity;
    private int energyUsage;

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public void updateSlave() {
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(priority);
        buf.writeInt(getStored());
        buf.writeInt(getCapacity());
        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        priority = buf.readInt();
        stored = buf.readInt();
        capacity = buf.readInt();
        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerStorage.class;
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(filters, 0, nbt);

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
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
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
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    @Override
    public void addStorages(List<IStorage> storages) {
        TileEntity facing = getFacingTile();

        if (facing instanceof IDrawerGroup) {
            IDrawerGroup group = (IDrawerGroup) facing;


            energyUsage = group.getDrawerCount();
            stored = 0;
            capacity = 0;

            for (int i = 0; i < group.getDrawerCount(); ++i) {
                if (group.isDrawerEnabled(i)) {
                    DrawerStorage storage = new DrawerStorage(this, group.getDrawer(i));

                    storages.add(storage);

                    stored += storage.getStored();
                    capacity += storage.getCapacity();
                }
            }
        } else if (facing instanceof IDrawer) {
            DrawerStorage storage = new DrawerStorage(this, (IDrawer) facing);

            energyUsage = 1;
            stored = storage.getStored();
            capacity = storage.getCapacity();

            storages.add(storage);
        } else if (facing instanceof IDeepStorageUnit) {
            DeepStorageUnitStorage storage = new DeepStorageUnitStorage(this, (IDeepStorageUnit) facing);

            energyUsage = 1;
            stored = storage.getStored();
            capacity = storage.getCapacity();

            storages.add(storage);
        } else {
            IItemHandler handler = RefinedStorageUtils.getItemHandler(facing, getDirection().getOpposite());

            if (handler != null) {
                ItemHandlerStorage storage = new ItemHandlerStorage(this, handler);

                energyUsage = 1;
                stored = storage.getStored();
                capacity = storage.getCapacity();

                storages.add(storage);
            } else {
                energyUsage = 0;
                stored = 0;
                capacity = 0;
            }
        }
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:external_storage";
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
    public int getStored() {
        return stored;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
    }
}
