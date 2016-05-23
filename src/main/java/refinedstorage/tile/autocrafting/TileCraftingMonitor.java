package refinedstorage.tile.autocrafting;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.autocrafting.task.ICraftingTask;

import java.util.ArrayList;
import java.util.List;

public class TileCraftingMonitor extends TileMachine {
    public class ClientSideCraftingTask {
        public ItemStack output;
        public int id;
        public String info;
    }

    private List<ClientSideCraftingTask> tasks = new ArrayList<ClientSideCraftingTask>();

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        if (connected) {
            buf.writeInt(controller.getCraftingTasks().size());

            for (ICraftingTask task : controller.getCraftingTasks()) {
                ByteBufUtils.writeUTF8String(buf, task.getInfo());

                buf.writeInt(task.getPattern().getOutputs().length);

                for (ItemStack output : task.getPattern().getOutputs()) {
                    ByteBufUtils.writeItemStack(buf, output);
                }
            }
        } else {
            buf.writeInt(0);
        }
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        int size = buf.readInt();

        List<ClientSideCraftingTask> newTasks = new ArrayList<ClientSideCraftingTask>();

        for (int i = 0; i < size; ++i) {
            String info = ByteBufUtils.readUTF8String(buf);

            int outputs = buf.readInt();

            for (int j = 0; j < outputs; ++j) {
                ClientSideCraftingTask task = new ClientSideCraftingTask();

                task.info = info;
                task.output = ByteBufUtils.readItemStack(buf);
                task.id = i;

                newTasks.add(task);
            }
        }

        tasks = newTasks;
    }

    public List<ClientSideCraftingTask> getTasks() {
        return tasks;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCraftingMonitor.class;
    }
}
