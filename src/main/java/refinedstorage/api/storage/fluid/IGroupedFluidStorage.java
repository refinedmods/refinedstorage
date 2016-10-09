package refinedstorage.api.storage.fluid;

import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IFluidStackList;

import javax.annotation.Nonnull;
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
     * Rebuilds the global fluid list.
     * Typically called when a {@link IFluidStorageProvider} is added or removed from the network.
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
     * @param stack      the stack to add, do NOT modify
     * @param rebuilding whether this method is called while the storage is rebuilding
     */
    void add(@Nonnull FluidStack stack, boolean rebuilding);

    /**
     * Removes a fluid from the global fluid list.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the global fluid list.
     * Use {@link INetworkMaster#extractFluid(FluidStack, int, int)} to remove an fluid from an actual storage.
     *
     * @param stack the fluid to remove, do NOT modify
     */
    void remove(@Nonnull FluidStack stack);

    /**
     * @return the list behind this grouped storage
     */
    IFluidStackList getList();

    /**
     * @return the fluid storages connected to this network
     */
    List<IFluidStorage> getStorages();
}
