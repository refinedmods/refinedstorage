package refinedstorage.tile.data;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.tile.ClientCraftingTask;
import refinedstorage.tile.ClientNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    public static final DataSerializer<List<ClientCraftingTask>> CLIENT_CRAFTING_TASK_SERIALIZER = new DataSerializer<List<ClientCraftingTask>>() {
        @Override
        public void write(PacketBuffer buf, List<ClientCraftingTask> tasks) {
            buf.writeInt(tasks.size());

            for (ClientCraftingTask task : tasks) {
                writeTask(buf, task);
            }
        }

        private void writeTask(PacketBuffer buf, ClientCraftingTask task) {
            ByteBufUtils.writeUTF8String(buf, task.getStatus());

            buf.writeInt(task.getOutputs().length);

            for (ItemStack output : task.getOutputs()) {
                ByteBufUtils.writeItemStack(buf, output);
            }

            buf.writeBoolean(task.getChild() != null);

            if (task.getChild() != null) {
                writeTask(buf, task.getChild());
            }
        }

        @Override
        public List<ClientCraftingTask> read(PacketBuffer buf) {
            int size = buf.readInt();

            List<ClientCraftingTask> tasks = new ArrayList<>();

            for (int i = 0; i < size; ++i) {
                readTask(buf, i, 0, tasks);
            }

            Collections.reverse(tasks);

            return tasks;
        }

        private void readTask(PacketBuffer buf, int i, int depth, List<ClientCraftingTask> tasks) {
            String status = ByteBufUtils.readUTF8String(buf);

            int outputs = buf.readInt();

            for (int j = 0; j < outputs; ++j) {
                tasks.add(new ClientCraftingTask(ByteBufUtils.readItemStack(buf), i, status, depth));
            }

            if (buf.readBoolean()) {
                readTask(buf, i, depth + 1, tasks);
            }
        }

        @Override
        public DataParameter<List<ClientCraftingTask>> createKey(int id) {
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
        public FluidStack read(PacketBuffer buf) throws IOException {
            if (buf.readBoolean()) {
                return new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), buf.readNBTTagCompoundFromBuffer());
            }

            return null;
        }

        @Override
        public DataParameter<FluidStack> createKey(int id) {
            return null;
        }
    };
}
