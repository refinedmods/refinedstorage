package com.refinedmods.refinedstorage.blockentity.data;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.blockentity.ClientNode;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public final class RSSerializers {
    public static final EntityDataSerializer<List<ClientNode>> CLIENT_NODE_SERIALIZER = new EntityDataSerializer<List<ClientNode>>() {
        @Override
        public void write(FriendlyByteBuf buf, List<ClientNode> nodes) {
            buf.writeInt(nodes.size());

            for (ClientNode node : nodes) {
                buf.writeItem(node.getStack());
                buf.writeInt(node.getAmount());
                buf.writeInt(node.getEnergyUsage());
            }
        }

        @Override
        public List<ClientNode> read(FriendlyByteBuf buf) {
            List<ClientNode> nodes = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                nodes.add(new ClientNode(buf.readItem(), buf.readInt(), buf.readInt()));
            }

            return nodes;
        }

        @Override
        public EntityDataAccessor<List<ClientNode>> createAccessor(int id) {
            return null;
        }

        @Override
        public List<ClientNode> copy(List<ClientNode> value) {
            return value;
        }
    };
    public static final EntityDataSerializer<FluidStack> FLUID_STACK_SERIALIZER = new EntityDataSerializer<FluidStack>() {
        @Override
        public void write(FriendlyByteBuf buf, FluidStack value) {
            value.writeToPacket(buf);
        }

        @Override
        public FluidStack read(FriendlyByteBuf buf) {
            return FluidStack.readFromPacket(buf);
        }

        @Override
        public EntityDataAccessor<FluidStack> createAccessor(int id) {
            return null;
        }

        @Override
        public FluidStack copy(FluidStack value) {
            return value;
        }
    };
    public static final EntityDataSerializer<AccessType> ACCESS_TYPE_SERIALIZER = new EntityDataSerializer<AccessType>() {
        @Override
        public void write(FriendlyByteBuf buf, AccessType value) {
            buf.writeInt(value.getId());
        }

        @Override
        public AccessType read(FriendlyByteBuf buf) {
            return AccessTypeUtils.getAccessType(buf.readInt());
        }

        @Override
        public EntityDataAccessor<AccessType> createAccessor(int id) {
            return null;
        }

        @Override
        public AccessType copy(AccessType value) {
            return value;
        }
    };
    public static final EntityDataSerializer<Long> LONG_SERIALIZER = new EntityDataSerializer<Long>() {
        @Override
        public void write(FriendlyByteBuf buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long read(FriendlyByteBuf buf) {
            return buf.readLong();
        }

        @Override
        public EntityDataAccessor<Long> createAccessor(int id) {
            return null;
        }

        @Override
        public Long copy(Long value) {
            return value;
        }
    };
    public static final EntityDataSerializer<Optional<ResourceLocation>> OPTIONAL_RESOURCE_LOCATION_SERIALIZER = new EntityDataSerializer<Optional<ResourceLocation>>() {
        @Override
        public void write(FriendlyByteBuf buf, Optional<ResourceLocation> value) {
            buf.writeBoolean(value.isPresent());

            value.ifPresent(buf::writeResourceLocation);
        }

        @Override
        public Optional<ResourceLocation> read(FriendlyByteBuf buf) {
            if (!buf.readBoolean()) {
                return Optional.empty();
            }

            return Optional.of(buf.readResourceLocation());
        }

        @Override
        public EntityDataAccessor<Optional<ResourceLocation>> createAccessor(int id) {
            return null;
        }

        @Override
        public Optional<ResourceLocation> copy(Optional<ResourceLocation> value) {
            return value;
        }
    };
    public static final EntityDataSerializer<List<Set<ResourceLocation>>> LIST_OF_SET_SERIALIZER = new EntityDataSerializer<List<Set<ResourceLocation>>>() {
        @Override
        public void write(FriendlyByteBuf buf, List<Set<ResourceLocation>> value) {
            buf.writeInt(value.size());

            for (Set<ResourceLocation> values : value) {
                buf.writeInt(values.size());

                values.forEach(buf::writeResourceLocation);
            }
        }

        @Override
        public List<Set<ResourceLocation>> read(FriendlyByteBuf buf) {
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
        public EntityDataAccessor<List<Set<ResourceLocation>>> createAccessor(int id) {
            return null;
        }

        @Override
        public List<Set<ResourceLocation>> copy(List<Set<ResourceLocation>> value) {
            return value;
        }
    };

    private RSSerializers() {
    }
}
