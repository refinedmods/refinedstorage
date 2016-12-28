package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ReaderWriterHandlerForgeEnergy implements IReaderWriterHandler {
    public static final String ID = "forgeenergy";

    private static final String NBT_ENERGY_STORED = "EnergyStored";

    private EnergyStorage storage;
    private EnergyStorageReaderWriter storageReader, storageWriter;

    public ReaderWriterHandlerForgeEnergy(@Nullable NBTTagCompound tag) {
        this.storage = new EnergyStorage(4000);
        this.storageReader = new EnergyStorageReaderWriter(storage, false, true);
        this.storageWriter = new EnergyStorageReaderWriter(storage, true, false);

        if (tag != null && tag.hasKey(NBT_ENERGY_STORED)) {
            storage.receiveEnergy(tag.getInteger(NBT_ENERGY_STORED), false);
        }
    }

    @Override
    public void update(IReaderWriterChannel channel) {
        // NO OP
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        // NO OP
    }

    @Override
    public boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability) {
        return capability == CapabilityEnergy.ENERGY && (readerWriter instanceof IReader || readerWriter instanceof IWriter);
    }

    @Override
    public <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (readerWriter instanceof IReader) {
                return CapabilityEnergy.ENERGY.cast(storageReader);
            } else if (readerWriter instanceof IWriter) {
                return CapabilityEnergy.ENERGY.cast(storageWriter);
            }
        }

        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger(NBT_ENERGY_STORED, storage.getEnergyStored());

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    private class EnergyStorageReaderWriter implements IEnergyStorage {
        private IEnergyStorage parent;
        private boolean canExtract, canReceive;

        public EnergyStorageReaderWriter(IEnergyStorage parent, boolean canExtract, boolean canReceive) {
            this.canExtract = canExtract;
            this.canReceive = canReceive;
            this.parent = parent;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return canReceive ? parent.receiveEnergy(maxReceive, simulate) : 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return canExtract ? parent.extractEnergy(maxExtract, simulate) : 0;
        }

        @Override
        public int getEnergyStored() {
            return parent.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return parent.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return canExtract;
        }

        @Override
        public boolean canReceive() {
            return canReceive;
        }
    }
}
