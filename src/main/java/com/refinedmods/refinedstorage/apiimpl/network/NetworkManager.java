package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.apiimpl.util.RSWorldSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager extends RSWorldSavedData implements INetworkManager {
    public static final String NAME = "refinedstorage_networks";

    private static final String NBT_NETWORKS = "Networks";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_DATA = "Data";
    private static final String NBT_POS = "Pos";

    private final Level world;

    private final Logger logger = LogManager.getLogger(getClass());

    private final ConcurrentHashMap<BlockPos, INetwork> networks = new ConcurrentHashMap<>();

    public NetworkManager(Level world) {
        this.world = world;
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains(NBT_NETWORKS)) {
            ListTag networksTag = tag.getList(NBT_NETWORKS, Tag.TAG_COMPOUND);

            this.networks.clear();

            for (int i = 0; i < networksTag.size(); ++i) {
                CompoundTag networkTag = networksTag.getCompound(i);

                CompoundTag data = networkTag.getCompound(NBT_DATA);
                BlockPos pos = BlockPos.of(networkTag.getLong(NBT_POS));
                int type = networkTag.getInt(NBT_TYPE);

                INetwork network = new Network(world, pos, NetworkType.values()[type]);

                try {
                    network = network.readFromNbt(data);
                } catch (Throwable t) {
                    logger.error("Error while reading network", t);
                }

                this.networks.put(pos, network);
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (INetwork network : all()) {
            try {
                CompoundTag networkTag = new CompoundTag();

                networkTag.putLong(NBT_POS, network.getPosition().asLong());
                networkTag.put(NBT_DATA, network.writeToNbt(new CompoundTag()));
                networkTag.putInt(NBT_TYPE, network.getType().ordinal());

                list.add(networkTag);
            } catch (Throwable t) {
                logger.error("Error while saving network", t);
            }
        }

        tag.put(NBT_NETWORKS, list);

        return tag;
    }

    @Nullable
    @Override
    public INetwork getNetwork(BlockPos pos) {
        return networks.get(pos);
    }

    @Override
    public void removeNetwork(BlockPos pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        networks.remove(pos);
    }

    @Override
    public void setNetwork(BlockPos pos, INetwork network) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        if (network == null) {
            throw new IllegalArgumentException("Network cannot be null");
        }

        networks.put(pos, network);
    }

    @Override
    public Collection<INetwork> all() {
        return networks.values();
    }

    @Override
    public void markForSaving() {
        setDirty();
    }
}
