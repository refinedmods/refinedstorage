package com.raoulvdberge.refinedstorage.apiimpl;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridManager;
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
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IQuantityFormatter;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingRequestInfo;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.GridManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterChannel;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.*;
import com.raoulvdberge.refinedstorage.apiimpl.util.Comparer;
import com.raoulvdberge.refinedstorage.apiimpl.util.FluidStackList;
import com.raoulvdberge.refinedstorage.apiimpl.util.ItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.util.QuantityFormatter;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
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
    private IGridManager gridManager = new GridManager();
    private IStorageDiskRegistry storageDiskRegistry = new StorageDiskRegistry();
    private IStorageDiskSync storageDiskSync = new StorageDiskSync();
    private Map<StorageType, TreeSet<IExternalStorageProvider>> externalStorageProviders = new HashMap<>();
    private List<ICraftingPatternRenderHandler> patternRenderHandlers = new LinkedList<>();

    public static IRSAPI instance() {
        return INSTANCE;
    }

    /* TODO API DELIVERY public static void deliver(ASMDataTable asmDataTable) {
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
    }*/

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
    public INetworkNodeManager getNetworkNodeManager(ServerWorld world) {
        String name = world.getDimension().getType().getRegistryName().getNamespace() + "_" + world.getDimension().getType().getRegistryName().getPath() + "_" + NetworkNodeManager.NAME;

        return world.getSavedData().getOrCreate(() -> new NetworkNodeManager(name, world), name);
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
        return new ItemStackList();
    }

    @Override
    @Nonnull
    public IStackList<FluidStack> createFluidStackList() {
        return new FluidStackList();
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementList createCraftingMonitorElementList() {
        return new CraftingMonitorElementList();
    }

    @Nonnull
    @Override
    public IGridManager getGridManager() {
        return gridManager;
    }

    @Nonnull
    @Override
    public IStorageDiskRegistry getStorageDiskRegistry() {
        return storageDiskRegistry;
    }

    @Nonnull
    @Override
    public IStorageDiskManager getStorageDiskManager(ServerWorld anyWorld) {
        ServerWorld world = anyWorld.getServer().getWorld(DimensionType.OVERWORLD);

        return world.getSavedData().getOrCreate(() -> new StorageDiskManager(StorageDiskManager.NAME, world), StorageDiskManager.NAME);
    }

    @Nonnull
    @Override
    public IStorageDiskSync getStorageDiskSync() {
        return storageDiskSync;
    }

    @Override
    public void addExternalStorageProvider(StorageType type, IExternalStorageProvider provider) {
        externalStorageProviders.computeIfAbsent(type, k -> new TreeSet<>((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))).add(provider);
    }

    @Override
    public Set<IExternalStorageProvider> getExternalStorageProviders(StorageType type) {
        TreeSet<IExternalStorageProvider> providers = externalStorageProviders.get(type);

        return providers == null ? Collections.emptySet() : providers;
    }

    @Override
    @Nonnull
    public IStorageDisk<ItemStack> createDefaultItemDisk(ServerWorld world, int capacity) {
        return new StorageDiskItem(world, capacity);
    }

    @Override
    @Nonnull
    public IStorageDisk<FluidStack> createDefaultFluidDisk(ServerWorld world, int capacity) {
        return new StorageDiskFluid(world, capacity);
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(ItemStack stack) {
        return new CraftingRequestInfo(stack);
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(FluidStack stack) {
        return new CraftingRequestInfo(stack);
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(CompoundNBT tag) throws CraftingTaskReadException {
        return new CraftingRequestInfo(tag);
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
    public void discoverNode(IWorld world, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));

            if (tile != null) {
                INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite()).orElse(null);
                if (proxy != null) {
                    INetworkNode node = proxy.getNode();

                    if (node.getNetwork() != null) {
                        node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().world(), node.getNetwork().getPosition());

                        return;
                    }
                }
            }
        }
    }

    // TODO: Remove eventually
    @Override
    public int getItemStackHashCode(ItemStack stack) {
        int result = stack.getItem().hashCode();

        if (stack.hasTag()) {
            result = getHashCode(stack.getTag(), result);
        }

        return result;
    }

    private int getHashCode(INBT tag, int result) {
        if (tag instanceof CompoundNBT) {
            result = getHashCode((CompoundNBT) tag, result);
        } else if (tag instanceof ListNBT) {
            result = getHashCode((ListNBT) tag, result);
        } else {
            result = 31 * result + tag.hashCode();
        }

        return result;
    }

    private int getHashCode(CompoundNBT tag, int result) {
        for (String key : tag.keySet()) {
            result = 31 * result + key.hashCode();
            result = getHashCode(tag.get(key), result);
        }

        return result;
    }

    private int getHashCode(ListNBT tag, int result) {
        for (int i = 0; i < tag.size(); ++i) {
            result = getHashCode(tag.get(i), result);
        }

        return result;
    }

    // TODO: Remove eventually
    @Override
    public int getFluidStackHashCode(FluidStack stack) {
        int result = stack.getFluid().hashCode();

        if (stack.getTag() != null) {
            result = getHashCode(stack.getTag(), result);
        }

        return result;
    }

    @Override
    public int getNetworkNodeHashCode(INetworkNode node) {
        int result = node.getPos().hashCode();
        result = 31 * result + node.getWorld().getDimension().getType().getId();

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

        if (left.getWorld().getDimension().getType().getId() != rightNode.getWorld().getDimension().getType().getId()) {
            return false;
        }

        return left.getPos().equals(rightNode.getPos());
    }
}
