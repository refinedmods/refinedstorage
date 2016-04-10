package refinedstorage.tile.autocrafting;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.tile.TileMachine;

import java.util.ArrayList;
import java.util.List;

public class TileCraftingMonitor extends TileMachine {
    private List<ItemStack> tasks = new ArrayList<ItemStack>();

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

            for (CraftingTask task : controller.getCraftingTasks()) {
                ByteBufUtils.writeItemStack(buf, task.getResult());
            }
        } else {
            buf.writeInt(0);
        }
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        List<ItemStack> crafting = new ArrayList<ItemStack>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            crafting.add(ByteBufUtils.readItemStack(buf));
        }

        tasks = crafting;
    }

    public List<ItemStack> getTasks() {
        return tasks;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCraftingMonitor.class;
    }
}
