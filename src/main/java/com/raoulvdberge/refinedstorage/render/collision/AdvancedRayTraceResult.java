package com.raoulvdberge.refinedstorage.render.collision;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AdvancedRayTraceResult<T extends RayTraceResult> {
    private CollisionGroup group;
    private AxisAlignedBB bounds;
    private T hit;

    public AdvancedRayTraceResult(CollisionGroup group, AxisAlignedBB bounds, T hit) {
        this.group = group;
        this.bounds = bounds;
        this.hit = hit;
    }

    public boolean valid() {
        return hit != null && bounds != null;
    }

    public double squareDistanceTo(Vec3d vec) {
        return hit.hitVec.squareDistanceTo(vec);
    }

    public CollisionGroup getGroup() {
        return group;
    }

    public AxisAlignedBB getBounds() {
        return bounds;
    }

    public T getHit() {
        return hit;
    }
}