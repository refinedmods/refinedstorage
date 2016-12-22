package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import java.util.function.Function;

public class WorldSavedDataNetworkNode extends WorldSavedData {
    private static final String NAME = "refinedstorage_nodes";

    private static final String NBT_NODES = "Nodes";
    private static final String NBT_NODE_ID = "Id";
    private static final String NBT_NODE_DATA = "Data";
    private static final String NBT_NODE_POS = "Pos";
    private static final String NBT_DIMENSION = "Dimension";

    private int dimension;

    public WorldSavedDataNetworkNode(int dimension) {
        super(NAME);

        this.dimension = dimension;
    }

    public WorldSavedDataNetworkNode(String s) {
        super(s);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_NODES) && tag.hasKey(NBT_DIMENSION)) {
            int dimension = tag.getInteger(NBT_DIMENSION);

            NBTTagList list = tag.getTagList(NBT_NODES, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound nodeTag = list.getCompoundTagAt(i);

                String id = nodeTag.getString(NBT_NODE_ID);
                NBTTagCompound data = nodeTag.getCompoundTag(NBT_NODE_DATA);
                BlockPos pos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS));

                Function<NBTTagCompound, INetworkNode> factory = API.instance().getNetworkNodeRegistry().get(id);

                if (factory != null) {
                    API.instance().getNetworkNodeProvider(dimension).setNode(pos, factory.apply(data));
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (INetworkNode node : API.instance().getNetworkNodeProvider(dimension).all()) {
            NBTTagCompound nodeTag = new NBTTagCompound();

            nodeTag.setString(NBT_NODE_ID, node.getId());
            nodeTag.setLong(NBT_NODE_POS, node.getPos().toLong());
            nodeTag.setTag(NBT_NODE_DATA, node.write(new NBTTagCompound()));

            list.appendTag(nodeTag);
        }

        tag.setTag(NBT_NODES, list);

        tag.setInteger(NBT_DIMENSION, dimension);

        return tag;
    }

    public static WorldSavedDataNetworkNode get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        WorldSavedDataNetworkNode instance = (WorldSavedDataNetworkNode) storage.getOrLoadData(WorldSavedDataNetworkNode.class, NAME);

        if (instance == null) {
            instance = new WorldSavedDataNetworkNode(world.provider.getDimension());

            storage.setData(NAME, instance);
        }

        return instance;
    }
}
