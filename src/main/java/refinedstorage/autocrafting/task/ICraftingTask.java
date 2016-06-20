package refinedstorage.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.storagenet.NetworkMaster;
import refinedstorage.autocrafting.CraftingPattern;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(NetworkMaster master);

    void onDone(NetworkMaster master);

    void onCancelled(NetworkMaster master);

    void writeToNBT(NBTTagCompound tag);

    String getInfo();
}
