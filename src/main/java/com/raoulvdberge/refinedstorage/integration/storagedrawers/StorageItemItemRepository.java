package com.raoulvdberge.refinedstorage.integration.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemExternal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StorageItemItemRepository extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IItemRepository> repositorySupplier;

    public StorageItemItemRepository(NetworkNodeExternalStorage externalStorage, Supplier<IItemRepository> repositorySupplier) {
        this.externalStorage = externalStorage;
        this.repositorySupplier = repositorySupplier;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        IItemRepository repository = repositorySupplier.get();

        if (repository == null) {
            return Collections.emptyList();
        }

        return repository.getAllItems().stream().map(r -> ItemHandlerHelper.copyStackWithSize(r.itemPrototype, r.count)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        IItemRepository repository = repositorySupplier.get();

        if (repository == null) {
            return stack;
        }

        return RSUtils.transformEmptyToNull(repository.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate));
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        IItemRepository repository = repositorySupplier.get();

        if (repository == null) {
            return stack;
        }

        int toExtract = size;

        if (toExtract > repository.getStoredItemCount(stack)) {
            toExtract = repository.getStoredItemCount(stack);
        }

        return repository.extractItem(stack, toExtract, simulate, s -> API.instance().getComparer().isEqual(stack, s, flags));
    }

    @Override
    public int getStored() {
        IItemRepository repository = repositorySupplier.get();

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
        IItemRepository repository = repositorySupplier.get();

        if (repository == null) {
            return 0;
        }

        int capacity = 0;

        for (IItemRepository.ItemRecord record : repository.getAllItems()) {
            capacity += repository.getItemCapacity(record.itemPrototype);
        }

        return capacity;
    }
}
