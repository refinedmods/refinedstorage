package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.world.World;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;

import java.util.Iterator;
import java.util.List;

public abstract class CraftingTask implements ICraftingTask {
    protected List<ICraftingTask> children;

    @Override
    public List<ICraftingTask> getChildren() {
        return children;
    }

    public void updateChildren(World world, INetworkMaster network) {
        Iterator<ICraftingTask> childrenIterator = children.iterator();

        while (childrenIterator.hasNext()) {
            if (childrenIterator.next().update(world, network)) {
                childrenIterator.remove();
            }
        }
    }

    @Override
    public void onCancelled(INetworkMaster network) {
        for (ICraftingTask child : children) {
            child.onCancelled(network);
        }
    }
}
