package refinedstorage.api.storage.fluid;

import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.item.IItemStorageProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * This holds all fluids from all the connected storages from a {@link INetworkMaster}.
 * <p>
 * Refined Storage uses this class mainly for use in Grids and Detectors to avoid querying
 * individual {@link IFluidStorage} constantly (performance impact) and to send and detect storage changes
 * more efficiently.
 */
public interface IGroupedFluidStorage {
    /**
     * Rebuilds the global fluid list. Typically called when a {@link IItemStorageProvider} is added or removed from the network.
     */
    void rebuild();

    /**
     * Adds an item to the global fluid list.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the global fluid list.
     * Use {@link INetworkMaster#insertFluid(FluidStack, int, boolean)} to add a fluid to an actual storage.
     * <p>
     * Will merge it with another fluid if it already exists.
     *
     * @param stack      The fluid to add, do NOT modify
     * @param rebuilding Whether this method is called while the storage is rebuilding
     */
    void add(@Nonnull FluidStack stack, boolean rebuilding);

    /**
     * Removes a fluid from the global fluid list.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the global fluid list.
     * Use {@link INetworkMaster#extractFluid(FluidStack, int, int)} to remove an fluid from an actual storage.
     *
     * @param stack The fluid to remove, do NOT modify
     */
    void remove(@Nonnull FluidStack stack);

    /**
     * Gets a fluid from the network.
     *
     * @param stack The stack to find
     * @param flags The flags to compare on, see {@link CompareUtils}
     * @return Null if no fluid is found, or the {@link FluidStack}, do NOT modify
     */
    @Nullable
    FluidStack get(@Nonnull FluidStack stack, int flags);

    /**
     * Gets a fluid from the network by hash, see {@link refinedstorage.api.network.NetworkUtils#getFluidStackHashCode(FluidStack)}.
     *
     * @return Null if no fluid is found matching the hash, or the {@link FluidStack}, do NOT modify
     */
    @Nullable
    FluidStack get(int hash);

    /**
     * @return All fluids in this storage network
     */
    Collection<FluidStack> getStacks();

    /**
     * @return The fluid storages connected to this network
     */
    List<IFluidStorage> getStorages();
}
