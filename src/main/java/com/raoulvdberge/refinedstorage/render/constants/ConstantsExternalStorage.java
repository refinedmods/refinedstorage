package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsExternalStorage {
    public static final AxisAlignedBB HEAD_NORTH_AABB = RenderUtils.getBounds(3, 3, 0, 13, 13, 2);
    public static final AxisAlignedBB HEAD_EAST_AABB = RenderUtils.getBounds(14, 3, 3, 16, 13, 13);
    public static final AxisAlignedBB HEAD_SOUTH_AABB = RenderUtils.getBounds(3, 3, 14, 13, 13, 16);
    public static final AxisAlignedBB HEAD_WEST_AABB = RenderUtils.getBounds(0, 3, 3, 2, 13, 13);
    public static final AxisAlignedBB HEAD_UP_AABB = RenderUtils.getBounds(3, 14, 3, 13, 16, 13);
    public static final AxisAlignedBB HEAD_DOWN_AABB = RenderUtils.getBounds(3, 0, 3, 13, 2, 13);
}
