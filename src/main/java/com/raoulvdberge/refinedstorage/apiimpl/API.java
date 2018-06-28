package com.raoulvdberge.refinedstorage.apiimpl;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.IRSAPI;
import com.raoulvdberge.refinedstorage.api.RSAPIInject;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.wireless.IWirelessGridRegistry;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskRegistry;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSync;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IOneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.api.util.IQuantityFormatter;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless.WirelessGridRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterChannel;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.*;
import com.raoulvdberge.refinedstorage.apiimpl.util.*;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class API implements IRSAPI {
    private static final IRSAPI INSTANCE = new API();

    private IComparer comparer = new Comparer();
    private IQuantityFormatter quantityFormatter = new QuantityFormatter();
    private INetworkNodeRegistry networkNodeRegistry = new NetworkNodeRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();
    private ICraftingPreviewElementRegistry craftingPreviewElementRegistry = new CraftingPreviewElementRegistry();
    private IReaderWriterHandlerRegistry readerWriterHandlerRegistry = new ReaderWriterHandlerRegistry();
    private IWirelessGridRegistry gridRegistry = new WirelessGridRegistry();
    private IStorageDiskRegistry storageDiskRegistry = new StorageDiskRegistry();
    private IStorageDiskSync storageDiskSync = new StorageDiskSync();
    private IOneSixMigrationHelper oneSixMigrationHelper = new OneSixMigrationHelper();
    private Map<StorageType, List<IExternalStorageProvider>> externalStorageProviders = new HashMap<>();
    private List<ICraftingPatternRenderHandler> patternRenderHandlers = new LinkedList<>();

    public static IRSAPI instance() {
        return INSTANCE;
    }

    public static void deliver(ASMDataTable asmDataTable) {
        String annotationClassName = RSAPIInject.class.getCanonicalName();

        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(annotationClassName);

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class clazz = Class.forName(asmData.getClassName());
                Field field = clazz.getField(asmData.getObjectName());

                if (field.getType() == IRSAPI.class) {
                    field.set(null, INSTANCE);
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set: {}" + asmData.getClassName() + "." + asmData.getObjectName(), e);
            }
        }
    }

    @Nonnull
    @Override
    public IComparer getComparer() {
        return comparer;
    }

    @Override
    @Nonnull
    public IQuantityFormatter getQuantityFormatter() {
        return quantityFormatter;
    }

    @Override
    @Nonnull
    public INetworkNodeRegistry getNetworkNodeRegistry() {
        return networkNodeRegistry;
    }

    @Override
    public INetworkNodeManager getNetworkNodeManager(World world) {
        if (world.isRemote) {
            throw new IllegalArgumentException("Attempting to access network node manager on the client");
        }

        MapStorage storage = world.getPerWorldStorage();
        NetworkNodeManager instance = (NetworkNodeManager) storage.getOrLoadData(NetworkNodeManager.class, NetworkNodeManager.NAME);

        if (instance == null) {
            instance = new NetworkNodeManager(NetworkNodeManager.NAME);

            storage.setData(NetworkNodeManager.NAME, instance);
        } else {
            instance.tryReadNodes(world);
        }

        return instance;
    }

    @Override
    @Nonnull
    public ICraftingTaskRegistry getCraftingTaskRegistry() {
        return craftingTaskRegistry;
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementRegistry getCraftingMonitorElementRegistry() {
        return craftingMonitorElementRegistry;
    }

    @Override
    @Nonnull
    public ICraftingPreviewElementRegistry getCraftingPreviewElementRegistry() {
        return craftingPreviewElementRegistry;
    }

    @Nonnull
    @Override
    public IReaderWriterHandlerRegistry getReaderWriterHandlerRegistry() {
        return readerWriterHandlerRegistry;
    }

    @Nonnull
    @Override
    public IReaderWriterChannel createReaderWriterChannel(String name, INetwork network) {
        return new ReaderWriterChannel(name, network);
    }

    @Nonnull
    @Override
    public IStackList<ItemStack> createItemStackList() {
        return new StackListItem();
    }

    @Override
    @Nonnull
    public IStackList<FluidStack> createFluidStackList() {
        return new StackListFluid();
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementList createCraftingMonitorElementList() {
        return new CraftingMonitorElementList();
    }

    @Nonnull
    @Override
    public IWirelessGridRegistry getWirelessGridRegistry() {
        return gridRegistry;
    }

    @Nonnull
    @Override
    public IStorageDiskRegistry getStorageDiskRegistry() {
        return storageDiskRegistry;
    }

    @Nonnull
    @Override
    public IStorageDiskManager getStorageDiskManager(World world) {
        if (world.isRemote) {
            throw new IllegalArgumentException("Attempting to access storage disk manager on the client");
        }

        MapStorage storage = world.getMapStorage();
        StorageDiskManager instance = (StorageDiskManager) storage.getOrLoadData(StorageDiskManager.class, StorageDiskManager.NAME);

        if (instance == null) {
            instance = new StorageDiskManager(StorageDiskManager.NAME);

            storage.setData(StorageDiskManager.NAME, instance);
        } else {
            instance.tryReadDisks(world);
        }

        return instance;
    }

    @Nonnull
    @Override
    public IStorageDiskSync getStorageDiskSync() {
        return storageDiskSync;
    }

    @Override
    public void addExternalStorageProvider(StorageType type, IExternalStorageProvider provider) {
        externalStorageProviders.computeIfAbsent(type, k -> new ArrayList<>()).add(provider);
    }

    @Override
    public List<IExternalStorageProvider> getExternalStorageProviders(StorageType type) {
        List<IExternalStorageProvider> providers = externalStorageProviders.get(type);

        return providers == null ? Collections.emptyList() : providers;
    }

    @Override
    @Nonnull
    public IStorageDisk<ItemStack> createDefaultItemDisk(World world, int capacity) {
        return new StorageDiskItem(world, capacity);
    }

    @Override
    @Nonnull
    public IStorageDisk<FluidStack> createDefaultFluidDisk(World world, int capacity) {
        return new StorageDiskFluid(world, capacity);
    }

    @Override
    @Nonnull
    public IOneSixMigrationHelper getOneSixMigrationHelper() {
        return oneSixMigrationHelper;
    }

    @Override
    public void addPatternRenderHandler(ICraftingPatternRenderHandler renderHandler) {
        patternRenderHandlers.add(renderHandler);
    }

    @Override
    public List<ICraftingPatternRenderHandler> getPatternRenderHandlers() {
        return patternRenderHandlers;
    }

    @Override
    public void openWirelessGrid(EntityPlayer player, EnumHand hand, int networkDimension, int id) {
        player.openGui(RS.INSTANCE, RSGui.WIRELESS_GRID, player.getEntityWorld(), hand.ordinal(), networkDimension, id);
    }

    @Override
    public void discoverNode(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));

            if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite())) {
                INetworkNodeProxy nodeProxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite());
                INetworkNode node = nodeProxy.getNode();

                if (node.getNetwork() != null) {
                    node.getNetwork().getNodeGraph().rebuild();

                    return;
                }
            }
        }
    }

    @Override
    public int getItemStackHashCode(ItemStack stack, boolean tag) {
        int result = stack.getItem().hashCode();
        result = 31 * result + (stack.getItemDamage() + 1);

        if (tag && stack.hasTagCompound()) {
            result = calcHashCode(stack.getTagCompound(), result);
        }

        return result;
    }

    private int calcHashCode(NBTBase tag, int result) {
        if (tag instanceof NBTTagCompound) {
            result = calcHashCode((NBTTagCompound) tag, result);
        } else if (tag instanceof NBTTagList) {
            result = calcHashCode((NBTTagList) tag, result);
        } else {
            result = 31 * result + tag.hashCode();
        }

        return result;
    }

    private int calcHashCode(NBTTagCompound tag, int result) {
        for (String key : tag.getKeySet()) {
            result = 31 * result + key.hashCode();
            result = calcHashCode(tag.getTag(key), result);
        }

        return result;
    }

    private int calcHashCode(NBTTagList tag, int result) {
        for (int i = 0; i < tag.tagCount(); ++i) {
            result = calcHashCode(tag.get(i), result);
        }

        return result;
    }

    @Override
    public int getFluidStackHashCode(FluidStack stack) {
        int result = stack.getFluid().hashCode();

        if (stack.tag != null) {
            result = calcHashCode(stack.tag, result);
        }

        return result;
    }

    @Override
    public int getNetworkNodeHashCode(INetworkNode node) {
        int result = node.getPos().hashCode();
        result = 31 * result + node.getWorld().provider.getDimension();

        return result;
    }

    @Override
    public boolean isNetworkNodeEqual(INetworkNode left, Object right) {
        if (!(right instanceof INetworkNode)) {
            return false;
        }

        if (left == right) {
            return true;
        }

        INetworkNode rightNode = (INetworkNode) right;

        if (left.getWorld().provider.getDimension() != rightNode.getWorld().provider.getDimension()) {
            return false;
        }

        return left.getPos().equals(rightNode.getPos());
    }
}
