package com.raoulvdberge.refinedstorage.render.collision.constants;

import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;

public final class ConstantsImporter {
    public static final AxisAlignedBB LINE_NORTH_1_AABB = RenderUtils.getBounds(6, 6, 4, 10, 10, 6);
    public static final AxisAlignedBB LINE_NORTH_2_AABB = RenderUtils.getBounds(5, 5, 2, 11, 11, 4);
    public static final AxisAlignedBB LINE_NORTH_3_AABB = RenderUtils.getBounds(3, 3, 0, 13, 13, 2);
    public static final CollisionGroup LINE_NORTH = new CollisionGroup().addItem(LINE_NORTH_1_AABB).addItem(LINE_NORTH_2_AABB).addItem(LINE_NORTH_3_AABB).setCanAccessGui(true);

    public static final AxisAlignedBB LINE_EAST_1_AABB = RenderUtils.getBounds(10, 6, 6, 12, 10, 10);
    public static final AxisAlignedBB LINE_EAST_2_AABB = RenderUtils.getBounds(12, 5, 5, 14, 11, 11);
    public static final AxisAlignedBB LINE_EAST_3_AABB = RenderUtils.getBounds(14, 3, 3, 16, 13, 13);
    public static final CollisionGroup LINE_EAST = new CollisionGroup().addItem(LINE_EAST_1_AABB).addItem(LINE_EAST_2_AABB).addItem(LINE_EAST_3_AABB).setCanAccessGui(true);

    public static final AxisAlignedBB LINE_SOUTH_1_AABB = RenderUtils.getBounds(6, 6, 10, 10, 10, 12);
    public static final AxisAlignedBB LINE_SOUTH_2_AABB = RenderUtils.getBounds(5, 5, 12, 11, 11, 14);
    public static final AxisAlignedBB LINE_SOUTH_3_AABB = RenderUtils.getBounds(3, 3, 14, 13, 13, 16);
    public static final CollisionGroup LINE_SOUTH = new CollisionGroup().addItem(LINE_SOUTH_1_AABB).addItem(LINE_SOUTH_2_AABB).addItem(LINE_SOUTH_3_AABB).setCanAccessGui(true);

    public static final AxisAlignedBB LINE_WEST_1_AABB = RenderUtils.getBounds(4, 6, 6, 6, 10, 10);
    public static final AxisAlignedBB LINE_WEST_2_AABB = RenderUtils.getBounds(2, 5, 5, 4, 11, 11);
    public static final AxisAlignedBB LINE_WEST_3_AABB = RenderUtils.getBounds(0, 3, 3, 2, 13, 13);
    public static final CollisionGroup LINE_WEST = new CollisionGroup().addItem(LINE_WEST_1_AABB).addItem(LINE_WEST_2_AABB).addItem(LINE_WEST_3_AABB).setCanAccessGui(true);

    public static final AxisAlignedBB LINE_UP_1_AABB = RenderUtils.getBounds(6, 10, 6, 10, 12, 10);
    public static final AxisAlignedBB LINE_UP_2_AABB = RenderUtils.getBounds(5, 12, 5, 11, 14, 11);
    public static final AxisAlignedBB LINE_UP_3_AABB = RenderUtils.getBounds(3, 14, 3, 13, 16, 13);
    public static final CollisionGroup LINE_UP = new CollisionGroup().addItem(LINE_UP_1_AABB).addItem(LINE_UP_2_AABB).addItem(LINE_UP_3_AABB).setCanAccessGui(true);

    public static final AxisAlignedBB LINE_DOWN_1_AABB = RenderUtils.getBounds(6, 4, 6, 10, 6, 10);
    public static final AxisAlignedBB LINE_DOWN_2_AABB = RenderUtils.getBounds(5, 2, 5, 11, 4, 11);
    public static final AxisAlignedBB LINE_DOWN_3_AABB = RenderUtils.getBounds(3, 0, 3, 13, 2, 13);
    public static final CollisionGroup LINE_DOWN = new CollisionGroup().addItem(LINE_DOWN_1_AABB).addItem(LINE_DOWN_2_AABB).addItem(LINE_DOWN_3_AABB).setCanAccessGui(true);
}
