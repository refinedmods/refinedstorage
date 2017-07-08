package com.raoulvdberge.refinedstorage.integration.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemExternal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StorageItemItemRepository extends StorageItemExternal {
    @CapabilityInject(IItemRepository.class)
    private static final Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;

    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IDrawerGroup> groupSupplier;

    public StorageItemItemRepository(NetworkNodeExternalStorage externalStorage, Supplier<IDrawerGroup> groupSupplier) {
        this.externalStorage = externalStorage;
        this.groupSupplier = groupSupplier;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        IItemRepository repository = getRepositoryFromSupplier();

        if (repository == null) {
            return Collections.emptyList();
        }

        return repository.getAllItems().stream().map(r -> ItemHandlerHelper.copyStackWithSize(r.itemPrototype, r.count)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        IItemRepository repository = getRepositoryFromSupplier();

        if (repository == null) {
            return stack;
        }

        return RSUtils.transformEmptyToNull(repository.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate));
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        IItemRepository repository = getRepositoryFromSupplier();

        if (repository == null) {
            return stack;
        }

        return repository.extractItem(stack, size, simulate, s -> API.instance().getComparer().isEqual(stack, s, flags));
    }

    @Override
    public int getStored() {
        IItemRepository repository = getRepositoryFromSupplier();

        if (repository == null) {
            return 0;
        }

        return repository.getAllItems().stream().mapToInt(r -> r.count).sum();
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }

    @Override
    public int getCapacity() {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return 0;
        }

        long capacity = 0;

        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);

            if (drawer.isEnabled()) {
                capacity += drawer.getMaxCapacity();
            }
        }

        if (capacity >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) capacity;
    }

    private IItemRepository getRepositoryFromSupplier() {
        IDrawerGroup group = groupSupplier.get();

        if (group == null) {
            return null;
        }

        return group.getCapability(ITEM_REPOSITORY_CAPABILITY, null);
    }
}
