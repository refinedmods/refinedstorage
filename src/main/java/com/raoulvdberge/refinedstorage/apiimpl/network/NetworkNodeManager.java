package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NetworkNodeManager extends WorldSavedData implements INetworkNodeManager {
    public static final String NAME = "refinedstorage_nodes";

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
        clear();

        if (tag.hasKey(NBT_NODES)) {
            NBTTagList list = tag.getTagList(NBT_NODES, Constants.NBT.TAG_COMPOUND);

            int toRead = list.tagCount();

            RSUtils.debugLog("Reading " + toRead + " nodes...");

            int read = 0;

            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound nodeTag = list.getCompoundTagAt(i);

                String id = nodeTag.getString(NBT_NODE_ID);
                NBTTagCompound data = nodeTag.getCompoundTag(NBT_NODE_DATA);
                BlockPos pos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS));

                Function<NBTTagCompound, INetworkNode> factory = API.instance().getNetworkNodeRegistry().get(id);

                if (factory != null) {
                    setNode(pos, factory.apply(data));

                    RSUtils.debugLog("Node at " + pos + " read... (" + (++read) + "/" + toRead + ")");
                } else {
                    RSUtils.debugLog("Factory for node at " + pos + " is null (id: " + id + ")");
                }
            }

            RSUtils.debugLog("Read " + read + " nodes out of " + toRead + " to read");
        } else {
            RSUtils.debugLog("Cannot read nodes, as there is no 'nodes' tag on this WorldSavedData");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        int toWrite = all().size();

        RSUtils.debugLog("Writing " + toWrite + " nodes...");

        NBTTagList list = new NBTTagList();

        int written = 0;

        for (INetworkNode node : all()) {
            NBTTagCompound nodeTag = new NBTTagCompound();

            nodeTag.setString(NBT_NODE_ID, node.getId());
            nodeTag.setLong(NBT_NODE_POS, node.getPos().toLong());
            nodeTag.setTag(NBT_NODE_DATA, node.write(new NBTTagCompound()));

            RSUtils.debugLog("Node at " + node.getPos() + " written... (" + (++written) + "/" + toWrite + ")");

            list.appendTag(nodeTag);
        }

        tag.setTag(NBT_NODES, list);

        RSUtils.debugLog("Wrote " + written + " nodes out of " + toWrite + " to write");

        return tag;
    }

    @Nullable
    @Override
    public INetworkNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    @Override
    public void removeNode(BlockPos pos) {
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
        RSUtils.debugLog("Clearing all nodes!");

        nodes.clear();
    }

    @Override
    public void markForSaving() {
        markDirty();
    }
}
