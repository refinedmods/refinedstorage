package refinedstorage.apiimpl.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import refinedstorage.api.network.INetworkMaster;

import java.util.Map;

public class NetworkMasterSavedData extends WorldSavedData {
    public static final String ID = "RSNetworks";
    public static final String NBT_NETWORKS = "Networks";
    public static final String NBT_NETWORK_X = "X";
    public static final String NBT_NETWORK_Y = "Y";
    public static final String NBT_NETWORK_Z = "Z";
    public static final String NBT_NETWORK_DIM = "Dim";
    public static final String NBT_NETWORK_DATA = "Data";

    public NetworkMasterSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList networks = tag.getTagList(NBT_NETWORKS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < networks.tagCount(); ++i) {
            NBTTagCompound networkTag = networks.getCompoundTagAt(i);

            INetworkMaster network = new NetworkMaster(new BlockPos(networkTag.getInteger(NBT_NETWORK_X), networkTag.getInteger(NBT_NETWORK_Y), networkTag.getInteger(NBT_NETWORK_Z)));

            network.readFromNBT(networkTag.getCompoundTag(NBT_NETWORK_DATA));

            NetworkMasterRegistry.add(networkTag.getInteger(NBT_NETWORK_DIM), network);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList networks = new NBTTagList();

        for (Map.Entry<Integer, Map<BlockPos, INetworkMaster>> entry : NetworkMasterRegistry.NETWORKS.entrySet()) {
            for (INetworkMaster network : entry.getValue().values()) {
                NBTTagCompound networkTag = new NBTTagCompound();

                networkTag.setInteger(NBT_NETWORK_X, network.getPosition().getX());
                networkTag.setInteger(NBT_NETWORK_Y, network.getPosition().getY());
                networkTag.setInteger(NBT_NETWORK_Z, network.getPosition().getZ());
                networkTag.setInteger(NBT_NETWORK_DIM, entry.getKey());

                networkTag.setTag(NBT_NETWORK_DATA, network.writeToNBT(new NBTTagCompound()));

                networks.appendTag(networkTag);
            }
        }

        tag.setTag(NBT_NETWORKS, networks);

        return tag;
    }

    public static NetworkMasterSavedData getOrLoad(World world) {
        NetworkMasterSavedData instance = (NetworkMasterSavedData) world.getMapStorage().getOrLoadData(NetworkMasterSavedData.class, ID);

        if (instance == null) {
            instance = new NetworkMasterSavedData(ID);

            world.getMapStorage().setData(ID, instance);
        }

        return instance;
    }
}
