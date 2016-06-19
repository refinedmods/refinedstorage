package refinedstorage.api.storagenet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class StorageNetworkSavedData extends WorldSavedData {
    public static final String NBT_STORAGE_NETWORKS = "StorageNetworks";

    public StorageNetworkSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList networks = tag.getTagList(NBT_STORAGE_NETWORKS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < networks.tagCount(); ++i) {
            NBTTagCompound networkTag = networks.getCompoundTagAt(i);

            BlockPos pos = new BlockPos(networkTag.getInteger("X"), networkTag.getInteger("Y"), networkTag.getInteger("Z"));

            StorageNetwork network = new StorageNetwork(pos);
            network.readFromNBT(networkTag.getCompoundTag("Data"));

            StorageNetworkRegistry.add(network, networkTag.getInteger("Dim"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList networks = new NBTTagList();

        for (Map.Entry<Integer, Map<BlockPos, StorageNetwork>> entry : StorageNetworkRegistry.NETWORKS.entrySet()) {
            for (StorageNetwork network : entry.getValue().values()) {
                NBTTagCompound networkTag = new NBTTagCompound();
                networkTag.setInteger("X", network.getPos().getX());
                networkTag.setInteger("Y", network.getPos().getY());
                networkTag.setInteger("Z", network.getPos().getZ());
                networkTag.setInteger("Dim", entry.getKey());
                networkTag.setTag("Data", network.writeToNBT(new NBTTagCompound()));
                networks.appendTag(networkTag);
            }
        }

        tag.setTag(NBT_STORAGE_NETWORKS, networks);

        return tag;
    }

    public static StorageNetworkSavedData get(World world) {
        StorageNetworkSavedData instance = (StorageNetworkSavedData) world.getMapStorage().getOrLoadData(StorageNetworkSavedData.class, "RSStorageNetworks");

        if (instance == null) {
            instance = new StorageNetworkSavedData("RSStorageNetworks");
            world.getMapStorage().setData("RSStorageNetworks", instance);
        }

        return instance;
    }
}
