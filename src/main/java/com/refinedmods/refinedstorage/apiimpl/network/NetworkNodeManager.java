package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.util.RSSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkNodeManager extends RSSavedData implements INetworkNodeManager {
    public static final String NAME = "refinedstorage_nodes";

    private static final String NBT_NODES = "Nodes";
    private static final String NBT_NODE_ID = "Id";
    private static final String NBT_NODE_DATA = "Data";
    private static final String NBT_NODE_POS = "Pos";

    private final Level level;

    private final Logger logger = LogManager.getLogger(getClass());

    private final ConcurrentHashMap<BlockPos, INetworkNode> nodes = new ConcurrentHashMap<>();

    public NetworkNodeManager(Level level) {
        this.level = level;
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains(NBT_NODES)) {
            ListTag nodesTag = tag.getList(NBT_NODES, Tag.TAG_COMPOUND);

            this.nodes.clear();

            for (int i = 0; i < nodesTag.size(); ++i) {
                CompoundTag nodeTag = nodesTag.getCompound(i);

                ResourceLocation id = new ResourceLocation(nodeTag.getString(NBT_NODE_ID));
                CompoundTag data = nodeTag.getCompound(NBT_NODE_DATA);
                BlockPos pos = BlockPos.of(nodeTag.getLong(NBT_NODE_POS));

                INetworkNodeFactory factory = API.instance().getNetworkNodeRegistry().get(id);

                if (factory != null) {
                    INetworkNode node = null;

                    try {
                        node = factory.create(data, level, pos);
                    } catch (Throwable t) {
                        logger.error("Could not read network node", t);
                    }

                    if (node != null) {
                        this.nodes.put(pos, node);
                    }
                } else {
                    logger.warn("Factory for {} not found in network node registry", id);
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (INetworkNode node : all()) {
            try {
                CompoundTag nodeTag = new CompoundTag();

                nodeTag.putString(NBT_NODE_ID, node.getId().toString());
                nodeTag.putLong(NBT_NODE_POS, node.getPos().asLong());
                nodeTag.put(NBT_NODE_DATA, node.write(new CompoundTag()));

                list.add(nodeTag);
            } catch (Throwable t) {
                logger.error("Error while saving network node", t);
            }
        }

        tag.put(NBT_NODES, list);

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
        setDirty();
    }
}
