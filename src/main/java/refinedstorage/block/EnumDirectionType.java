package refinedstorage.block;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public enum EnumDirectionType {
    ANY(
        EnumFacing.VALUES
    ),
    HORIZONTAL(
        EnumFacing.NORTH,
        EnumFacing.EAST,
        EnumFacing.SOUTH,
        EnumFacing.WEST
    );

    public final EnumFacing[] allowed;

    EnumDirectionType(EnumFacing... allowed) {
        this.allowed = allowed;
    }

    EnumFacing getFrom(BlockPos pos, EntityLivingBase entity) {
        switch (this) {
            case ANY:
                return BlockPistonBase.getFacingFromEntity(pos, entity);
            case HORIZONTAL:
                return entity.getHorizontalFacing().getOpposite();
            default:
                return null;
        }
    }

    EnumFacing getNext(EnumFacing previous) {
        switch (this) {
            case ANY:
                return previous.ordinal() + 1 >= EnumFacing.VALUES.length ? EnumFacing.VALUES[0] : EnumFacing.VALUES[previous.ordinal() + 1];
            case HORIZONTAL:
                return previous.rotateYCCW();
            default:
                return previous;
        }
    }
}
