package com.raoulvdberge.refinedstorage.render.constants;

import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsExporter {
    public static final AxisAlignedBB LINE_NORTH_1_AABB = RenderUtils.getBounds(6, 6, 0, 10, 10, 2);
    public static final AxisAlignedBB LINE_NORTH_2_AABB = RenderUtils.getBounds(5, 5, 2, 11, 11, 4);
    public static final AxisAlignedBB LINE_NORTH_3_AABB = RenderUtils.getBounds(3, 3, 4, 13, 13, 6);
    public static final AxisAlignedBB LINE_EAST_1_AABB = RenderUtils.getBounds(14, 6, 6, 16, 10, 10);
    public static final AxisAlignedBB LINE_EAST_2_AABB = RenderUtils.getBounds(12, 5, 5, 14, 11, 11);
    public static final AxisAlignedBB LINE_EAST_3_AABB = RenderUtils.getBounds(10, 3, 3, 12, 13, 13);
    public static final AxisAlignedBB LINE_SOUTH_1_AABB = RenderUtils.getBounds(6, 6, 14, 10, 10, 16);
    public static final AxisAlignedBB LINE_SOUTH_2_AABB = RenderUtils.getBounds(5, 5, 12, 11, 11, 14);
    public static final AxisAlignedBB LINE_SOUTH_3_AABB = RenderUtils.getBounds(3, 3, 10, 13, 13, 12);
    public static final AxisAlignedBB LINE_WEST_1_AABB = RenderUtils.getBounds(0, 6, 6, 2, 10, 10);
    public static final AxisAlignedBB LINE_WEST_2_AABB = RenderUtils.getBounds(2, 5, 5, 4, 11, 11);
    public static final AxisAlignedBB LINE_WEST_3_AABB = RenderUtils.getBounds(4, 3, 3, 6, 13, 13);
    public static final AxisAlignedBB LINE_UP_1_AABB = RenderUtils.getBounds(6, 14, 6, 10, 16, 10);
    public static final AxisAlignedBB LINE_UP_2_AABB = RenderUtils.getBounds(5, 12, 5, 11, 14, 11);
    public static final AxisAlignedBB LINE_UP_3_AABB = RenderUtils.getBounds(3, 10, 3, 13, 12, 13);
    public static final AxisAlignedBB LINE_DOWN_1_AABB = RenderUtils.getBounds(6, 0, 6, 10, 2, 10);
    public static final AxisAlignedBB LINE_DOWN_2_AABB = RenderUtils.getBounds(5, 2, 5, 11, 4, 11);
    public static final AxisAlignedBB LINE_DOWN_3_AABB = RenderUtils.getBounds(3, 4, 3, 13, 6, 13);
}
