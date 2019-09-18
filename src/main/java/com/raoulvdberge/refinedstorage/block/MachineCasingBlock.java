package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;

public class MachineCasingBlock extends Block {
    public MachineCasingBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "machine_casing");
    }
}
