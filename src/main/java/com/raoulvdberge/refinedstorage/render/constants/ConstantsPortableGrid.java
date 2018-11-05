package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsPortableGrid {
    public static final CollisionGroup COLLISION = new CollisionGroup().addItem(new AxisAlignedBB(0, 0, 0, 1, 13.2F / 16F, 1));
}
