package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeFactory;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkNodeManager extends WorldSavedData implements INetworkNodeManager {
    public static final String NAME = "refinedstorage_nodes";

    private static final String NBT_NODES = "Nodes";
    private static final String NBT_NODE_ID = "Id";
    private static final String NBT_NODE_DATA = "Data";
    private static final String NBT_NODE_POS = "Pos";

    private boolean canReadNodes;
    private NBTTagList nodesTag;

    private ConcurrentHashMap<BlockPos, INetworkNode> nodes = new ConcurrentHashMap<>();

    public NetworkNodeManager(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_NODES)) {
            this.nodesTag = tag.getTagList(NBT_NODES, Constants.NBT.TAG_COMPOUND);
            this.canReadNodes = true;
        }
    }

    public void tryReadNodes(World world) {
        if (this.canReadNodes) {
            this.canReadNodes = false;

            this.nodes.clear();

            for (int i = 0; i < nodesTag.tagCount(); ++i) {
                NBTTagCompound nodeTag = nodesTag.getCompoundTagAt(i);

                String id = nodeTag.getString(NBT_NODE_ID);
                NBTTagCompound data = nodeTag.getCompoundTag(NBT_NODE_DATA);
                BlockPos pos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS));

                INetworkNodeFactory factory = API.instance().getNetworkNodeRegistry().get(id);

                if (factory != null) {
                    INetworkNode node = null;

                    try {
                        node = factory.create(data, world, pos);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                    if (node != null) {
                        this.nodes.put(pos, node);
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (INetworkNode node : all()) {
            try {
                NBTTagCompound nodeTag = new NBTTagCompound();

                nodeTag.setString(NBT_NODE_ID, node.getId());
                nodeTag.setLong(NBT_NODE_POS, node.getPos().toLong());
                nodeTag.setTag(NBT_NODE_DATA, node.write(new NBTTagCompound()));

                list.appendTag(nodeTag);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        tag.setTag(NBT_NODES, list);

        return tag;
    }

    @Nullable
    @Override
    public INetworkNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    @Override
    public void removeNode(BlockPos pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        nodes.remove(pos);
    }

    @Override
    public void setNode(BlockPos pos, INetworkNode node) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        nodes.put(pos, node);
    }

    @Override
    public Collection<INetworkNode> all() {
        return nodes.values();
    }

    @Override
    public void markForSaving() {
        markDirty();
    }
}
