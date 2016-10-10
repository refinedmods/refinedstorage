package refinedstorage.tile.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.storage.AccessType;
import refinedstorage.apiimpl.API;
import refinedstorage.tile.ClientNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RSSerializers {
    public static final DataSerializer<List<ClientNode>> CLIENT_NODE_SERIALIZER = new DataSerializer<List<ClientNode>>() {
        @Override
        public void write(PacketBuffer buf, List<ClientNode> nodes) {
            buf.writeInt(nodes.size());

            for (ClientNode node : nodes) {
                ByteBufUtils.writeItemStack(buf, node.getStack());
                buf.writeInt(node.getAmount());
                buf.writeInt(node.getEnergyUsage());
            }
        }

        @Override
        public List<ClientNode> read(PacketBuffer buf) {
            List<ClientNode> nodes = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                nodes.add(new ClientNode(ByteBufUtils.readItemStack(buf), buf.readInt(), buf.readInt()));
            }

            return nodes;
        }

        @Override
        public DataParameter<List<ClientNode>> createKey(int id) {
            return null;
        }
    };

    public static final DataSerializer<List<ICraftingMonitorElement>> CRAFTING_MONITOR_ELEMENT_SERIALIZER = new DataSerializer<List<ICraftingMonitorElement>>() {
        @Override
        public void write(PacketBuffer buf, List<ICraftingMonitorElement> elements) {
            buf.writeInt(elements.size());

            for (ICraftingMonitorElement task : elements) {
                ByteBufUtils.writeUTF8String(buf, task.getId());

                task.write(buf);
            }
        }

        @Override
        public List<ICraftingMonitorElement> read(PacketBuffer buf) {
            List<ICraftingMonitorElement> elements = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                Function<ByteBuf, ICraftingMonitorElement> factory = API.instance().getCraftingMonitorElementRegistry().getFactory(ByteBufUtils.readUTF8String(buf));

                if (factory != null) {
                    elements.add(factory.apply(buf));
                }
            }

            return elements;
        }

        @Override
        public DataParameter<List<ICraftingMonitorElement>> createKey(int id) {
            return null;
        }
    };

    public static final DataSerializer<FluidStack> FLUID_STACK_SERIALIZER = new DataSerializer<FluidStack>() {
        @Override
        public void write(PacketBuffer buf, FluidStack value) {
            if (value == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(value));
                buf.writeInt(value.amount);
                buf.writeNBTTagCompoundToBuffer(value.tag);
            }
        }

        @Override
        public FluidStack read(PacketBuffer buf) {
            try {
                if (buf.readBoolean()) {
                    return new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), buf.readNBTTagCompoundFromBuffer());
                }
            } catch (IOException e) {
                // NO OP
            }

            return null;
        }

        @Override
        public DataParameter<FluidStack> createKey(int id) {
            return null;
        }
    };

    public static final DataSerializer<AccessType> ACCESS_TYPE_SERIALIZER = new DataSerializer<AccessType>() {
        @Override
        public void write(PacketBuffer buf, AccessType value) {
            buf.writeInt(value.getId());
        }

        @Override
        public AccessType read(PacketBuffer buf) throws IOException {
            return RSUtils.getAccessType(buf.readInt());
        }

        @Override
        public DataParameter<AccessType> createKey(int id) {
            return null;
        }
    };
}
