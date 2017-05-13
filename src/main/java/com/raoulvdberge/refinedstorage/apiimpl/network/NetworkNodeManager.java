package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NetworkNodeManager extends WorldSavedData implements INetworkNodeManager {
    private static final String NAME = "refinedstorage_nodes";

    private static final String NBT_NODES = "Nodes";
    private static final String NBT_NODE_ID = "Id";
    private static final String NBT_NODE_DATA = "Data";
    private static final String NBT_NODE_POS = "Pos";

    private Map<BlockPos, INetworkNode> nodes = new HashMap<>();

    public NetworkNodeManager(String s) {
        super(s);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        System.out.println("[RS DEBUG] Reading network nodes, first clearing data...");

        clear();

        NBTTagList list = tag.getTagList(NBT_NODES, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nodeTag = list.getCompoundTagAt(i);

            String id = nodeTag.getString(NBT_NODE_ID);
            NBTTagCompound data = nodeTag.getCompoundTag(NBT_NODE_DATA);
            BlockPos pos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS));

            Function<NBTTagCompound, INetworkNode> factory = API.instance().getNetworkNodeRegistry().get(id);

            if (factory != null) {
                setNode(pos, factory.apply(data));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        System.out.println("[RS DEBUG] Writing network nodes");

        NBTTagList list = new NBTTagList();

        for (INetworkNode node : all()) {
            NBTTagCompound nodeTag = new NBTTagCompound();

            nodeTag.setString(NBT_NODE_ID, node.getId());
            nodeTag.setLong(NBT_NODE_POS, node.getPos().toLong());
            nodeTag.setTag(NBT_NODE_DATA, node.write(new NBTTagCompound()));

            list.appendTag(nodeTag);
        }

        tag.setTag(NBT_NODES, list);

        return tag;
    }

    public static NetworkNodeManager getManager(World world) {
        MapStorage storage = world.getPerWorldStorage();
        NetworkNodeManager instance = (NetworkNodeManager) storage.getOrLoadData(NetworkNodeManager.class, NAME);

        if (instance == null) {
            System.out.println("[RS DEBUG] Initializing Network Node Manager for " + world.provider.getDimension());
            instance = new NetworkNodeManager(NAME);

            storage.setData(NAME, instance);
        } else {
            System.out.println("[RS DEBUG] Network Node Manager for " + world.provider.getDimension() + " already exists, OK...");
        }

        return instance;
    }

    @Nullable
    @Override
    public INetworkNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    @Override
    public void removeNode(BlockPos pos) {
        System.out.println("[RS DEBUG] Removing node at " + pos);

        nodes.remove(pos);
    }

    @Override
    public void setNode(BlockPos pos, INetworkNode node) {
        nodes.put(pos, node);
    }

    @Override
    public Collection<INetworkNode> all() {
        return nodes.values();
    }

    @Override
    public void clear() {
        System.out.println("[RS DEBUG] Clearing data!");
        nodes.clear();
    }

    @Override
    public void markForSaving() {
        markDirty();
    }
}
