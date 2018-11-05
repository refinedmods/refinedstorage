package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsWirelessTransmitter {
    // @Volatile: From BlockTorch
    public static final CollisionGroup COLLISION = new CollisionGroup().addItem(new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D));
}
