package com.refinedmods.refinedstorage.apiimpl;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridBehavior;
import com.refinedmods.refinedstorage.api.network.grid.IGridManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskRegistry;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTrackerManager;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IQuantityFormatter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.CraftingRequestInfo;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.CraftingTaskRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkManager;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeManager;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.grid.CraftingGridBehavior;
import com.refinedmods.refinedstorage.apiimpl.network.grid.GridManager;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskManager;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskRegistry;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskSync;
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.StorageTrackerManager;
import com.refinedmods.refinedstorage.apiimpl.util.Comparer;
import com.refinedmods.refinedstorage.apiimpl.util.FluidStackList;
import com.refinedmods.refinedstorage.apiimpl.util.ItemStackList;
import com.refinedmods.refinedstorage.apiimpl.util.QuantityFormatter;
import com.refinedmods.refinedstorage.util.StackUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class API implements IRSAPI {
    private static final Logger LOGGER = LogManager.getLogger(API.class);

    private static final IRSAPI INSTANCE = new API();

    private final IComparer comparer = new Comparer();
    private final IQuantityFormatter quantityFormatter = new QuantityFormatter();
    private final INetworkNodeRegistry networkNodeRegistry = new NetworkNodeRegistry();
    private final ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private final ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();
    private final ICraftingPreviewElementRegistry craftingPreviewElementRegistry = new CraftingPreviewElementRegistry();
    private final IGridManager gridManager = new GridManager();
    private final ICraftingGridBehavior craftingGridBehavior = new CraftingGridBehavior();
    private final IStorageDiskRegistry storageDiskRegistry = new StorageDiskRegistry();
    private final IStorageDiskSync storageDiskSync = new StorageDiskSync();
    private final Map<StorageType, TreeSet<IExternalStorageProvider<?>>> externalStorageProviders =
        new EnumMap<>(StorageType.class);
    private final List<ICraftingPatternRenderHandler> patternRenderHandlers = new LinkedList<>();

    public static IRSAPI instance() {
        return INSTANCE;
    }

    public static void deliver() {
        Type annotationType = Type.getType(RSAPIInject.class);

        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
            .map(ModFileScanData::getAnnotations)
            .flatMap(Collection::stream)
            .filter(a -> annotationType.equals(a.annotationType()))
            .toList();

        LOGGER.info("Found {} RS API injection {}", annotations.size(), annotations.size() == 1 ? "point" : "points");

        for (ModFileScanData.AnnotationData annotation : annotations) {
            try {
                Class<?> clazz = Class.forName(annotation.clazz().getClassName());
                Field field = clazz.getField(annotation.memberName());

                if (field.getType() == IRSAPI.class) {
                    field.set(null, INSTANCE);
                }

                LOGGER.info("Injected RS API in {} {}", annotation.clazz().getClassName(), annotation.memberName());
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException |
                     SecurityException e) {
                LOGGER.error("Could not inject RS API in {} {}", annotation.clazz().getClassName(),
                    annotation.memberName(), e);
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
    public INetworkNodeManager getNetworkNodeManager(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
            () -> new NetworkNodeManager(level),
            tag -> {
                NetworkNodeManager manager = new NetworkNodeManager(level);
                manager.load(tag);
                return manager;
            }
        ), NetworkNodeManager.NAME);
    }

    @Override
    public INetworkManager getNetworkManager(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
            () -> new NetworkManager(level),
            tag -> {
                NetworkManager manager = new NetworkManager(level);
                manager.load(tag);
                return manager;
            }
        ), NetworkManager.NAME);
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
    public ICraftingGridBehavior getCraftingGridBehavior() {
        return craftingGridBehavior;
    }

    @Nonnull
    @Override
    public IStorageDiskRegistry getStorageDiskRegistry() {
        return storageDiskRegistry;
    }

    @Nonnull
    @Override
    public IStorageDiskManager getStorageDiskManager(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld(); // Get the overworld

        return overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
            () -> new StorageDiskManager(overworld),
            tag -> {
                StorageDiskManager manager = new StorageDiskManager(overworld);
                manager.load(tag);
                return manager;
            }
        ), StorageDiskManager.NAME);
    }

    @Nonnull
    @Override
    public IStorageDiskSync getStorageDiskSync() {
        return storageDiskSync;
    }

    @Nonnull
    @Override
    public IStorageTrackerManager getStorageTrackerManager(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld(); // Get the overworld
        return overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
            StorageTrackerManager::new,
            tag -> {
                StorageTrackerManager manager = new StorageTrackerManager();
                manager.load(tag);
                return manager;
            }
        ), StorageTrackerManager.NAME);
    }

    @Override
    public void addExternalStorageProvider(StorageType type, IExternalStorageProvider<?> provider) {
        externalStorageProviders.computeIfAbsent(type,
            k -> new TreeSet<>((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))).add(provider);
    }

    @Override
    public Set<IExternalStorageProvider<?>> getExternalStorageProviders(StorageType type) {
        TreeSet<IExternalStorageProvider<?>> providers = externalStorageProviders.get(type);

        return providers == null ? Collections.emptySet() : providers;
    }

    @Override
    @Nonnull
    public IStorageDisk<ItemStack> createDefaultItemDisk(ServerLevel level, int capacity, @Nullable Player owner) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        return new ItemStorageDisk(level, capacity, owner == null ? null : owner.getGameProfile().getId());
    }

    @Override
    @Nonnull
    public IStorageDisk<FluidStack> createDefaultFluidDisk(ServerLevel level, int capacity, @Nullable Player owner) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        return new FluidStorageDisk(level, capacity, owner == null ? null : owner.getGameProfile().getId());
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(ItemStack stack, int count) {
        return new CraftingRequestInfo(ItemHandlerHelper.copyStackWithSize(stack, count));
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(FluidStack stack, int count) {
        return new CraftingRequestInfo(StackUtils.copy(stack, count));
    }

    @Override
    public ICraftingRequestInfo createCraftingRequestInfo(CompoundTag tag) throws CraftingTaskReadException {
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
    public int getItemStackHashCode(ItemStack stack) {
        int result = stack.getItem().hashCode();

        if (stack.hasTag()) {
            result = getHashCode(stack.getTag(), result);
        }

        return result;
    }

    private int getHashCode(Tag tag, int result) {
        if (tag instanceof CompoundTag) {
            result = getHashCode((CompoundTag) tag, result);
        } else if (tag instanceof ListTag) {
            result = getHashCode((ListTag) tag, result);
        } else {
            result = 31 * result + tag.hashCode();
        }

        return result;
    }

    private int getHashCode(CompoundTag tag, int result) {
        for (String key : tag.getAllKeys()) {
            result = 31 * result + key.hashCode();
            result = getHashCode(tag.get(key), result);
        }

        return result;
    }

    private int getHashCode(ListTag tag, int result) {
        for (Tag tagItem : tag) {
            result = getHashCode(tagItem, result);
        }

        return result;
    }

    @Override
    public int getFluidStackHashCode(FluidStack stack) {
        int result = stack.getFluid().hashCode();

        if (stack.getTag() != null) {
            result = getHashCode(stack.getTag(), result);
        }

        return result;
    }
}
