package refinedstorage.tile.data;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
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
        public List<ClientNode> read(PacketBuffer buf) throws IOException {
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
                ByteBufUtils.writeUTF8String(buf, task.getInfo());

                buf.writeInt(task.getOutputs().length);

                for (ItemStack output : task.getOutputs()) {
                    ByteBufUtils.writeItemStack(buf, output);
                }
            }
        }

        @Override
        public List<ClientCraftingTask> read(PacketBuffer buf) throws IOException {
            int size = buf.readInt();

            List<ClientCraftingTask> tasks = new ArrayList<>();

            for (int i = 0; i < size; ++i) {
                String info = ByteBufUtils.readUTF8String(buf);

                int outputs = buf.readInt();

                for (int j = 0; j < outputs; ++j) {
                    tasks.add(new ClientCraftingTask(ByteBufUtils.readItemStack(buf), i, info));
                }
            }

            Collections.reverse(tasks);

            return tasks;
        }

        @Override
        public DataParameter<List<ClientCraftingTask>> createKey(int id) {
            return null;
        }
    };
}
