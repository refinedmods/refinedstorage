package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.tile.TileController;

import javax.annotation.Nullable;

public abstract class CraftingTask implements ICraftingTask {
    private static final String NBT_CHILD = "Child";

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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (child != null) {
            tag.setTag(NBT_CHILD, child.writeToNBT(new NBTTagCompound()));
        }

        return tag;
    }

    public void readChildNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_CHILD)) {
            child = TileController.readCraftingTask(tag.getCompoundTag(NBT_CHILD));
        }
    }
}
