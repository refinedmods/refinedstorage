package refinedstorage.tile.data;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.gui.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.tile.ClientNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RefinedStorageSerializers {
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

    public static final DataSerializer<List<ICraftingMonitorElement>> CLIENT_CRAFTING_TASK_SERIALIZER = new DataSerializer<List<ICraftingMonitorElement>>() {
        @Override
        public void write(PacketBuffer buf, List<ICraftingMonitorElement> tasks) {
            buf.writeInt(tasks.size());

            for (ICraftingMonitorElement task : tasks) {
                buf.writeInt(task.getType());

                task.write(buf);
            }
        }

        @Override
        public List<ICraftingMonitorElement> read(PacketBuffer buf) {
            List<ICraftingMonitorElement> tasks = new ArrayList<>();

            int size = buf.readInt();

            for (int i = 0; i < size; ++i) {
                int type = buf.readInt();

                if (ICraftingMonitorElement.REGISTRY.containsKey(type)) {
                    tasks.add(ICraftingMonitorElement.REGISTRY.get(type).apply(buf));
                }
            }

            return tasks;
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
}
