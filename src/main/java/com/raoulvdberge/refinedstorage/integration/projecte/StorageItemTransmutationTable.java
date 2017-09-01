package com.raoulvdberge.refinedstorage.integration.projecte;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemExternal;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class StorageItemTransmutationTable extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;

    public StorageItemTransmutationTable(NetworkNodeExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        List<ItemStack> stored = new LinkedList<>();

        if (externalStorage.getOwner() != null) {
            IKnowledgeProvider provider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(externalStorage.getOwner());

            // @todo: https://github.com/sinkillerj/ProjectE/issues/1591
            if (!provider.getClass().getName().equals("moze_intel.projecte.impl.TransmutationOffline$1")) {
                for (ItemStack knowledge : provider.getKnowledge()) {
                    stored.add(ItemHandlerHelper.copyStackWithSize(knowledge, (int) Math.floor(provider.getEmc() / (double) ProjectEAPI.getEMCProxy().getValue(knowledge))));
                }
            }
        }

        return stored;
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack actualStack = ItemHandlerHelper.copyStackWithSize(stack, size);

        if (externalStorage.getOwner() != null) {
            IKnowledgeProvider provider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(externalStorage.getOwner());

            // @todo: https://github.com/sinkillerj/ProjectE/issues/1591
            if (!provider.getClass().getName().equals("moze_intel.projecte.impl.TransmutationOffline$1")) {
                int emc = ProjectEAPI.getEMCProxy().getValue(actualStack) * size;

                if (emc == 0) {
                    return actualStack;
                }

                if (!simulate) {
                    provider.setEmc(provider.getEmc() + emc);

                    if (!provider.hasKnowledge(stack)) {
                        provider.addKnowledge(stack);
                    }

                    EntityPlayer player = externalStorage.getWorld().getPlayerEntityByUUID(externalStorage.getOwner());

                    if (player != null) {
                        provider.sync((EntityPlayerMP) player);
                    }
                }

                return null;
            }
        }

        return actualStack;
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        if (externalStorage.getOwner() != null) {
            IKnowledgeProvider provider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(externalStorage.getOwner());

            // @todo: https://github.com/sinkillerj/ProjectE/issues/1591
            if (!provider.getClass().getName().equals("moze_intel.projecte.impl.TransmutationOffline$1") && provider.hasKnowledge(stack)) {
                double singleEmc = ProjectEAPI.getEMCProxy().getValue(stack);

                int maxExtract = (int) Math.floor(provider.getEmc() / singleEmc);

                if (size > maxExtract) {
                    size = maxExtract;
                }

                if (size <= 0) {
                    return null;
                }

                ItemStack result = ItemHandlerHelper.copyStackWithSize(stack, size);

                if (!simulate) {
                    provider.setEmc(provider.getEmc() - (singleEmc * size));

                    EntityPlayer player = externalStorage.getWorld().getPlayerEntityByUUID(externalStorage.getOwner());

                    if (player != null) {
                        provider.sync((EntityPlayerMP) player);
                    }
                }

                return result;
            }
        }

        return null;
    }

    @Override
    public int getStored() {
        return 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return externalStorage.getAccessType();
    }
}
