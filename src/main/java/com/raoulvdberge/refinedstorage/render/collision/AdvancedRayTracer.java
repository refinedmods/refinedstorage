package com.raoulvdberge.refinedstorage.render.collision;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Collection;

public final class AdvancedRayTracer {
    public static Vec3d getStart(EntityPlayer player) {
        return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
    }

    public static Vec3d getEnd(EntityPlayer player) {
        double reachDistance = player instanceof EntityPlayerMP ? player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : (player.capabilities.isCreativeMode ? 5.0D : 4.5D);

        Vec3d lookVec = player.getLookVec();
        Vec3d start = getStart(player);

        return start.add(lookVec.x * reachDistance, lookVec.y * reachDistance, lookVec.z * reachDistance);
    }

    @Nullable
    public static AdvancedRayTraceResult<RayTraceResult> rayTrace(BlockPos pos, Vec3d start, Vec3d end, Collection<CollisionGroup> groups) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;

        for (CollisionGroup group : groups) {
            for (AxisAlignedBB aabb : group.getItems()) {
                AdvancedRayTraceResult result = rayTrace(pos, start, end, aabb, i, group);

                if (result != null) {
                    double d = result.squareDistanceTo(start);

                    if (d < minDistance) {
                        minDistance = d;
                        hit = result;
                    }
                }

                i++;
            }
        }

        return hit;
    }

    public static AdvancedRayTraceResult<RayTraceResult> rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB bounds, int subHit, CollisionGroup group) {
        RayTraceResult result = bounds.offset(pos).calculateIntercept(start, end);

        if (result == null) {
            return null;
        }

        result = new RayTraceResult(RayTraceResult.Type.BLOCK, result.hitVec, result.sideHit, pos);
        result.subHit = subHit;

        return new AdvancedRayTraceResult<>(group, bounds, result);
    }
}
