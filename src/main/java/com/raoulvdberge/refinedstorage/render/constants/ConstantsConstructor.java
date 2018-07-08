package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsConstructor {
    public static final AxisAlignedBB HEAD_NORTH_AABB = RenderUtils.getBounds(2, 2, 0, 14, 14, 2);
    public static final AxisAlignedBB HEAD_EAST_AABB = RenderUtils.getBounds(14, 2, 2, 16, 14, 14);
    public static final AxisAlignedBB HEAD_SOUTH_AABB = RenderUtils.getBounds(2, 2, 14, 14, 14, 16);
    public static final AxisAlignedBB HEAD_WEST_AABB = RenderUtils.getBounds(0, 2, 2, 2, 14, 14);
    public static final AxisAlignedBB HEAD_DOWN_AABB = RenderUtils.getBounds(2, 0, 2, 14, 2, 14);
    public static final AxisAlignedBB HEAD_UP_AABB = RenderUtils.getBounds(2, 14, 2, 14, 16, 14);
}
