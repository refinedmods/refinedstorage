package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.container.ContainerCraftingMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileCraftingMonitor extends TileMachine {
    private List<ClientSideCraftingTask> tasks = new ArrayList<ClientSideCraftingTask>();
    private int selected = -1;
    private List<Object> info = new ArrayList<Object>();

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
                buf.writeInt(task.getPattern().getOutputs().length);

                for (ItemStack output : task.getPattern().getOutputs()) {
                    ByteBufUtils.writeItemStack(buf, output);
                }
            }
        } else {
            buf.writeInt(0);
        }

        buf.writeInt(info.size());

        for (Object item : info) {
            if (item instanceof String) {
                buf.writeInt(0);
                ByteBufUtils.writeUTF8String(buf, (String) item);
            } else if (item instanceof ItemStack) {
                buf.writeInt(1);
                ByteBufUtils.writeItemStack(buf, (ItemStack) item);
            }
        }
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        int size = buf.readInt();

        List<ClientSideCraftingTask> newTasks = new ArrayList<ClientSideCraftingTask>();

        for (int i = 0; i < size; ++i) {
            int outputs = buf.readInt();

            for (int j = 0; j < outputs; ++j) {
                newTasks.add(new ClientSideCraftingTask(ByteBufUtils.readItemStack(buf), i));
            }
        }

        Collections.reverse(newTasks);

        tasks = newTasks;

        List<Object> newInfo = new ArrayList<Object>();

        size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            int type = buf.readInt();

            if (type == 0) {
                newInfo.add(ByteBufUtils.readUTF8String(buf));
            } else if (type == 1) {
                newInfo.add(ByteBufUtils.readItemStack(buf));
            }
        }

        info = newInfo;
    }

    public List<ClientSideCraftingTask> getTasks() {
        return tasks;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;

        if (!worldObj.isRemote) {
            if (selected != -1) {
                info = controller.getCraftingTasks().get(selected).getInfo();
            } else {
                info.clear();
            }
        }
    }

    public boolean hasSelection() {
        return selected != -1;
    }

    public List<Object> getInfo() {
        return info;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCraftingMonitor.class;
    }

    public class ClientSideCraftingTask {
        public ItemStack output;
        public int id;

        public ClientSideCraftingTask(ItemStack output, int id) {
            this.output = output;
            this.id = id;
        }
    }
}
