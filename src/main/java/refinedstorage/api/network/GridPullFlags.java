package refinedstorage.api.network;

public class GridPullFlags {
    public static final int PULL_HALF = 1;
    public static final int PULL_ONE = 2;
    public static final int PULL_SHIFT = 4;

    public static boolean isPullingHalf(int flags) {
        return (flags & PULL_HALF) == PULL_HALF;
    }

    public static boolean isPullingOne(int flags) {
        return (flags & PULL_ONE) == PULL_ONE;
    }

    public static boolean isPullingWithShift(int flags) {
        return (flags & PULL_SHIFT) == PULL_SHIFT;
    }
}
