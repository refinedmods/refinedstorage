package refinedstorage.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.apiimpl.network.NetworkMaster;
import refinedstorage.autocrafting.CraftingPattern;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(NetworkMaster network);

    void onDone(NetworkMaster network);

    void onCancelled(NetworkMaster network);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}
