package refinedstorage.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.storagenet.StorageNetwork;
import refinedstorage.autocrafting.CraftingPattern;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(StorageNetwork network);

    void onDone(StorageNetwork network);

    void onCancelled(StorageNetwork network);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}
