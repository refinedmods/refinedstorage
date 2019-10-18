package com.raoulvdberge.refinedstorage.tile.data;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.tile.ClientNode;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RSSerializers {
    public static final IDataSerializer<List<ClientNode>> CLIENT_NODE_SERIALIZER = new IDataSerializer<List<ClientNode>>() {
        @Override
        public void write(PacketBuffer buf, List<ClientNode> nodes) {
            buf.writeInt(nodes.size());

            for (ClientNode node : nodes) {
                buf.writeItemStack(node.getStack());
                buf.writeInt(node.getAmount());
                buf.writeInt(node.getEnergyUsage());
            }
        }

        @Override
        public List<ClientNode> read(PacketBuffer buf) {
            List<ClientNode> nodes = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                nodes.add(new ClientNode(buf.readItemStack(), buf.readInt(), buf.readInt()));
            }

            return nodes;
        }

        @Override
        public DataParameter<List<ClientNode>> createKey(int id) {
            return null;
        }

        @Override
        public List<ClientNode> copyValue(List<ClientNode> value) {
            return value;
        }
    };

    public static final IDataSerializer<FluidStack> FLUID_STACK_SERIALIZER = new IDataSerializer<FluidStack>() {
        @Override
        public void write(PacketBuffer buf, FluidStack value) {
            value.writeToPacket(buf);
        }

        @Override
        public FluidStack read(PacketBuffer buf) {
            return FluidStack.readFromPacket(buf);
        }

        @Override
        public DataParameter<FluidStack> createKey(int id) {
            return null;
        }

        @Override
        public FluidStack copyValue(FluidStack value) {
            return value;
        }
    };

    public static final IDataSerializer<AccessType> ACCESS_TYPE_SERIALIZER = new IDataSerializer<AccessType>() {
        @Override
        public void write(PacketBuffer buf, AccessType value) {
            buf.writeInt(value.getId());
        }

        @Override
        public AccessType read(PacketBuffer buf) {
            return AccessTypeUtils.getAccessType(buf.readInt());
        }

        @Override
        public DataParameter<AccessType> createKey(int id) {
            return null;
        }

        @Override
        public AccessType copyValue(AccessType value) {
            return value;
        }
    };

    public static final IDataSerializer<Long> LONG_SERIALIZER = new IDataSerializer<Long>() {
        @Override
        public void write(PacketBuffer buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long read(PacketBuffer buf) {
            return buf.readLong();
        }

        @Override
        public DataParameter<Long> createKey(int id) {
            return null;
        }

        @Override
        public Long copyValue(Long value) {
            return value;
        }
    };

    public static final IDataSerializer<Optional<ResourceLocation>> OPTIONAL_RESOURCE_LOCATION_SERIALIZER = new IDataSerializer<Optional<ResourceLocation>>() {
        @Override
        public void write(PacketBuffer buf, Optional<ResourceLocation> value) {
            buf.writeBoolean(value.isPresent());

            value.ifPresent(buf::writeResourceLocation);
        }

        @Override
        public Optional<ResourceLocation> read(PacketBuffer buf) {
            if (!buf.readBoolean()) {
                return Optional.empty();
            }

            return Optional.of(buf.readResourceLocation());
        }

        @Override
        public DataParameter<Optional<ResourceLocation>> createKey(int id) {
            return null;
        }

        @Override
        public Optional<ResourceLocation> copyValue(Optional<ResourceLocation> value) {
            return value;
        }
    };
}
