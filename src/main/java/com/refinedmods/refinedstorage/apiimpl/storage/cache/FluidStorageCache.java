package com.refinedmods.refinedstorage.apiimpl.storage.cache;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class FluidStorageCache implements IStorageCache<FluidStack> {
    public static final Function<InvalidateCause, Consumer<INetwork>> INVALIDATE_ACTION = cause -> invalidatedNetwork -> invalidatedNetwork.getFluidStorageCache().invalidate(cause);

    private static final Logger LOGGER = LogManager.getLogger(FluidStorageCache.class);

    private final INetwork network;
    private final CopyOnWriteArrayList<IStorage<FluidStack>> storages = new CopyOnWriteArrayList<>();
    private final IStackList<FluidStack> list = API.instance().createFluidStackList();
    private final IStackList<FluidStack> craftables = API.instance().createFluidStackList();
    private final List<IStorageCacheListener<FluidStack>> listeners = new LinkedList<>();
    private final List<StackListResult<FluidStack>> batchedChanges = new ArrayList<>();

    public FluidStorageCache(INetwork network) {
        this.network = network;
    }

    @Override
    public void invalidate(InvalidateCause cause) {
        LOGGER.debug("Invalidating fluid storage cache of network at position {} due to {}", network.getPosition(), cause);

        storages.clear();

        network.getNodeGraph()
            .all()
            .stream()
            .map(INetworkNodeGraphEntry::getNode)
            .filter(node -> node.isActive() && node instanceof IStorageProvider)
            .forEach(node -> ((IStorageProvider) node).addFluidStorages(storages));

        list.clear();

        sort();

        for (IStorage<FluidStack> storage : storages) {
            if (storage.getAccessType() == AccessType.INSERT) {
                continue;
            }

            for (FluidStack stack : storage.getStacks()) {
                if (!stack.isEmpty()) {
                    add(stack, stack.getAmount(), true, false);
                }
            }
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public void add(@Nonnull FluidStack stack, int size, boolean rebuilding, boolean batched) {
        StackListResult<FluidStack> result = list.add(stack, size);

        if (!rebuilding) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(result));
            } else {
                batchedChanges.add(result);
            }
        }
    }

    @Override
    public void remove(@Nonnull FluidStack stack, int size, boolean batched) {
        StackListResult<FluidStack> result = list.remove(stack, size);

        if (result != null) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(result));
            } else {
                batchedChanges.add(result);
            }
        }
    }

    @Override
    public void flush() {
        if (!batchedChanges.isEmpty()) {
            if (batchedChanges.size() > 1) {
                listeners.forEach(l -> l.onChangedBulk(batchedChanges));
            } else {
                batchedChanges.forEach(change -> listeners.forEach(l -> l.onChanged(change)));
            }

            batchedChanges.clear();
        }
    }

    @Override
    public void addListener(IStorageCacheListener<FluidStack> listener) {
        listeners.add(listener);

        listener.onAttached();
    }

    @Override
    public void removeListener(IStorageCacheListener<FluidStack> listener) {
        listeners.remove(listener);
    }

    @Override
    public void reAttachListeners() {
        listeners.forEach(IStorageCacheListener::onAttached);
    }

    @Override
    public void sort() {
        storages.sort(IStorage.COMPARATOR);
    }

    @Override
    public IStackList<FluidStack> getList() {
        return list;
    }

    @Override
    public IStackList<FluidStack> getCraftablesList() {
        return craftables;
    }

    @Override
    public List<? extends IGridStack> getGridStacks() {
        final List<FluidGridStack> stacks = new ArrayList<>();

        for (StackListEntry<FluidStack> stack : network.getFluidStorageCache().getList().getStacks()) {
            stacks.add(FluidGridStack.of(
                stack,
                network.getFluidStorageTracker(),
                network.getFluidStorageCache().getCraftablesList(),
                false
            ));
        }

        for (StackListEntry<FluidStack> stack : network.getFluidStorageCache().getCraftablesList().getStacks()) {
            stacks.add(FluidGridStack.of(
                stack,
                network.getFluidStorageTracker(),
                network.getFluidStorageCache().getList(),
                true
            ));
        }

        return stacks;
    }

    @Override
    public List<IStorage<FluidStack>> getStorages() {
        return storages;
    }
}
