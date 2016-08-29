package refinedstorage.apiimpl.autocrafting.task;

import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;

import javax.annotation.Nullable;

public abstract class CraftingTask implements ICraftingTask {
    protected ICraftingTask child;

    @Override
    @Nullable
    public ICraftingTask getChild() {
        return child;
    }

    @Override
    public void setChild(@Nullable ICraftingTask child) {
        this.child = child;
    }

    @Override
    public void onCancelled(INetworkMaster network) {
        if (child != null) {
            child.onCancelled(network);
        }
    }
}
