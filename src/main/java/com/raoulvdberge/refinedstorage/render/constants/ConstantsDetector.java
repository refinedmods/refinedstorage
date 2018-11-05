package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsDetector {
    public static final CollisionGroup COLLISION = new CollisionGroup().addItem(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 5D / 16D, 1.0D));
}
