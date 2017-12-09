package com.raoulvdberge.refinedstorage.integration.funkylocomotion;

import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.BlockNode;
import com.rwtema.funkylocomotion.api.FunkyRegistry;
import net.minecraft.block.Block;

import java.util.List;

public class MoveFactoryRegisterer {
    public static void register(List<BlockBase> blocks) {
        for (Block block : blocks) {
            if (block instanceof BlockNode) {
                FunkyRegistry.INSTANCE.registerMoveFactoryBlock(block, new MoveFactoryNetworkNode());
            }
        }
    }
}
