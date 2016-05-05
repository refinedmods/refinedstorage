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
    private List<ItemStack> tasks = new ArrayList<ItemStack>();
    private String[] info = new String[0];

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

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
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        int size = buf.readInt();

        List<ItemStack> newTasks = new ArrayList<ItemStack>();
        String[] newInfo = new String[size];

        for (int i = 0; i < size; ++i) {
            newInfo[i] = ByteBufUtils.readUTF8String(buf);

            int outputSize = buf.readInt();

            for (int j = 0; j < outputSize; ++j) {
                newTasks.add(ByteBufUtils.readItemStack(buf));
            }
        }

        tasks = newTasks;
        info = newInfo;
    }

    public List<ItemStack> getTasks() {
        return tasks;
    }

    public String[] getInfo() {
        return info;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCraftingMonitor.class;
    }
}
