package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;

public class NetworkNodeCable extends NetworkNode {
    public static final String ID = "cable";

    public NetworkNodeCable(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        if (IntegrationMCMP.isLoaded() && direction != null) {
            return RSMCMPAddon.hasConnectionWith(world.getTileEntity(pos), Collections.singletonList(BlockCable.getCableExtensionAABB(direction)))
                && RSMCMPAddon.hasConnectionWith(world.getTileEntity(pos.offset(direction)), Collections.singletonList(BlockCable.getCableExtensionAABB(direction.getOpposite())));
        }

        return true;
    }
}
