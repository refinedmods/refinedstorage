package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.util.RSWorldSavedData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkNodeManager extends RSWorldSavedData implements INetworkNodeManager {
    public static final String NAME = "refinedstorage_nodes";

    private static final String NBT_NODES = "Nodes";
    private static final String NBT_NODE_ID = "Id";
    private static final String NBT_NODE_DATA = "Data";
    private static final String NBT_NODE_POS = "Pos";

    private final World world;

    private final Logger logger = LogManager.getLogger(getClass());

    private final ConcurrentHashMap<BlockPos, INetworkNode> nodes = new ConcurrentHashMap<>();

    public NetworkNodeManager(String name, World world) {
        super(name);

        this.world = world;
    }

    @Override
    public void load(CompoundNBT tag) {
        if (tag.contains(NBT_NODES)) {
            ListNBT nodesTag = tag.getList(NBT_NODES, Constants.NBT.TAG_COMPOUND);

            this.nodes.clear();

            for (int i = 0; i < nodesTag.size(); ++i) {
                CompoundNBT nodeTag = nodesTag.getCompound(i);

                ResourceLocation id = new ResourceLocation(nodeTag.getString(NBT_NODE_ID));
                CompoundNBT data = nodeTag.getCompound(NBT_NODE_DATA);
                BlockPos pos = BlockPos.of(nodeTag.getLong(NBT_NODE_POS));

                INetworkNodeFactory factory = API.instance().getNetworkNodeRegistry().get(id);

                if (factory != null) {
                    INetworkNode node = null;

                    try {
                        node = factory.create(data, world, pos);
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
    public CompoundNBT save(CompoundNBT tag) {
        ListNBT list = new ListNBT();

        for (INetworkNode node : all()) {
            try {
                CompoundNBT nodeTag = new CompoundNBT();

                nodeTag.putString(NBT_NODE_ID, node.getId().toString());
                nodeTag.putLong(NBT_NODE_POS, node.getPos().asLong());
                nodeTag.put(NBT_NODE_DATA, node.write(new CompoundNBT()));

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
