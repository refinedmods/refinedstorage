package com.refinedmods.refinedstorage.tile.data;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.tile.ClientNode;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public final class RSSerializers {
    private RSSerializers() {
    }

    public static final IDataSerializer<List<ClientNode>> CLIENT_NODE_SERIALIZER = new IDataSerializer<List<ClientNode>>() {
        @Override
        public void write(PacketBuffer buf, List<ClientNode> nodes) {
            buf.writeInt(nodes.size());

            for (ClientNode node : nodes) {
                buf.writeItem(node.getStack());
                buf.writeInt(node.getAmount());
                buf.writeInt(node.getEnergyUsage());
            }
        }

        @Override
        public List<ClientNode> read(PacketBuffer buf) {
            List<ClientNode> nodes = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                nodes.add(new ClientNode(buf.readItem(), buf.readInt(), buf.readInt()));
            }

            return nodes;
        }

        @Override
        public DataParameter<List<ClientNode>> createAccessor(int id) {
            return null;
        }

        @Override
        public List<ClientNode> copy(List<ClientNode> value) {
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
        public DataParameter<FluidStack> createAccessor(int id) {
            return null;
        }

        @Override
        public FluidStack copy(FluidStack value) {
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
        public DataParameter<AccessType> createAccessor(int id) {
            return null;
        }

        @Override
        public AccessType copy(AccessType value) {
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
        public DataParameter<Long> createAccessor(int id) {
            return null;
        }

        @Override
        public Long copy(Long value) {
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
        public DataParameter<Optional<ResourceLocation>> createAccessor(int id) {
            return null;
        }

        @Override
        public Optional<ResourceLocation> copy(Optional<ResourceLocation> value) {
            return value;
        }
    };

    public static final IDataSerializer<List<Set<ResourceLocation>>> LIST_OF_SET_SERIALIZER = new IDataSerializer<List<Set<ResourceLocation>>>() {
        @Override
        public void write(PacketBuffer buf, List<Set<ResourceLocation>> value) {
            buf.writeInt(value.size());

            for (Set<ResourceLocation> values : value) {
                buf.writeInt(values.size());

                values.forEach(buf::writeResourceLocation);
            }
        }

        @Override
        public List<Set<ResourceLocation>> read(PacketBuffer buf) {
            List<Set<ResourceLocation>> value = new ArrayList<>();

            int size = buf.readInt();
            for (int i = 0; i < size; ++i) {
                int setSize = buf.readInt();

                Set<ResourceLocation> values = new HashSet<>();

                for (int j = 0; j < setSize; ++j) {
                    values.add(buf.readResourceLocation());
                }

                value.add(values);
            }

            return value;
        }

        @Override
        public DataParameter<List<Set<ResourceLocation>>> createAccessor(int id) {
            return null;
        }

        @Override
        public List<Set<ResourceLocation>> copy(List<Set<ResourceLocation>> value) {
            return value;
        }
    };
}
