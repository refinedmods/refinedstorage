package refinedstorage.block;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;

public enum EnumPlacementType {
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

    EnumPlacementType(EnumFacing... allowed) {
        this.allowed = allowed;
    }

    EnumFacing getFrom(EnumFacing facing, EntityLivingBase entity) {
        switch (this) {
            case ANY:
                return facing.getOpposite();
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
