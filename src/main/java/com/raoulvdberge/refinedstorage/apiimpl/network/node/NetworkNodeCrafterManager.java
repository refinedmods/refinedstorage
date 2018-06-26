package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.network.MessageCrafterManagerSlotSizes;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeCrafterManager extends NetworkNode {
    public static final String ID = "crafter_manager";

    private static final String NBT_SIZE = "Size";
    private static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

    private int size = IGrid.SIZE_STRETCH;
    private int searchBoxMode = IGrid.SEARCH_BOX_MODE_NORMAL;

    public NetworkNodeCrafterManager(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.crafterManagerUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    public void sendTo(EntityPlayerMP player) {
        if (network != null) {
            RS.INSTANCE.network.sendTo(new MessageCrafterManagerSlotSizes(network.getCraftingManager().getNamedContainers()), player);
        }
    }

    public int getSize() {
        return world.isRemote ? TileCrafterManager.SIZE.getValue() : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_SIZE, size);
        tag.setInteger(NBT_SEARCH_BOX_MODE, searchBoxMode);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_SIZE)) {
            size = tag.getInteger(NBT_SIZE);
        }

        if (tag.hasKey(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInteger(NBT_SEARCH_BOX_MODE);
        }
    }

    public int getSearchBoxMode() {
        return world.isRemote ? TileCrafterManager.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
