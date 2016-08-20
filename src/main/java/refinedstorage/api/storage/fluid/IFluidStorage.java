package refinedstorage.api.storage.fluid;

import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.item.IItemStorageProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a fluid storage sink for the storage network.
 * Provide this through an {@link IItemStorageProvider}.
 */
public interface IFluidStorage {
    /**
     * @return Fluids stored in this storage
     */
    List<FluidStack> getStacks();

    /**
     * Inserts a fluid to this storage.
     *
     * @param stack    The fluid prototype to insert, do NOT modify
     * @param size     The amount of that prototype that has to be inserted
     * @param simulate If we are simulating
     * @return null if the insert was successful, or a {@link FluidStack} with the remainder
     */
    @Nullable
    FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate);

    /**
     * Extracts a fluid from this storage.
     * <p>
     * If the fluid we found in the system is smaller than the requested size, return that fluid anyway.
     *
     * @param stack A prototype of the fluid to extract, do NOT modify
     * @param size  The amount of that fluid that has to be extracted
     * @param flags On what we are comparing to extract this fluid, see {@link CompareUtils}
     * @return null if we didn't extract anything, or an {@link FluidStack} with the result
     */
    @Nullable
    FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags);

    /**
     * @return The amount of fluids stored in this storage
     */
    int getStored();

    /**
     * @return The priority of this storage
     */
    int getPriority();
}
