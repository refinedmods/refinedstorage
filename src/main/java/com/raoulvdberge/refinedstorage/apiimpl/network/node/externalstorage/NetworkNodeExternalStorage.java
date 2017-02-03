package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.integration.cyclopscore.IntegrationCyclopsCore;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeExternalStorage extends NetworkNode implements IStorageProvider, IGuiStorage, IComparable, IFilterable, IPrioritizable, IType, IAccessType {
    public static final String ID = "external_storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int networkTicks;

    private List<StorageItemExternal> itemStorages = new ArrayList<>();
    private List<StorageFluidExternal> fluidStorages = new ArrayList<>();

    public NetworkNodeExternalStorage(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.externalStorageUsage + ((itemStorages.size() + fluidStorages.size()) * RS.INSTANCE.config.externalStoragePerStorageUsage);
    }

    @Override
    public void onConnectedStateChange(INetworkMaster network, boolean state) {
        super.onConnectedStateChange(network, state);

        updateStorage(network);

        network.getItemStorageCache().invalidate();
        network.getFluidStorageCache().invalidate();
    }

    @Override
    public void update() {
        super.update();

        if (network != null) {
            if (networkTicks++ == 0) {
                updateStorage(network);

                return;
            }

            for (StorageItemExternal storage : itemStorages) {
                storage.detectChanges(network);
            }

            boolean fluidChangeDetected = false;

            for (StorageFluidExternal storage : fluidStorages) {
                if (storage.updateCache()) {
                    fluidChangeDetected = true;
                }
            }

            if (fluidChangeDetected) {
                network.getFluidStorageCache().invalidate();
            }
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        RSUtils.writeItems(itemFilters, 0, tag);
        RSUtils.writeItems(fluidFilters, 1, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        RSUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        RSUtils.readItems(itemFilters, 0, tag);
        RSUtils.readItems(fluidFilters, 1, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        accessType = RSUtils.readAccessType(tag);
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

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    public void updateStorage(INetworkMaster network) {
        itemStorages.clear();
        fluidStorages.clear();

        TileEntity facing = getFacingTile();

        if (type == IType.ITEMS) {
            if (facing instanceof IDrawerGroup) {
                itemStorages.add(new StorageItemDrawerGroup(this, () -> {
                    TileEntity f = getFacingTile();

                    return f instanceof IDrawerGroup ? (IDrawerGroup) f : null;
                }));
            } else if (facing instanceof IDrawer) {
                itemStorages.add(new StorageItemDrawer(this, () -> {
                    TileEntity f = getFacingTile();

                    return f instanceof IDrawer ? (IDrawer) f : null;
                }));
            } else if (facing instanceof IDeepStorageUnit) {
                itemStorages.add(new StorageItemDSU(this, () -> {
                    TileEntity f = getFacingTile();

                    return f instanceof IDeepStorageUnit ? (IDeepStorageUnit) f : null;
                }));
            } else if (IntegrationCyclopsCore.isLoaded() && StorageItemCyclops.isValid(facing, holder.getDirection().getOpposite())) {
                itemStorages.add(new StorageItemCyclops(this));
            } else if (!(facing instanceof TileNode)) {
                IItemHandler itemHandler = RSUtils.getItemHandler(facing, holder.getDirection().getOpposite());

                if (itemHandler != null) {
                    itemStorages.add(new StorageItemItemHandler(this, () -> RSUtils.getItemHandler(getFacingTile(), holder.getDirection().getOpposite())));
                }
            }
        } else if (type == IType.FLUIDS) {
            IFluidHandler fluidHandler = RSUtils.getFluidHandler(facing, holder.getDirection().getOpposite());

            if (fluidHandler != null) {
                fluidStorages.add(new StorageFluidExternal(this, () -> RSUtils.getFluidHandler(getFacingTile(), holder.getDirection().getOpposite())));
            }
        }

        network.getItemStorageCache().invalidate();
        network.getFluidStorageCache().invalidate();
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        storages.addAll(this.itemStorages);
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        storages.addAll(this.fluidStorages);
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:external_storage";
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return TileExternalStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return TileExternalStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return TileExternalStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return TileExternalStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<Boolean> getVoidExcessParameter() {
        return null;
    }

    @Override
    public TileDataParameter<AccessType> getAccessTypeParameter() {
        return TileExternalStorage.ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return null;
    }

    @Override
    public int getStored() {
        return TileExternalStorage.STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return TileExternalStorage.CAPACITY.getValue();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType type) {
        this.accessType = type;

        if (network != null) {
            network.getItemStorageCache().invalidate();
            network.getFluidStorageCache().invalidate();
        }

        markDirty();
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return TileExternalStorage.TYPE;
    }

    @Override
    public int getType() {
        return holder.world().isRemote ? TileExternalStorage.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();

        if (network != null) {
            updateStorage(network);
        }
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }

    public ItemHandlerBasic getItemFilters() {
        return itemFilters;
    }

    public ItemHandlerFluid getFluidFilters() {
        return fluidFilters;
    }

    public List<StorageItemExternal> getItemStorages() {
        return itemStorages;
    }

    public List<StorageFluidExternal> getFluidStorages() {
        return fluidStorages;
    }
}
